package visitorToJson

import jsonAlternative.*

interface JsonVisitor {
    fun visitorObject(obj: JsonObject)
    fun visitorArray(array: JsonArray)
    fun visitorString(str: JsonString)
    fun visitorNumber(num: JsonNumber)
    fun visitorBoolean(bool: JsonBoolean)
    fun visitorNull(nullValue: JsonNull)
}