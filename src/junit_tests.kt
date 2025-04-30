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
fun conversao_Int_toJson(){

    val jsonid = JsonNumber(10)
    assertEquals(jsonid.toString(), "10")
    assertEquals(jsonid.getValueVariavel(),10)

    jsonid.setValue(1)

    assertEquals(jsonid.getValueVariavel(),1)}

@Test
fun conversao_String_toJson(){

    val jsonid = JsonString("teste")

    assertEquals(jsonid.toString(), "\"teste\"")
    assertEquals(jsonid.getValueVariavel(),"teste")

    jsonid.setValue("novoTeste")
    assertEquals(jsonid.getValueVariavel(),"novoTeste")
}



fun main() {

//    val course = Course(
//        "PA", 6, listOf(
//            EvalItem("quizzes", .2, false, null),
//            EvalItem("project", .8, true, EvalType.PROJECT)
//        )
//    )
//    val conversao = JsonObject(course)

    val person = JsonObject(mapOf(
        "name" to JsonString("Alice"),
        "age" to JsonNumber(30),
        "isStudent" to JsonBoolean(false),
        "hobbies" to JsonArray(listOf(
            JsonString("reading"),
            JsonString("cycling")
        )),
        "address" to JsonNull
    ))

    println(person.toJson())
}