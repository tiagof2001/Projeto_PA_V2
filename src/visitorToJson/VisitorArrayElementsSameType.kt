package visitorToJson

import jsonAlternative.*

class VisitorArrayElementsSameType : JsonVisitor {
    var isSameType = true

    override fun visitorArray(array: JsonArray) {
        val elements = array.getValues().filter { it != JsonNull }
        if (elements.isNotEmpty()) {
            isSameType = isSameType && elements.all { it::class == elements.first()::class }
        }

        for (value in array.getValues()) {
            value.accept(this)
        }
    }

    override fun visitorObject(obj: JsonObject) {
        for ((_, value) in obj.getMembers()) {
            value.accept(this)
        }
    }

    override fun visitorString(str: JsonString) {}
    override fun visitorNumber(num: JsonNumber) {}
    override fun visitorBoolean(bool: JsonBoolean) {}
    override fun visitorNull(nullValue: JsonNull) {}
}