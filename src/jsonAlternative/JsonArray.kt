package jsonAlternative

import visitorToJson.JsonVisitor

/**
 * @constructor
 * Criar um objecto do tipo json_alternative.JsonValue recebendo
 * como parametro um objeto do tipo List, aceitando objetos json_alternative.JsonValue
 */
class JsonArray(private var value: List<JsonValue>) : JsonValue {

    fun filter(predicate: (JsonValue) -> Boolean): JsonArray = JsonArray(value.filter(predicate))

    fun map(transform: (JsonValue) -> JsonValue): JsonArray = JsonArray(value.map(transform))

    override fun toJson(): String = value.joinToString(prefix = "[", postfix = "]") { it.toJson() }

    override fun accept(visitor: JsonVisitor) { visitor.visitorArray(this) }
    fun getValues(): List<JsonValue> = value
}