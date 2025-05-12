package jsonAlternative

import kotlin.reflect.full.memberProperties


fun convertToJson(objectToConvert: Any?) : JsonValue {
    /**
     * 1º Recebe uma variavel qualquer
     * 2º Verificar se existe um json para converter
     * - Se sim, retornar um JsonValue correspondente
     * - Se não, mostra mensagem de erro a indicar que não é possivel
     */
    return when(objectToConvert) {
        is Int, is Double -> JsonNumber(objectToConvert)
        is String -> JsonString(objectToConvert)
        is Boolean -> JsonBoolean(objectToConvert)
        is List<*> -> JsonArray(objectToConvert.map {
            convertToJson(it) }
        )
        //Enums
        null -> JsonNull
        //data classes with properties whose type is supported



        //-> Transformado em JsonObject
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
                val properties = kClass.memberProperties
                val jsonFields = properties.map { prop ->
                    prop.name to convertToJson(prop.get(objectToConvert))
                }
                JsonObject(jsonFields)
            } else {
                throw IllegalArgumentException("Não existe conversão para json com o valor recebido")
            }
        }

        //else -> throw IllegalArgumentException("Não existe conversão para json com o valor recebido")
    }

}

