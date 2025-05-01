package jsonAlternative

import visitorToJson.JsonVisitor

/**
 * @constructor
 * Criar um objecto do tipo json_alternative.JsonValue recebendo como parametro um objeto do tipo String
 */
class JsonString(private var value: String) : JsonValue{

    override fun toJson(): String = "\"${value}\""

    override fun accept(visitor: JsonVisitor) {
        visitor.visitorString(this)
    }
}