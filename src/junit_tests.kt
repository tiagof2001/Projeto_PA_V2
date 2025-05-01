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


@Test
fun intTojson() {

    val jsonTest = JsonNumber(10)
    assertEquals(jsonTest.toJson(), "10")
}

@Test
fun booleanTojson(){
    val jsonTest = JsonBoolean(true)
    assertEquals(jsonTest.toJson(), "true")
}

@Test
fun stringTojson(){

    val jsonTest = JsonString("teste")
    assertEquals(jsonTest.toJson(), "\"teste\"")

}

@Test
fun nullTojson() {

    val jsonTest = JsonNull
    assertEquals(jsonTest.toJson(), "null")
}

@Test
fun arrayTojson() {

    var jsonTest = JsonArray(listOf(
        JsonNumber(10),
        JsonNumber(20),
        JsonNumber(30)))
    assertEquals(jsonTest.toJson(), "[10, 20, 30]")

    jsonTest = JsonArray(listOf(
        JsonBoolean(true),
        JsonBoolean(false),
        JsonBoolean(true)
    ))
    assertEquals(jsonTest.toJson(), "[true, false, true]")

    jsonTest = JsonArray(listOf(
        JsonString("10"),
        JsonString("20"),
        JsonString("30")
    ))
    assertEquals(jsonTest.toJson(), "[\"10\", \"20\", \"30\"]")

    jsonTest = JsonArray(listOf(
        JsonNull,
        JsonNull,
        JsonNull
    ))
    assertEquals(jsonTest.toJson(), "[null, null, null]")
    jsonTest = JsonArray(listOf(
        JsonNumber(10),
        JsonString("20"),
        JsonBoolean(true),
        JsonBoolean(false),
        JsonNull
    ))
    assertEquals(jsonTest.toJson(), "[10, \"20\", true, false, null]")
}

@Test
fun objectTojson() {
    val person = JsonObject(mapOf(
        "name" to JsonString("Alice"),
        "age" to JsonNumber(30),
        "isStudent" to JsonBoolean(false),
        "hobbies" to JsonArray(listOf(JsonString("reading"), JsonString("reading"))),
        "address" to JsonNull
        )
    )

    assertEquals(person.toJson(),
        "{" +
                "\"name\": \"Alice\", " +
                "\"age\": 30, " + "\"isStudent\": false, "+
                "\"hobbies\": [\"reading\", \"reading\"], "+
                "\"address\": null" +
                  "}"
                )
}

@Test
fun objectToJsonFiltring() {
    val testjson = JsonObject(mapOf(
        "name" to JsonString("Alice"),
        "age" to JsonNumber(30),
        "isStudent" to JsonBoolean(true),
        "hobbies" to JsonArray(listOf(JsonString("reading"), JsonString("reading"))),
        "address" to JsonNull
    )
    )
    val filterName = testjson.filter { it.key == "name" }
    assertEquals(filterName.toJson(),"{" + "\"name\": \"Alice\"" + "}")
    val filterNumber = testjson.filter { it.value::class == JsonNumber::class }
    assertEquals(filterNumber.toJson(),"{" + "\"age\": 30" + "}")

}

@Test
fun arrayFilterTest() {
    val array = JsonArray(listOf(
            JsonNumber(1),
            JsonNumber(2),
            JsonNumber(3),
            JsonNumber(4)
        )
    )

    val filtered = array.filter { it.toJson().toInt() % 2 == 0 }
    assertEquals(filtered.toJson(), "[2, 4]")
}

@Test
fun arrayMapTest() {
    val array = JsonArray(listOf(
            JsonNumber(1),
            JsonNumber(2),
            JsonNumber(3)
        )
    )

    val mapped = array.map { JsonNumber(it.toJson().toInt() * 10) }
    assertEquals(mapped.toJson(), "[10, 20, 30]")
}

fun main() {

    intTojson()
    booleanTojson()
    stringTojson()
    nullTojson()
    arrayTojson()
    objectTojson()
    objectToJsonFiltring()
    arrayFilterTest()
    arrayMapTest()

}

//    val course = Course(
//        "PA", 6, listOf(
//            EvalItem("quizzes", .2, false, null),
//            EvalItem("project", .8, true, EvalType.PROJECT)
//        )
//    )
//    val conversao = JsonObject(course)

