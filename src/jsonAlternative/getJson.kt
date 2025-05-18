package jsonAlternative

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.URI

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import jsonAlternative.convertToJson
import kotlin.reflect.full.instanceParameter

@Target(AnnotationTarget.CLASS,AnnotationTarget.FUNCTION,AnnotationTarget.VALUE_PARAMETER)
annotation class Mapping(val path: String = "")

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class PathParam(val name: String = "")

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class QueryParam(val name: String = "")

class GetJson(private vararg val controllers: KClass<*>) {
    private val router = Router(controllers.map { it.createInstance() })

    fun start(port: Int) {

        val server = HttpServer.create(InetSocketAddress(port), 0)
        server.createContext("/") { handleRequest(it) }
        server.executor = null
        server.start()
        println("Server started on port $port")
    }

    private fun handleRequest(exchange: HttpExchange) {

        try {
            println("Iniciar Request")
            val response = router.handle(exchange.requestURI)
            exchange.sendResponseHeaders(200, response.toByteArray().size.toLong())
            exchange.responseBody.use { it.write(response.toByteArray()) }
        } catch (e: Exception) {
            println("Falha Request")
            exchange.sendResponseHeaders(500, 0)
            exchange.responseBody.use { it.write(e.message?.toByteArray() ?: byteArrayOf()) }
        }
    }
}

//private
//Verificar se o conteudo de 1 mapping não existe controladores com o mesmo nome

class Router(controllers: List<Any>) {
    private val routes = mutableMapOf<String, Route>()

    init {
        controllers.forEach { registerController(it) }
    }

    private fun registerController(controller: Any) {
        val basePath = controller::class.findAnnotation<Mapping>()?.path ?: ""

        controller::class.memberFunctions.forEach { func ->
            func.findAnnotation<Mapping>()?.let { mapping ->
                val fullPath = listOf(basePath, mapping.path)
                    .filter { it.isNotEmpty() }
                    .joinToString("/")
                    .replace("//", "/")

                routes[fullPath] = Route(controller, func, fullPath)
                println(fullPath)
            }
        }
    }

    fun handle(uri: URI): String {

        val path = uri.path.drop(1)
        println("Caminho a pedir: $path")

        val route = routes.entries.find { entry ->
            val pattern = entry.key.replace("{var}", ".*").toRegex()
            pattern.matches(path)
        }?.value ?: throw IllegalArgumentException("Rota não encontrada: $path")
        println(route.path)

        val pathParams = extractPathParams(uri, route)
        val queryParams = parseQueryParams(uri.query)

        val args = prepareArguments(route.function, pathParams, queryParams,route.controller)

        val result = route.invoke(args)

        return convertToJson(result).toJson()
    }

    private fun extractPathParams(uri: URI, route: Route): Map<String, String> {
        println("Procurando parâmetros")
        val pathSegments = uri.path.split('/').filter { it.isNotEmpty() }
        val routeSegments = route.path.split('/').filter { it.isNotEmpty() }

        return routeSegments.zip(pathSegments)
            .filter { it.first.startsWith("{") }
            .associate {
                it.first.removeSurrounding("{", "}") to it.second
            }
    }

    private fun parseQueryParams(query: String?): Map<String, String> {
        return query?.split("&")?.associate {
            val (key, value) = it.split("=")
            key to value
        } ?: emptyMap()
    }

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
                            String::class -> value
                            else -> value
                        }
                    } else null
                }
                else -> null
            }
        }.toTypedArray()
    }

    private fun convertType(value: String?, type: KClass<*>): Any? {
        return when (type) {
            String::class -> value
            Int::class -> value?.toIntOrNull()
            Double::class -> value?.toDoubleOrNull()
            Boolean::class -> value?.toBoolean()
            List::class -> value?.split(",")?.map { it.trim() }
            else -> {
                // Caso o tipo não seja um tipo primitivo ou uma lista, tentamos criar uma instância do tipo
                try {
                    if (value.isNullOrEmpty()) {
                        null
                    } else {
                        // Tentando converter para um tipo personalizado (se houver algum tipo especializado)
                        val constructor = type.constructors.firstOrNull()
                        constructor?.call(value)
                    }
                } catch (e: Exception) {
                    println("Erro ao tentar converter valor '$value' para o tipo ${type.simpleName}")
                    null
                }
            }
        }
    }
}

private class Route(
    val controller: Any,
    val function: KFunction<*>,
    val path: String = ""
) {
    fun invoke(args: Array<Any?>): Any? {
        val params = function.parameters.zip(args).toMap()
        return function.callBy(params)
    }
}
