package httpGetForJson

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import convertToJson.*
import java.net.InetSocketAddress
import java.net.URI

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions

import httpGetForJson.annotationList.*


/**
 * Melhorar com o prepareArguments para anotações do tipo @queryParam
 * - Limitado a valores do tipo Int e "String"
 */
class GetJson(vararg controllers: KClass<*>, var convertion: JsonConverter) {
    private val router = Router(controllers.map { it.createInstance() },convertion)

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
            exchange.sendResponseHeaders(404, 0)
            exchange.responseBody.use { it.write(e.message?.toByteArray() ?: byteArrayOf()) }
        } catch (e: Exception) {
            exchange.sendResponseHeaders(500, 0)
            exchange.responseBody.use { it.write(e.message?.toByteArray() ?: byteArrayOf()) }
        }
    }
}


private class Router(controllers: List<Any>, var convertion: JsonConverter) {
    private val routes = mutableMapOf<String, Route>()

    init {

        val basePathCounts = controllers.mapNotNull { it::class.findAnnotation<Mapping>()?.path }
            .groupingBy{ it }.eachCount()

        val duplicateBasePaths = basePathCounts.filter { it.value > 1 }

        if (duplicateBasePaths.isNotEmpty()) {
            throw IllegalArgumentException("Existem controladores com o mesmo path base: ${duplicateBasePaths.keys}")
        }

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
            }
        }
    }

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

        //Possivel erro - ser criado nova opção de conversão

        return convertion.objectToJson(result).toJson()
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
                //Limitado a valores do tipo Int e String
                parameter.annotations.any { it is QueryParam } -> {
                    val paramName = parameter.name
                    val value = queryParams[paramName]
                    if (value != null) {
                        when (parameter.type.classifier) {
                            Number::class -> value.toIntOrNull()
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
