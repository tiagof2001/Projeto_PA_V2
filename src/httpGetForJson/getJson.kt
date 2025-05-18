package httpGetForJson

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import httpGetForJson.annotationList.Mapping
import httpGetForJson.annotationList.PathParam
import httpGetForJson.annotationList.QueryParam
import java.net.InetSocketAddress
import java.net.URI

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import convertObjectToJson.convertToJson

/**
 * Inicializa e arranca um servidor HTTP simples que encaminha pedidos HTTP para os endpoints apropriados com base
 * no conjunto de controladores fornecido. Cada controlador define mapeamentos de rotas HTTP via anotações.
 *
 * @constructor Cria uma instância de `GetJson` com os controladores indicados.
 * @param controllers Parâmetro vararg para classes que definem mapeamentos de rotas HTTP.
 *
 * Esta classe utiliza uma instância de `Router` para gerir o mapeamento e processamento dos pedidos HTTP
 * para as rotas apropriadas definidas nos controladores. Ao ser instanciada e iniciada,
 * configura um servidor HTTP que escuta na porta especificada e encaminha os pedidos recebidos.
 *
 * O servidor trata exceções no encaminhamento ou processamento dos pedidos e responde com os códigos de estado HTTP apropriados:
 * `200` para respostas bem-sucedidas, `404` quando a rota não é encontrada e
 * `500` para erros internos do servidor.
 *
 * Consulte a classe `Router` para detalhes sobre como é feita a resolução de rotas e a interação com os controladores.
 */


class GetJson(private vararg val controllers: KClass<*>) {

    /**
     * Representa uma instância da classe `Router` inicializada com uma lista de instâncias de controladores.
     * Esta variável `router` funciona como router central para mapear pedidos HTTP para as funções apropriadas
     * dos controladores, com base em rotas predefinidas.
     *
     * Os controladores são instanciados dinamicamente através do método `createInstance` e passados ao
     * construtor da classe `Router`. A classe `Router` é responsável por registar as rotas definidas pela
     * anotação `Mapping` em cada controlador e resolver os URIs dos pedidos HTTP para essas rotas.
     *
     * É utilizada principalmente na classe `GetJson` para processar pedidos HTTP através do método `handleRequest`.
     */

    private val router = Router(controllers.map { it.createInstance() })

    /**
     * Inicia um servidor HTTP na porta especificada.
     *
     * O servidor é inicializado com um único contexto a processar pedidos através do
     * método `handleRequest`. Os pedidos são processados de forma síncrona na
     * thread que invoca o servidor.
     *
     * @param port Número da porta na qual o servidor irá escutar pedidos HTTP recebidos.
     */

    fun start(port: Int) {

        val server = HttpServer.create(InetSocketAddress(port), 0)
        server.createContext("/") { handleRequest(it) }
        server.executor = null
        server.start()
    }

    /**
     * Processa um pedido HTTP recebido, delegando-o ao router e fornecendo uma resposta ao cliente.
     *
     * Este método processa o URI contido no pedido utilizando o router para determinar
     * a resposta apropriada. Em caso de sucesso, é enviado um código de estado HTTP 200 juntamente
     * com o conteúdo da resposta. Se for lançada uma exceção durante o processamento do URI,
     * responde com um código de estado 404 e a mensagem da exceção.
     *
     * @param exchange O objeto HTTP exchange que contém os detalhes do pedido e da resposta.
     */


    private fun handleRequest(exchange: HttpExchange) {

        try {
            val response = router.handle(exchange.requestURI)
            exchange.sendResponseHeaders(200, response.toByteArray().size.toLong())
            exchange.responseBody.use { it.write(response.toByteArray()) }
        } catch (e: Exception) {
            exchange.sendResponseHeaders(404, 0)
            exchange.responseBody.use { it.write(e.message?.toByteArray() ?: byteArrayOf()) }
        }
    }
}

/**
 * A class responsible for managing and handling routing logic by mapping URI paths
 * to their respective controller methods. It enables the invocation of appropriate
 * controller functions based on the requested URI.
 *
 * @param controllers A list of controller instances, each annotated with a base path
 * using the `@Mapping` annotation. Each controller is processed to register its methods
 * as routes.
 */


