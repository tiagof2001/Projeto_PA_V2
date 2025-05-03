package jsonAlternative

import visitorToJson.JsonVisitor

/**
 *  Os objetos são especificados entre chaves e podem ser compostos por múltiplos pares nome/valor,
 *  desde que as chaves sejam distintas
 *  {
 *   “titulo”: “JSON x XML”,
 *   “resumo”: “o duelo de dois modelos de representação de informações”,
 *   “ano”: 2012,
 *   “genero”: [“aventura”, “ação”, “ficção”]
 *  }
 */
class JsonObject(private val members: List<Pair<String, JsonValue>>) : JsonValue {

    init {
        val countKeys = members.groupingBy { it.first }.eachCount()
        val duplicates = countKeys.filter { it.value > 1 }.keys
        if (duplicates.isNotEmpty()) {
            throw IllegalArgumentException("Chaves duplicadas não são permitidas: ${duplicates.joinToString(", ")}")
        }
    }

    fun filter(predicate: (Pair<String, JsonValue>) -> Boolean): JsonObject =
        JsonObject(members.filter(predicate))

    override fun toJson(): String =
        members.joinToString(prefix = "{", postfix = "}") {
                (key, value) -> "\"$key\": ${value.toJson()}"
        }

    override fun accept(visitor: JsonVisitor) { visitor.visitorObject(this) }

    fun getMembers(): List<Pair<String, JsonValue>> = members
}