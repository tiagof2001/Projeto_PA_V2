package convertToJson

import jsonAlternative.JsonObject
import jsonAlternative.JsonValue
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor

class ExtendedJsonConverter( private val delegate: JsonConverter) : JsonConverter {
    override fun objectToJson(objectToConvert: Any?): JsonValue {
       return when (objectToConvert) {
           is Pair<*,*> -> {
               var jsonFields: List<Pair<String, JsonValue>> = listOf()
               val kClass = objectToConvert::class as KClass<*>
               kClass.primaryConstructor?.parameters?.forEach { p ->
                   val properties = kClass.declaredMemberProperties.first { it.name == p.name }
                   jsonFields = jsonFields + listOf(properties.name to objectToJson(properties.call(objectToConvert)))
               }
               JsonObject(jsonFields)
           }
           else -> delegate.objectToJson(objectToConvert)  // chama o conversor original para os outros casos
       }
    }
}