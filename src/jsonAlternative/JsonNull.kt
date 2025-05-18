package jsonAlternative
import visitorToJson.JsonVisitor

/**
 * @constructor
 * Criar um objecto do tipo json_alternative.JsonValue contendo este valor -> Null
 */
object JsonNull : JsonValue {
    override fun toJson(): String = "null"
    override fun accept(visitor: JsonVisitor) {
        visitor.visitorNull(this)
    }
}


