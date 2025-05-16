package convertToJson

import jsonAlternative.JsonArray
import jsonAlternative.JsonBoolean
import jsonAlternative.JsonNull
import jsonAlternative.JsonNumber
import jsonAlternative.JsonObject
import jsonAlternative.JsonString
import jsonAlternative.JsonValue
import kotlin.reflect.KClass
import kotlin.reflect.full.*

/**
 * @param objectToConvert
 * Qualquer objeto
 *
 * @return JsonValue correspondente ao tipo de objeto do input recebido
 *
 * @exception IllegalArgumentException
 * Caso não exista conversão para o input recebido
 */
class DefaultJsonConverter : JsonConverter {

    override fun objectToJson(objectToConvert: Any?) : JsonValue {

        return when(objectToConvert) {
            is Int, is Double -> JsonNumber(objectToConvert)
            is String -> JsonString(objectToConvert)
            is Boolean -> JsonBoolean(objectToConvert)
            is List<*> -> JsonArray(objectToConvert.map { objectToJson(it) })
            is Enum<*> -> JsonString(objectToConvert.name)
            null -> JsonNull
            is Map<*, *> -> {
                var conversion: List<Pair<String, JsonValue>> = listOf()
                //Criar caso Key não seja string

                objectToConvert.forEach { (key, value) ->
                    if (key is String) {
                        conversion = conversion + listOf(key to objectToJson(value))
                    }
                }
                JsonObject(conversion)
            }
            else -> {
                val kClass = objectToConvert::class as KClass<Any>
                if (kClass.isData) { // Verificação correta via reflection
                    var jsonFields: List<Pair<String, JsonValue>> = listOf()
                    kClass.primaryConstructor?.parameters?.forEach { p ->
                        val properties = kClass.declaredMemberProperties.first { it.name == p.name }
                        jsonFields = jsonFields + listOf(properties.name to objectToJson(properties.call(objectToConvert)))
                    }

                    JsonObject(jsonFields)
                } else {
                    throw IllegalArgumentException("Não existe conversão para json com o valor recebido")
                }
            }
        }

    }
}