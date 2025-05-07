package visitorToJson

import jsonAlternative.*

class VisitorArrayElementsSameType : JsonVisitor {
    var isSameType = true

    override fun visitorArray(array: JsonArray) {
        val elements = array.getValues().filter { it != JsonNull }
        if (elements.isNotEmpty()) {
            isSameType = elements.all { it::class == elements.first()::class }
        }
    }

    override fun visitorObject(obj: JsonObject) {}
    override fun visitorString(str: JsonString) {}
    override fun visitorNumber(num: JsonNumber) {}
    override fun visitorBoolean(bool: JsonBoolean) {}
    override fun visitorNull(nullValue: JsonNull) {}
}