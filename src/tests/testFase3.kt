package tests

import httpGetForJson.*
import httpGetForJson.annotationList.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import jsonAlternative.*
import convertObjectToJson.convertToJson
import kotlin.collections.List

class TestFase3 {

    companion object {
        private lateinit var server: GetJson

        @BeforeAll
        @JvmStatic
        fun setup() {
            server = GetJson(
                TestController::class,
                CourseController::class
            )
            server.start(8080)
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            // Implementar shutdown do servidor se necessário
        }
    }

    @Test
    fun controllersWithSameUrlBase(){

        assertThrows(IllegalArgumentException::class.java) {
            val serverTest = GetJson(TestController::class, CopyController::class)
            serverTest.start(8090)
        }
    }

    // Testes Fase 3: Endpoints HTTP
    @Test
    fun getJsonEndpointMapping() {

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://localhost:8080/api/ints")
            .build()
        val response = client.newCall(request).execute()
        assertEquals("""[1, 2, 3]""", response.body?.string())
    }


    @Test
    fun getJsonEndpointPathParam() {

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://localhost:8080/api/path/a")
            .build()
        val response = client.newCall(request).execute()
        assertEquals(JsonString("A").toJson(), response.body?.string())

    }

    @Test
    fun getJsonEndpointPathParamPair() {
        val client = OkHttpClient()
        val requestNew = Request.Builder().url("http://localhost:8080/api/pair").build()
        val responseNew = client.newCall(requestNew).execute()

        assertEquals(
            TestController().obj().convertToJson().toJson(),
            responseNew.body?.string())

    }

    @Test
    fun getJsonEndpointQueryParam() {

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://localhost:8080/api/args?n=3&text=PA")
            .build()
        val response = client.newCall(request).execute()

        val result = TestController()

        assertEquals(
            (result.args(3,"PA")).convertToJson().toJson(),
            response.body?.string())
    }

    @Test
    fun notFoundRouteReturns404() {
        val client = OkHttpClient()
        val request = Request.Builder().url("http://localhost:8080/api/str").build()

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

    @Mapping("pair")
    fun obj(): Pair<String, String> = Pair("um", "dois")

    @Mapping("path/{var}")
    fun getPath(@PathParam("var") param: String) = param.uppercase()

    @Mapping("args")
    fun args(
        @QueryParam n: Int,
        @QueryParam text: String
    ): Map<String, String> = mapOf(text to text.repeat(n))
}

@Mapping("api")
class CopyController {
    @Mapping("ints")
    fun getInts() : List<Int> = listOf(1, 2, 3)
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
