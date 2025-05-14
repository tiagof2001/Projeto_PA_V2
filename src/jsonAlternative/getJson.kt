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
//Verificar se o conteudo do do 1 mapping não existe controladores com o mesmo nome
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


//        val route = routes.forEach { it -> if(path.contains(it.key)) it else throw IllegalArgumentException("Rota não encontrada: $path") }

        val route = routes[path] ?: throw IllegalArgumentException("Rota não encontrada: $path")

        println(route.path)

        val pathParams = extractPathParams(uri, route)
        val queryParams = parseQueryParams(uri.query)

        println(pathParams)
        println(queryParams)
        val args = prepareArguments(route.function, pathParams, queryParams,route.controller)

//        args.forEach { it -> println(it.key.toString() +" : "+ it.value.toString()) }
        println(args.toString())
        val result = route.invoke(args)

//        println("Result -> " + result)

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
        println("Procurando Query")
        return query?.split('&')?.associate {
            val (key, value) = it.split('=', ignoreCase = true, limit = 2)
            key to value
        } ?: emptyMap()
    }

    private fun prepareArguments(
        function: KFunction<*>,
        pathParams: Map<String, String>,
        queryParams: Map<String, String>,
        controller: Any
    ): Map<KParameter, Any?> {

//        controller = route.controller
//        function = route.function

        return function.parameters.associateWith { param ->
            when {
                // Se for o parâmetro "instance" (controlador), retornamos a instância
                param.kind == KParameter.Kind.INSTANCE -> controller
                // Se for um PathParam, extraímos o valor correspondente e convertemos
                param.findAnnotation<PathParam>() != null -> {
                    val paramName = param.findAnnotation<PathParam>()?.name
                    convertType(pathParams[paramName], param.type.classifier as KClass<*>)
                }
                // Se for um QueryParam, extraímos o valor correspondente e convertemos
                param.findAnnotation<QueryParam>() != null -> {
                    val paramName = param.findAnnotation<QueryParam>()?.name
                    convertType(queryParams[paramName], param.type.classifier as KClass<*>)
                }

                else -> null // Caso não seja nenhum dos parâmetros conhecidos, retorna null
            }
        }
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
    fun invoke(args: Map<KParameter, Any?>): Any? {
        return function.callBy(args.toMap())
//                return function.callBy(args)
    }
}
