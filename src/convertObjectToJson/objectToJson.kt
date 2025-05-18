package convertObjectToJson

import jsonAlternative.*
import kotlin.reflect.full.*

/**
 * Cria uma extensão para qualquer objeto para um objeto do tipo JsonValue
 *
 * @return JsonValue correspondente ao tipo do objeto fornecido.
 * @throws IllegalArgumentException Caso não exista conversão para o tipo do objeto fornecido.
 */

fun Any?.convertToJson(): JsonValue = when (this) {

    /**
     *  Converte um objeto do tipo Int.
     *  @return JsonNumber.
     */
    is Int -> JsonNumber(this)

    /**
     *  Converte um objeto do tipo Double.
     *  @return JsonNumber.
     */
    is Double -> JsonNumber(this)

    /**
     *  Converte um objeto do tipo Boolean.
     *  @return JsonBoolean.
     */
    is Boolean -> JsonBoolean(this)

    /**
     *  Converte um objeto do tipo string.
     *  @return JsonString.
     */
    is String -> JsonString(this)

    /**
     *  Converte um objeto do tipo null.
     *  @return JsonNull.
     */

    null -> JsonNull

    /**
     *  Converte um valor de um objeto Enum.
     *  @return JsonString.
     */
    is Enum<*> -> JsonString(this.name)

    /**
     *  Converte um objeto do tipo List<*>.
     *  @return JsonArray.
     */
    is List<*> -> JsonArray(this.map { it.convertToJson() })

    /**
     * Converte um objeto do tipo Map<*,*>.
     * @return JsonObject.
     */
    is Map<*, *> -> JsonObject(
        this.entries.mapNotNull { (key, value) ->
            (key as? String)?.let { it to value.convertToJson() }
        }
    )

    is Pair<*, *> -> JsonObject(
        listOf(
            "first" to this.first.convertToJson(),
            "second" to this.second.convertToJson()
        )
    )

    else -> {
        val kClass = this::class
        var jsonFields: List<Pair<String, JsonValue>> = listOf()
        if (kClass.isData) {
            /**
             * Converte um objeto do tipo data Classe.
             * @return JsonObject.
             */
            kClass.primaryConstructor?.parameters?.forEach { p ->
                val properties = kClass.declaredMemberProperties.first { it.name == p.name }
                jsonFields = jsonFields + listOf(properties.name to properties.call(this).convertToJson())
            }
            JsonObject(jsonFields)

        } else {
            /**
             * Mensagem de erro a indicar que não existe uma conversão para esse tipo de objeto.
             * @return IllegalArgumentException.
             */
            throw IllegalArgumentException("Não existe conversão para objeto recebido")
        }
    }
}







