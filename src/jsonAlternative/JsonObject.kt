package jsonAlternative

import visitorToJson.JsonVisitor

/**
 *  Os objetos são especificados entre chaves e podem ser compostos por múltiplos pares nome/valor
 *  {
 *   “titulo”: “JSON x XML”,
 *   “resumo”: “o duelo de dois modelos de representação de informações”,
 *   “ano”: 2012,
 *   “genero”: [“aventura”, “ação”, “ficção”]
 *  }
 */
class JsonObject(private val members: Map<String, JsonValue>) : JsonValue {

    fun filter(predicate: (Map.Entry<String, JsonValue>) -> Boolean):
            JsonObject = JsonObject(members.filter(predicate))


    override fun toJson(): String =
        members.entries.joinToString(prefix = "{", postfix = "}") {
            (key, value) -> "\"$key\": ${value.toJson()}"
        }


    override fun accept(visitor: JsonVisitor) { visitor.visitorObject(this) }
    fun getMembers(): Map<String, JsonValue> = members
}