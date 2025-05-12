import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import jsonAlternative.*
import java.net.URI
import kotlin.collections.List

class TestFase3 {

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

    companion object {
        private lateinit var server: jsonAlternative.GetJson

        @BeforeAll
        @JvmStatic
        fun setup() {
            server = _root_ide_package_.jsonAlternative.GetJson(
                TestController::class,
                CourseController::class,
                Controller::class)
            server.start(8080)
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            // Implementar shutdown do servidor se necessário
        }
    }

    // Testes Fase 1: Modelo JSON
    @Test
    fun `test serialization of JsonObject`() {
        val obj = _root_ide_package_.jsonAlternative.JsonObject(
            listOf(
                "name" to _root_ide_package_.jsonAlternative.JsonString("PA")
            )
        )
        assertEquals("""{"name": "PA"}""", obj.toJson())
    }

    // Testes Fase 2: Conversão de Objetos
    @Test
    fun `test convert data class to JSON`() {
        val course = Course("PA", 6, emptyList())
        val json = _root_ide_package_.jsonAlternative.convertToJson(course)
        assertTrue(json is jsonAlternative.JsonObject)
        assertEquals("""{"name": "PA", "credits": 6, "evaluation": []}""", json.toJson())
    }

    // Testes Fase 3: Endpoints HTTP
    @Test
    fun `test GET api_ints endpoint`() {

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://localhost/api/ints")
            .build()
        //            .url("http://localhost:8080/api/ints")
        val response = client.newCall(request).execute()
        assertEquals("""[1,2,3]""", response.body?.string())
    }

//    @Test
//    fun `test GET api_ints endpoint_path`() {
//
//        val client = OkHttpClient()
//        val request = Request.Builder()
//            .url("http://localhost/api/path/a")
//            .build()
//        //            .url("http://localhost:8080/api/ints")
//        val response = client.newCall(request).execute()
//        assertEquals("A", response.body?.string())
//    }

    @Test
    fun `test nested data class conversion`() {
        val evalItem = EvalItem("test", 0.5, true, EvalType.TEST)
        val json = _root_ide_package_.jsonAlternative.convertToJson(evalItem)
        assertEquals(
            """{"name": "test", "percentage": 0.5, "mandatory": true, "type": "TEST"}""",
            json.toJson()
        )
    }

    @Test
    fun `test invalid route returns 404`() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://localhost/invalid")
            .build()
        //            .url("http://localhost:8080/invalid")
        val response = client.newCall(request).execute()
        assertEquals(404, response.code)
    }
}

// Controladores de Teste
@jsonAlternative.Mapping("api")
class TestController {
    @jsonAlternative.Mapping("ints")
    fun getInts() : List<Int> = listOf(1, 2, 3)

    @jsonAlternative.Mapping("path/{var}")
    fun getPath(@jsonAlternative.PathParam("var") param: String) = param.uppercase()
}

@jsonAlternative.Mapping("courses")
class CourseController {
    @jsonAlternative.Mapping("sample")
    fun getCourse() = Course(
        "PA", 6, listOf(
            EvalItem("quizzes", 0.2, false, null)
        )
    )
}
