package tests

import jsonAlternative.*
import visitorToJson.*
import junit.framework.TestCase.assertEquals
import org.junit.Test



class TestFase1Visitores(){
    val json = JsonObject(
        listOf(
            "nome" to JsonString("Alice"),
            "idade" to JsonNumber(30),
            "numero" to JsonArray(listOf(JsonNumber(1), JsonNumber(2), JsonNumber(3))),
            "valor" to JsonArray(listOf(JsonString("N"), JsonNumber(2), JsonNumber(3)))
        )
    )

    @Test
    fun visitorValidateKeys(){

        // Validar chaves únicas e válidas
        val keyVisitor = VisitorValidateKeys()
        json.accept(keyVisitor)
        //println("Chaves válidas e únicas? ${keyVisitor.getValid()}")
        assertEquals(true,keyVisitor.getValid())
    }

    @Test
    fun visitorArraysHaveSameType(){
        val json2 = JsonArray(listOf(
            JsonArray(listOf(JsonNumber(1),JsonNumber(2))),
            JsonArray(listOf(JsonNumber(1),JsonNumber(2))),
            JsonArray(listOf(JsonNumber(3),JsonNumber(4)))
        ))

        // Validar tipos homogêneos
        val typeVisitor = VisitorArrayElementsSameType()
        json2.accept(typeVisitor)
        assertEquals(true,typeVisitor.getIsSameType())

        //Possivel erro, conteudo do JsonString("1") == JsonNumber(1)
        val json3 = JsonArray(listOf(
            JsonArray(listOf(JsonString("A"),JsonNumber(2))),
            JsonArray(listOf(JsonNumber(1),JsonNumber(2))),
            JsonArray(listOf(JsonNumber(3),JsonNumber(4)))
        ))

        json.accept(typeVisitor)
        assertEquals(false,typeVisitor.getIsSameType())
    }
}