class Router(controllers: List<Any>) {
    /**
     * Um mapeamento de caminhos URI para os respetivos objetos `Route`.
     *
     * Este contentor armazena todas as rotas registadas na classe `Router`,
     * onde cada rota está associada a uma chave de caminho única. Os objetos `Route`
     * fornecem a lógica para invocar a função de controlador apropriada para um determinado URI.
     *
     * O mapa `routes` é preenchido quando os controladores são registados
     * através do método `registerController`. Cada chave representa um
     * caminho com possíveis parâmetros de caminho (por exemplo, `/users/{id}`), e o
     * valor correspondente é uma instância de `Route` que contém o controlador associado,
     * a função e o caminho completo.
     *
     * Este mapa é utilizado pelo método `handle` para resolver
     * e invocar a rota apropriada com base no URI fornecido.
     */

    private val routes = mutableMapOf<String, Route>()

    /**
     * Inicializa o Router registando todos os controladores fornecidos.
     * Verifica se existem duplicados nos caminhos-base dos controladores e regista os mapeamentos
     * das funções de cada controlador.
     *
     * @throws IllegalArgumentException se forem encontrados controladores com caminhos-base duplicados
     */

    init {
        val basePathCounts = controllers.mapNotNull { it::class.findAnnotation<Mapping>()?.path }
            .groupingBy{ it }.eachCount()

        val duplicateBasePaths = basePathCounts.filter { it.value > 1 }

        if (duplicateBasePaths.isNotEmpty()) {
            throw IllegalArgumentException("Existem controladores com o mesmo path base: ${duplicateBasePaths.keys}")
        }
        controllers.forEach { registerController(it) }
    }

    /**
     * Regista um controlador e as suas funções anotadas no sistema de routing, mapeando os respetivos caminhos.
     *
     * @param controller Um objeto que representa o controlador e que contém métodos anotados com `@Mapping`.
     */

    private fun registerController(controller: Any) {
        val basePath = controller::class.findAnnotation<Mapping>()?.path ?: ""

        controller::class.memberFunctions.forEach { func ->
            func.findAnnotation<Mapping>()?.let { mapping ->
                val fullPath = listOf(basePath, mapping.path)
                    .filter { it.isNotEmpty() }
                    .joinToString("/")
                    .replace("//", "/")

                routes[fullPath] = Route(controller, func, fullPath)
            }
        }
    }

    /**
     * Processa um determinado URI e determina a rota correspondente a executar com base
     * nas regras de routing predefinidas. Extrai os parâmetros de caminho e de query do URI,
     * invoca a função associada com os argumentos apropriados e converte o resultado
     * para o formato de string JSON.
     *
     * @param uri O objeto URI que representa o pedido. Contém o caminho e os parâmetros
     *            de query necessários para determinar a rota correspondente e os seus parâmetros.
     * @return Uma string JSON resultante da execução da função da rota mapeada
     *         correspondente ao URI.
     * @throws IllegalArgumentException Se não for encontrada nenhuma rota correspondente para o caminho do URI fornecido.
     */

    fun handle(uri: URI): String {

        val path = uri.path.drop(1)

        val route = routes.entries
            .sortedByDescending { it.key.count { char -> char == '/' } } // Prioriza rotas mais específicas
            .find { entry ->
                val pattern = entry.key.replace(Regex("\\{[^/]+\\}"), "([^/]+)").toRegex()
                pattern.matches(path)
            }?.value ?: throw IllegalArgumentException("Rota não encontrada: $path")

        val pathParams = extractPathParams(uri, route)
        val queryParams = parseQueryParams(uri.query)

        val args = prepareArguments(route.function, pathParams, queryParams,route.controller)
        val result = route.invoke(args)

        return result.convertToJson().toJson()
    }

    /**
     * Extrai os parâmetros de caminho de um determinado URI com base na definição da rota correspondente.
     *
     * @param uri O objeto URI que contém o caminho a ser analisado para obter os parâmetros.
     * @param route O objeto route que contém o template de caminho com os placeholders para os parâmetros.
     * @return Um mapa onde as chaves são os nomes dos parâmetros e os valores são os respetivos valores dos segmentos do caminho extraídos do URI.
     */

