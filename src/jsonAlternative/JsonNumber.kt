package jsonAlternative

import visitorToJson.JsonVisitor

/**
 * @constructor
 * Criar um objecto do tipo json_alternative.JsonValue recebendo como parametro um objeto do tipo Number
 */
class JsonNumber(private var value: Number) : JsonValue{

    override fun toJson(): String = value.toString()

    override fun accept(visitor: JsonVisitor) {
        visitor.visitorNumber(this)
    }
}