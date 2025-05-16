import convertToJson.*
import jsonAlternative.*
import junit.framework.TestCase.assertEquals
import org.junit.Test

data class Course(
    val name: String,
    val credits: Int,
    val evaluation: List<EvalItem>
)

data class EvalItem(
    val name: String,
    val percentage: Double,
    val mandatory: Boolean,
    val type: EvalType?
)

enum class EvalType {
    TEST, PROJECT, EXAM
}

class TestFase2{

    private val baseConverter = DefaultJsonConverter()

    @Test
    fun intTojson(){
        assertEquals(baseConverter.objectToJson(10).toJson(), JsonNumber(10).toJson())
    }

    @Test
    fun doubleTojson(){

        assertEquals(baseConverter.objectToJson(10.2).toJson(), JsonNumber(10.2).toJson())
    }

    @Test
    fun stringTojson(){
        assertEquals(baseConverter.objectToJson("Alice").toJson(), JsonString("Alice").toJson())
    }

    @Test
    fun booleanTojson(){
        assertEquals(baseConverter.objectToJson(true).toJson(), JsonBoolean(true).toJson())
    }

    @Test
    fun mapToJson(){
        val testMap = mapOf<String, Any?>( "teste1" to 10, "teste2" to 20 )
        val resultMap = listOf<Pair<String, JsonValue>>("teste1" to JsonNumber(10), "teste2" to JsonNumber(20))
        assertEquals(baseConverter.objectToJson(testMap).toJson(), JsonObject(resultMap).toJson())
    }

    @Test
    fun listToJson(){
        val testList = listOf<Any?>(null,20)
        val resultList = listOf<JsonValue>(JsonNull, JsonNumber(20))
        assertEquals(baseConverter.objectToJson(testList).toJson(), JsonArray(resultList).toJson())
    }

    @Test
    fun enumToJson(){
        val direction = baseConverter.objectToJson(EvalType.EXAM)
        assertEquals(direction.toJson(), JsonString("EXAM").toJson())
    }

    @Test
    fun dataToJson(){
        val course = Course("PA", 6, listOf(
            EvalItem("quizzes", .2, false, null),
            EvalItem("project", .8, true, EvalType.PROJECT))
        )
        println(baseConverter.objectToJson(course).toJson())
        //val courseResult = JsonObject()
    }

    @Test
    fun pairToJson(){
        val extendedConverter = ExtendedJsonConverter(baseConverter)

        val pair: Pair<String, String> = Pair("um", "dois")
        val person = JsonObject(
            listOf(
                "first" to JsonString("um"),
                "second" to JsonString("dois")
            )
        )
        println(extendedConverter.objectToJson(pair).toJson())
        assertEquals(extendedConverter.objectToJson(pair).toJson(), person.toJson())
    }


}
