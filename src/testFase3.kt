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

    companion object {
        private lateinit var server: GetJson

        @BeforeAll
        @JvmStatic
        fun setup() {
            server = GetJson(
                TestController::class,
                CourseController::class
                //Controller::class
            )
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
        val obj = JsonObject(
            listOf(
                "name" to JsonString("PA")
            )
        )
        assertEquals("""{"name": "PA"}""", obj.toJson())
    }

    // Testes Fase 2: Conversão de Objetos
    @Test
    fun `test convert data class to JSON`() {
        val course = Course("PA", 6, emptyList())
        val json = convertToJson(course)
        assertTrue(json is JsonObject)
        assertEquals("""{"name": "PA", "credits": 6, "evaluation": []}""", json.toJson())
    }

    // Testes Fase 3: Endpoints HTTP
    @Test
    fun `test GET api_ints endpoint`() {

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://localhost:8080/api/ints")
            .build()
        val response = client.newCall(request).execute()
        assertEquals("""[1, 2, 3]""", response.body?.string())
    }


    @Test
    fun `test GET api_ints endpoint_PathParam`() {

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://localhost:8080/api/path/a")
            .build()
        val response = client.newCall(request).execute()
        assertEquals(JsonString("A").toJson(), response.body?.string())
    }

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
            .url("http://localhost:8080/invalid")
            .build()
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
