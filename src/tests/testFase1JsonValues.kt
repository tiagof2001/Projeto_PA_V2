package tests

import jsonAlternative.*
import junit.framework.TestCase.assertEquals
import org.junit.Test

class TestFase1JsonValues {

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

        var jsonTest = JsonArray(
            listOf(
                JsonNumber(10),
                JsonNumber(20),
                JsonNumber(30)
            )
        )
        assertEquals(jsonTest.toJson(), "[10, 20, 30]")

        jsonTest = JsonArray(
            listOf(
                JsonBoolean(true),
                JsonBoolean(false),
                JsonBoolean(true)
            )
        )
        assertEquals(jsonTest.toJson(), "[true, false, true]")

        jsonTest = JsonArray(
            listOf(
                JsonString("10A"),
                JsonString("20A"),
                JsonString("30A")
            )
        )
        assertEquals(jsonTest.toJson(), "[\"10A\", \"20A\", \"30A\"]")

        jsonTest = JsonArray(
            listOf(
                JsonNull,
                JsonNull,
                JsonNull
            )
        )
        assertEquals(jsonTest.toJson(), "[null, null, null]")
        jsonTest = JsonArray(
            listOf(
                JsonNumber(10),
                JsonString("20A"),
                JsonBoolean(true),
                JsonBoolean(false),
                JsonNull
            )
        )
        assertEquals(jsonTest.toJson(), "[10, \"20A\", true, false, null]")
    }

    @Test
    fun objectTojson() {
        val person = JsonObject(
            listOf(
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
        val testjson = JsonObject(
            listOf(
                "name" to JsonString("Alice"),
                "age" to JsonNumber(30),
                "isStudent" to JsonBoolean(true),
                "hobbies" to JsonArray(listOf(JsonString("not reading"), JsonString("reading"))),
                "address" to JsonNull
            )
        )
        val filterName = testjson.filter { it.first == "name" }
        val filterNameResult = JsonObject(listOf("name" to JsonString("Alice")))
        assertEquals(filterName.toJson(),filterNameResult.toJson())

        val filterNumber = testjson.filter { it.second::class == JsonNumber::class }
        val filterNumberResult = JsonObject(listOf( "age" to JsonNumber(30)))
        assertEquals(filterNumber.toJson(),filterNumberResult.toJson())

        val filterArray = testjson.filter { it.second is JsonArray }
        val hobbiesArray = (filterArray.getJsonValue("hobbies") as JsonArray)
        println(hobbiesArray.toJson())
        val readingOnly = hobbiesArray.filter {
            it is JsonString && it.toJson() == JsonString("reading").toJson()
        }

        val expectedReadingOnly = JsonArray(listOf(JsonString("reading")))
        assertEquals(expectedReadingOnly.toJson(), readingOnly.toJson())

    }

    @Test
    fun arrayFilterTest() {
        val array = JsonArray(
            listOf(
                JsonNumber(1),
                JsonNumber(2),
                JsonNumber(3),
                JsonNumber(4)
            )
        )

        val filtered = array.filter { it.toJson().toInt() % 2 == 0 }
        val filterResult = JsonArray(listOf(JsonNumber(2),JsonNumber(4)))
        assertEquals(filtered.toJson(), filterResult.toJson())
    }

    @Test
    fun arrayMapTest() {
        val array = JsonArray(
            listOf(
                JsonNumber(1),
                JsonNumber(2),
                JsonNumber(3)
            )
        )

        val mapped = array.map { JsonNumber(it.toJson().toInt() * 10) }
        val mappedResult = JsonArray(listOf(JsonNumber(10),
            JsonNumber(20),
            JsonNumber(30)))
        assertEquals(mapped.toJson(), mappedResult.toJson())
    }

    @Test
    fun testGetJsonValueFromJsonArray() {
        val jsonArray = JsonArray(
            listOf(
                JsonString("item1"),
                JsonNumber(42),
                JsonBoolean(false)
            )
        )

        // Teste com índices válidos
        assertEquals(JsonString("item1").toJson(), jsonArray.getJsonValue(0).toJson())
        assertEquals(JsonNumber(42).toJson(), jsonArray.getJsonValue(1).toJson())
        assertEquals(JsonBoolean(false).toJson(), jsonArray.getJsonValue(2).toJson())
    }

}