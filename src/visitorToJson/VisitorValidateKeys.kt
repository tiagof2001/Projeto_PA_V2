package visitorToJson

import jsonAlternative.*

/**
 * Implementação de visitor para validar as chaves de um `JsonObject`.
 *
 * Esta classe verifica se todas as chaves num `JsonObject` cumprem as seguintes condições:
 * - Nenhuma das chaves está em branco.
 * - Todas as chaves são únicas no objeto.
 *
 * A propriedade `isValid` é definida como `true` se as chaves forem válidas de acordo com estas regras,
 * e `false` caso contrário.
 */

class VisitorValidateKeys : JsonVisitor {
    var isValid = true

    /**
     * Verifica a validade das chaves em um `JsonObject`.
     *
     * O método verifica se todas as chaves do `JsonObject` não estão em branco e se não há chaves duplicadas.
     * Define a propriedade `isValid` da classe como `true` se todas as chaves forem válidas
     * e como `false` caso contrário.
     *
     * @param obj O `JsonObject` cujas chaves serão validadas.
     */
    override fun visitorObject(obj: JsonObject) {
        val keys = obj.getMembers().map { it.first }
        isValid = keys.all { it.isNotBlank() } && keys.size == keys.toSet().size

    }

    override fun visitorArray(array: JsonArray) {}
    override fun visitorString(str: JsonString) {}
    override fun visitorNumber(num: JsonNumber) {}
    override fun visitorBoolean(bool: JsonBoolean) {}
    override fun visitorNull(nullValue: JsonNull) {}
}
