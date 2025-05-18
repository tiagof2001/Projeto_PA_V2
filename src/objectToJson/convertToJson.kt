package objectToJson

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
 * @param "objectToConvert"
 * Qualquer objeto
 *
 * @return JsonValue correspondente ao tipo de objeto do input recebido
 *
 * @exception IllegalArgumentException
 * Caso não exista conversão para o input recebido
 *
 */
fun Any?.convertToJson() : JsonValue = when(this) {
        is Int, is Double -> JsonNumber(this)
        is String -> JsonString(this)
        is Boolean -> JsonBoolean(this)
        is List<*> -> JsonArray(this.map {it.convertToJson()})
        is Enum<*> -> JsonString(this.name)
        null -> JsonNull

        else -> {
            val kClass = this::class as KClass<Any>
            if (kClass.isData) { // Verificação correta via reflection
                var jsonFields: List<Pair<String, JsonValue>> = listOf()
                kClass.primaryConstructor?.parameters?.forEach { p ->
                    val properties = kClass.declaredMemberProperties.first { it.name == p.name }
                    jsonFields = jsonFields + listOf(properties.name to properties.call(this).convertToJson())
                }

                JsonObject(jsonFields)
            } else {
                throw IllegalArgumentException("Não existe conversão para json com o valor recebido")
            }
        }

}

fun Map<*,*>.convertToJson() : JsonValue {
        var conversion: List<Pair<String, JsonValue>> = listOf()
        this.forEach { (key, value) ->
            if (key is String) {
                conversion = conversion + listOf(key to value.convertToJson())
            }
        }
        return JsonObject(conversion)
}


fun Pair<String, String>.convertToJson() : JsonValue {

    var jsonFields: List<Pair<String, JsonValue>> = listOf()
    val kClass = this::class as KClass<*>
    kClass.primaryConstructor?.parameters?.forEach { p ->
        val properties = kClass.declaredMemberProperties.first { it.name == p.name }
        jsonFields = jsonFields + listOf(properties.name to properties.call(p).convertToJson())
    }
    return JsonObject(jsonFields)
}
