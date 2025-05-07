import jsonAlternative.*
import visitorToJson.*


    fun testVisitores(){
        val json = JsonObject(
            listOf(
                "nome" to JsonString("Alice"),
                "idade" to JsonNumber(30),
                "numero" to JsonArray(listOf(JsonNumber(1), JsonNumber(2), JsonNumber(3))),
                "valor" to JsonArray(listOf(JsonString("N"), JsonNumber(2), JsonNumber(3)))

            )
        )

        // Validar chaves únicas e válidas
        val keyVisitor = VisitorValidateKeys()
        json.accept(keyVisitor)
        println("Chaves válidas e únicas? ${keyVisitor.isValid}")

        val json2 = JsonArray(listOf(
            JsonArray(listOf(JsonNumber(1),JsonNumber(2))),
            JsonArray(listOf(JsonNumber(1),JsonNumber(2))),
            JsonArray(listOf(JsonNumber(3),JsonNumber(4)))
        ))

        // Validar tipos homogêneos
        val typeVisitor = VisitorArrayElementsSameType()
        json2.accept(typeVisitor)
        println("Arrays homogêneos? ${typeVisitor.isSameType}")

        json.accept(typeVisitor)
        println("Arrays homogêneos? ${typeVisitor.isSameType}")
    }

fun main(){
    testVisitores()
}