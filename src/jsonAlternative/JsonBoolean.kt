package jsonAlternative

import visitorToJson.JsonVisitor

/**
 * @constructor
 * Criar um objecto do tipo json_alternative.JsonValue
 * recebendo como parametro um objeto do tipo boolean (True/False)
 */
class JsonBoolean(private var value: Boolean) : JsonValue{

    override fun toJson(): String = value.toString()

    override fun accept(visitor: JsonVisitor) {
        visitor.visitorBoolean(this)
    }
}