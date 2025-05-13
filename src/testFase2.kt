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

    @Test
    fun intTojson(){
        assertEquals(convertToJson(10).toJson(), JsonNumber(10).toJson())
    }

    @Test
    fun doubleTojson(){
        assertEquals(convertToJson(10.2).toJson(), JsonNumber(10.2).toJson())
    }

    @Test
    fun stringTojson(){
        assertEquals(convertToJson("Alice").toJson(), JsonString("Alice").toJson())
    }

    @Test
    fun booleanTojson(){
        assertEquals(convertToJson(true).toJson(), JsonBoolean(true).toJson())
    }

    @Test
    fun mapToJson(){
        val testMap = mapOf<String, Any?>( "teste1" to 10, "teste2" to 20 )
        val resultMap = listOf<Pair<String, JsonValue>>("teste1" to JsonNumber(10), "teste2" to JsonNumber(20))
        assertEquals(convertToJson(testMap).toJson(), JsonObject(resultMap).toJson())
    }

    @Test
    fun listToJson(){
        val testList = listOf<Any?>(null,20)
        val resultList = listOf<JsonValue>(JsonNull, JsonNumber(20))
        assertEquals(convertToJson(testList).toJson(), JsonArray(resultList).toJson())
    }

    @Test
    fun enumToJson(){
        val direction = convertToJson(EvalType.EXAM)
        assertEquals(direction.toJson(), JsonString("EXAM").toJson())
    }

    @Test
    fun dataToJson(){
        val course = Course("PA", 6, listOf(
            EvalItem("quizzes", .2, false, null),
            EvalItem("project", .8, true, EvalType.PROJECT))
        )
        println(convertToJson(course).toJson())
    }

    @Test
    fun pairToJson(){
        val pair: Pair<String, String> = Pair("um", "dois")
        val person = JsonObject(
            listOf(
                "first" to JsonString("um"),
                "second" to JsonString("dois")
            )
        )
        println(convertToJson(pair).toJson())
        assertEquals(convertToJson(pair).toJson(), person.toJson())

    }
    /**
     * {
     *  "credits": 6,
     *  "evaluation": [
     *      {
     *          "mandatory": false,
     *          "name": "quizzes",
     *          "percentage": 0.2,
     *          "type": null
     *      },
     *      {
     *          "mandatory": true,
     *          "name": "project",
     *          "percentage": 0.8,
     *          "type": "PROJECT"
     *       }
     *       ],
     *  "name": "PA"
     * }
     *
     */

}
