import convertToJson.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.collections.List

import httpGetForJson.annotationList.*
import httpGetForJson.GetJson

class TestFase3 {


    companion object {
        private lateinit var server: GetJson
        private val objectToJson = ExtendedJsonConverter(DefaultJsonConverter())

        @BeforeAll
        @JvmStatic
        fun setup() {
            server = GetJson(
                TestController::class,
                CourseController::class,
                convertion = objectToJson
            )
            server.start(8080)
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            // Implementar shutdown do servidor se necessário
        }
    }

    /**
     * Verificar se existe 2 ou mais controllers com o mesmo valor da anotação associado a classe
     * Lança um IllegalArgumentException
     */
    @Test
    fun checkControllerWithDoubleIdMapping() {
        assertThrows(IllegalArgumentException::class.java) {
            val serverTest = GetJson(TestController::class, Controller::class,convertion = objectToJson)
            serverTest.start(8090)
        }
    }

    @Test
    fun getJsonEndpointOnlyMapping() {

        val client = OkHttpClient()

        val request = Request.Builder().url("http://localhost:8080/api/ints").build()
        val response = client.newCall(request).execute()
        val result = objectToJson.objectToJson(TestController().getInts()).toJson()

        assertEquals(result, response.body?.string())

        val request2 = Request.Builder().url("http://localhost:8080/courses/sample").build()
        val response2 = client.newCall(request2).execute()
        val result2 = objectToJson.objectToJson(CourseController().getCourse()).toJson()

        assertEquals(result2, response2.body?.string())

    }


    @Test
    fun getJsonEndpointPathParam() {

        val client = OkHttpClient()
        val request = Request.Builder().url("http://localhost:8080/api/path/a").build()
        val response = client.newCall(request).execute()

        val result = objectToJson.objectToJson(TestController().getPath("a")).toJson()

        assertEquals(result, response.body?.string())
    }

    @Test
    fun getJsonEndpointQueryParam() {

        val client = OkHttpClient()
        val request = Request.Builder().url("http://localhost:8080/api/args?n=3&text=PA").build()
        val response = client.newCall(request).execute()

        val result = objectToJson.objectToJson(TestController().args(3,"PA")).toJson()
        assertEquals( result, response.body?.string())
    }

    @Test
    fun notFoundRouteReturns404() {
        val client = OkHttpClient()
        val request = Request.Builder().url("http://localhost:8080/api/test").build()
        val response = client.newCall(request).execute()
        assertEquals(404, response.code)
    }
    @Test
    fun invalidRouteReturns500() {
        val client = OkHttpClient()
        val request = Request.Builder().url("http://localhost:8080/invalid").build()
        val response = client.newCall(request).execute()
        assertEquals(404, response.code)
    }
}


// Controladores de Teste
@Mapping("api")
class TestController {
    @Mapping("ints")
    fun getInts() : List<Int> = listOf(1, 2, 3)

    @Mapping("path/{var}")
    fun getPath(@PathParam("var") param: String) = param.uppercase()

    @Mapping("args")
    fun args(
        @QueryParam n: Int,
        @QueryParam text: String
    ): Map<String, String> = mapOf(text to text.repeat(n))
}

@Mapping("courses")
class CourseController {
    @Mapping("sample")
    fun getCourse() = Course(
        "PA", 6, listOf(
            EvalItem("quizzes", 0.2, false, null)
        )
    )
}
@Mapping("api")
class Controller {
    @Mapping("ints")
    fun demo(): List<Int> = listOf(1, 2, 3)

    @Mapping("pair")
    fun obj(): Pair<String, String> = Pair("um", "dois")

    @Mapping("path/{pathvar}")
    fun path(
        @PathParam pathvar: String
    ): String = "$pathvar!"

    @Mapping("args")
    fun args(
        @QueryParam n: Int,
        @QueryParam text: String
    ): Map<String, String> = mapOf(text to text.repeat(n))
}


