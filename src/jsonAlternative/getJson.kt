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

@Target(AnnotationTarget.CLASS)
annotation class Mapping(val path: String = "")

@Target(AnnotationTarget.FUNCTION)
annotation class GetMapping(val path: String = "")

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
    }

    private fun handleRequest(exchange: HttpExchange) {
        try {
            val response = router.handle(exchange.requestURI)
            exchange.sendResponseHeaders(200, response.toByteArray().size.toLong())
            exchange.responseBody.use { it.write(response.toByteArray()) }
        } catch (e: Exception) {
            exchange.sendResponseHeaders(500, 0)
            exchange.responseBody.use { it.write(e.message?.toByteArray() ?: byteArrayOf()) }
        }
    }
}

private class Router(controllers: List<Any>) {
    private val routes = mutableMapOf<String, Route>()

    init {
        controllers.forEach { registerController(it) }
    }

    private fun registerController(controller: Any) {
        val basePath = controller::class.findAnnotation<Mapping>()?.path ?: ""

        controller::class.memberFunctions.forEach { func ->
            func.findAnnotation<GetMapping>()?.let { mapping ->
                val fullPath = listOf(basePath, mapping.path)
                    .filter { it.isNotEmpty() }
                    .joinToString("/")
                    .replace("//", "/")

                routes[fullPath] = Route(controller, func, fullPath)
            }
        }
    }

    fun handle(uri: URI): String {
        val path = uri.path
        val route = routes[path] ?: throw IllegalArgumentException("Rota não encontrada: $path")

        val pathParams = extractPathParams(uri, route)
        val queryParams = parseQueryParams(uri.query)

        val args = prepareArguments(route.function, pathParams, queryParams)
        val result = route.invoke(args)

        return convertToJson(result).toJson()
    }

    private fun extractPathParams(uri: URI, route: Route): Map<String, String> {
        val pathSegments = uri.path.split('/').filter { it.isNotEmpty() }
        val routeSegments = route.path.split('/').filter { it.isNotEmpty() }

        return routeSegments.zip(pathSegments)
            .filter { it.first.startsWith("{") }
            .associate {
                it.first.removeSurrounding("{", "}") to it.second
            }
    }

    private fun parseQueryParams(query: String?): Map<String, String> {
        return query?.split('&')?.associate {
            val (key, value) = it.split('=', ignoreCase = true, limit = 2)
            key to value
        } ?: emptyMap()
    }

    private fun prepareArguments(
        function: KFunction<*>,
        pathParams: Map<String, String>,
        queryParams: Map<String, String>
    ): Map<KParameter, Any?> {
        return function.parameters.associateWith { param ->
            when {
                param.findAnnotation<PathParam>() != null ->
                    convertType(
                        pathParams[param.findAnnotation<PathParam>()?.name],
                        param.type.classifier as KClass<*>
                    )
                param.findAnnotation<QueryParam>() != null ->
                    convertType(
                        queryParams[param.findAnnotation<QueryParam>()?.name],
                        param.type.classifier as KClass<*>
                    )
                else -> null
            }
        }
    }

    private fun convertType(value: String?, type: KClass<*>): Any? {
        return when (type) {
            String::class -> value
            Int::class -> value?.toInt()
            Double::class -> value?.toDouble()
            Boolean::class -> value?.toBoolean()
            else -> throw IllegalArgumentException("Tipo não suportado: $type")
        }
    }
}

private class Route(
    private val controller: Any,
    val function: KFunction<*>,
    val path: String = ""
) {
    fun invoke(args: Map<KParameter, Any?>): Any? {
        return function.callBy(args.toMap())
    }
}
