package tests

import jsonAlternative.*
import junit.framework.TestCase.assertEquals
import convertObjectToJson.*
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
        val num = 10
        assertEquals(num.convertToJson().toJson(), JsonNumber(num).toJson())
    }

    @Test
    fun doubleTojson(){
        val num = 10.2
        assertEquals(num.convertToJson().toJson(), JsonNumber(num).toJson())
    }

    @Test
    fun stringTojson(){
        assertEquals("Alice".convertToJson().toJson(), JsonString("Alice").toJson())
    }

    @Test
    fun booleanTojson(){
        val bool = true
        assertEquals(bool.convertToJson().toJson(), JsonBoolean(bool).toJson())
    }

    @Test
    fun mapToJson(){
        val testMap = mapOf<String, Any?>( "teste1" to 10, "teste2" to 20 )
        val resultMap = listOf<Pair<String, JsonValue>>("teste1" to JsonNumber(10), "teste2" to JsonNumber(20))
        assertEquals(testMap.convertToJson().toJson(), JsonObject(resultMap).toJson())

    }

    @Test
    fun listToJson(){
        val testList = listOf<Any?>(null,20)
        val resultList = listOf(JsonNull, JsonNumber(20))
        assertEquals(testList.convertToJson().toJson(), JsonArray(resultList).toJson())
    }

    @Test
    fun enumToJson(){
        val direction = (EvalType.EXAM).convertToJson()
        assertEquals(direction.toJson(), JsonString("EXAM").toJson())
    }

    @Test
    fun dataToJson(){
        val course = Course("PA", 6, listOf(
            EvalItem("quizzes", .2, false, null),
            EvalItem("project", .8, true, EvalType.PROJECT))
        )

        val courseResult = JsonObject(listOf(
            "name" to JsonString("PA"),
            "credits" to JsonNumber(6),
            "evaluation" to JsonArray(listOf(
                JsonObject(listOf(
                    "name" to JsonString("quizzes"),
                    "percentage" to JsonNumber(.2),
                    "mandatory" to JsonBoolean(false),
                    "type" to JsonNull
                )),
                JsonObject(listOf(
                    "name" to JsonString("project"),
                    "percentage" to JsonNumber(.8),
                    "mandatory" to JsonBoolean(true),
                    "type" to JsonString(EvalType.PROJECT.toString())
                ))
            ))
        ))

        assertEquals(course.convertToJson().toJson(), courseResult.toJson())
    }

    @Test
    fun pairToJson(){
        val pair: Pair<*, *> = Pair("um", "dois")
        val person = JsonObject(
            listOf(
                "first" to JsonString("um"),
                "second" to JsonString("dois")
            )
        )
        assertEquals(pair.convertToJson().toJson(), person.toJson())
    }
}