    private fun extractPathParams(uri: URI, route: Route): Map<String, String> {
        val pathSegments = uri.path.split('/').filter { it.isNotEmpty() }
        val routeSegments = route.path.split('/').filter { it.isNotEmpty() }

        return routeSegments.zip(pathSegments)
            .filter { it.first.startsWith("{") }
            .associate {
                it.first.removeSurrounding("{", "}") to it.second
            }
    }

    /**
     * Analisa os parâmetros de query de uma string de query e converte-os para um mapa de pares chave-valor.
     *
     * @param query A string de query que contém os parâmetros no formato "chave=valor&chave2=valor2".
     *              Pode ser nula caso não existam parâmetros de query.
     * @return Um mapa onde as chaves são os nomes dos parâmetros e os valores são os respetivos valores.
     *         Se a string de query for nula, é devolvido um mapa vazio.
     */

    private fun parseQueryParams(query: String?): Map<String, String> {
        return query?.split("&")?.associate {
            val (key, value) = it.split("=")
            key to value
        } ?: emptyMap()
    }

    /**
     * Prepara os argumentos para invocar uma determinada função, mapeando os parâmetros da função
     * para as suas respetivas origens: instância, parâmetros de caminho e parâmetros de query.
     *
     * @param function A função a ser invocada, que pode incluir parâmetros anotados
     *                 com `@PathParam` ou `@QueryParam`.
     * @param pathParams Um mapa que contém os parâmetros de caminho, onde a chave é o nome do parâmetro e
     *                   o valor é o respetivo valor como string.
     * @param queryParams Um mapa que contém os parâmetros de query, onde a chave é o nome do parâmetro e
     *                    o valor é o respetivo valor como string.
     * @param controller A instância do controlador a ser passada como parâmetro de instância, se necessário.
     * @return Um array de argumentos a serem usados na invocação da função, com valores determinados
     *         a partir dos `pathParams`, `queryParams` e da instância `controller` conforme necessário.
     */

    private fun prepareArguments(
        function: KFunction<*>,
        pathParams: Map<String, String>,
        queryParams: Map<String, String>,
        controller: Any
    ): Array<Any?> {
        return function.parameters.map { parameter ->
            when {
                parameter.kind == KParameter.Kind.INSTANCE -> controller
                parameter.annotations.any { it is PathParam } -> {
                    val paramName = parameter.annotations.filterIsInstance<PathParam>().first().name
                    pathParams[paramName]
                }
                parameter.annotations.any { it is QueryParam } -> {
                    val paramName = parameter.name
                    val value = queryParams[paramName]
                    if (value != null) {
                        when (parameter.type.classifier) {
                            Int::class -> value.toIntOrNull()
                            Boolean::class -> value.toBooleanStrictOrNull()
                            String::class -> value
                            else -> value
                        }
                    } else null
                }
                else -> null
            }
        }.toTypedArray()
    }

}

/**
 * Representa um mecanismo de routing que associa uma função específica de um controlador
 * a um caminho de rota. Facilita a invocação da função associada,
 * tratando dinamicamente os parâmetros.
 *
 * @property controller A instância do controlador que contém a função a ser invocada.
 * @property function A função associada à rota, que será executada
 *                    quando a rota for correspondida.
 * @property path O template de caminho para esta rota, utilizado para corresponder os pedidos recebidos.
 */

private class Route(
    val controller: Any,
    val function: KFunction<*>,
    val path: String = ""
) {

    /**
     * Invoca a função associada com os argumentos fornecidos.
     *
     * @param args Um array de argumentos a serem passados para a função.
     *             Cada elemento do array corresponde a um parâmetro da função.
     * @return O resultado da chamada da função, ou null se a função não tiver valor de retorno.
     *
     */

    fun invoke(args: Array<Any?>): Any? {
        val params = function.parameters.zip(args).toMap()
        return function.callBy(params)
    }
}
