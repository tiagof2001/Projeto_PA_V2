package jsonAlternative

import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility
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
fun convertToJson(objectToConvert: Any?) : JsonValue {
    /**
     * 1º Recebe uma variável qualquer
     * 2º Verificar se existe um json para converter
     * - Se sim, retornar um JsonValue correspondente
     * - Se não, mostra mensagem de erro a indicar que não é possível
     */
    return when(objectToConvert) {
        is Int, is Double -> JsonNumber(objectToConvert)
        is String -> JsonString(objectToConvert)
        is Boolean -> JsonBoolean(objectToConvert)
        is List<*> -> JsonArray(objectToConvert.map {
            convertToJson(it) }
        )
        is Enum<*> -> JsonString(objectToConvert.name)
        is Pair<*,*> -> {
            var jsonFields: List<Pair<String, JsonValue>> = listOf()
            val kClass = objectToConvert::class as kotlin.reflect.KClass<*>
            kClass.primaryConstructor?.parameters?.forEach { p ->
                val properties = kClass.declaredMemberProperties.first { it.name == p.name }
                jsonFields = jsonFields + listOf(properties.name to convertToJson(properties.call(objectToConvert)))
            }
            JsonObject(jsonFields)
        }
        null -> JsonNull
        is Map<*, *> -> {
            var conversion: List<Pair<String, JsonValue>> = listOf()
            objectToConvert.forEach { (key, value) ->
                if (key is String) {
                    conversion = conversion + listOf(key to convertToJson(value))
                }
                //Criar caso Key não seja string
            }
            return JsonObject(conversion)
        }
        else -> {
            val kClass = objectToConvert::class as kotlin.reflect.KClass<Any>
            if (kClass.isData) { // Verificação correta via reflection
                var jsonFields: List<Pair<String, JsonValue>> = listOf()
                kClass.primaryConstructor?.parameters?.forEach { p ->
                    val properties = kClass.declaredMemberProperties.first { it.name == p.name }
                    jsonFields = jsonFields + listOf(properties.name to convertToJson(properties.call(objectToConvert)))
                }

                JsonObject(jsonFields)
            } else {
                throw IllegalArgumentException("Não existe conversão para json com o valor recebido")
            }
        }
//        else -> throw IllegalArgumentException("Não existe conversão para json com o valor recebido")
    }

}