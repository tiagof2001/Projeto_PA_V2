package convertToJson

import jsonAlternative.JsonValue

interface JsonConverter {
    fun objectToJson(objectToConvert: Any?): JsonValue
}