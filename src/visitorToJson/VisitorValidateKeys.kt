package visitorToJson

import jsonAlternative.*

class VisitorValidateKeys : JsonVisitor {
    var isValid = true

    override fun visitorObject(obj: JsonObject) {
        val keys = obj.getMembers().keys
        isValid = isValid && keys.all { it.isNotBlank() } && keys.size == keys.toSet().size

        for ((_, value) in obj.getMembers()) {
            value.accept(this)
        }
    }

    override fun visitorArray(array: JsonArray) {
        for (item in array.getValues()) {
            item.accept(this)
        }
    }

    override fun visitorString(str: JsonString) {}
    override fun visitorNumber(num: JsonNumber) {}
    override fun visitorBoolean(bool: JsonBoolean) {}
    override fun visitorNull(nullValue: JsonNull) {}
}
