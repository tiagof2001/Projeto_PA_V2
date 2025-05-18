package visitorToJson

import jsonAlternative.*

class VisitorValidateKeys : JsonVisitor {
    var isValid = true

    override fun visitorObject(obj: JsonObject) {
        val keys = obj.getMembers().map { it.first }
        isValid = keys.all { it.isNotBlank() } && keys.size == keys.toSet().size

    }

    override fun visitorArray(array: JsonArray) {}
    override fun visitorString(str: JsonString) {}
    override fun visitorNumber(num: JsonNumber) {}
    override fun visitorBoolean(bool: JsonBoolean) {}
    override fun visitorNull(nullValue: JsonNull) {}
}
