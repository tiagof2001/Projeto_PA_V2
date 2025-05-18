package visitorToJson

import jsonAlternative.*

/**
 * Implementação de Visitor que verifica se todos os elementos de um array JSON são do mesmo tipo.
 *
 * Esta classe percorre uma estrutura `JsonArray` e determina se todos os elementos não nulos (`JsonNull`)
 * partilham o mesmo tipo. O resultado é armazenado na propriedade `isSameType`.
 *
 * O visitor não processa outras estruturas JSON como `JsonObject`, `JsonString`, `JsonNumber`,
 * `JsonBoolean` ou `JsonNull`.
 *
 * @constructor Inicializa o visitor com propriedades predefinidas. Por predefinição, `isSameType` é definido como `true`.
 *
 * @property isSameType Armazena o resultado da comparação de tipos dos elementos do array. Inicialmente é `true`, mas
 *                      pode mudar para `false` se o array contiver elementos de tipos diferentes.
 *
 * @see JsonVisitor
 * @see JsonArray
 */

class VisitorArrayElementsSameType : JsonVisitor {
    /**
     * Indica se todos os elementos não nulos num `JsonArray` partilham o mesmo tipo.
     *
     * Esta propriedade é inicialmente definida como `true` e reflete o resultado do percurso
     * de um `JsonArray` usando o `VisitorArrayElementsSameType`. Se algum par de elementos
     * no array tiver tipos diferentes, o valor é atualizado para `false`.
     *
     * É utilizada principalmente durante o ciclo de vida do visitor para validar a consistência
     * de tipos entre os elementos do array.
     */

    private var isSameType = true
    fun getIsSameType(): Boolean = isSameType
    /**
     * Processa um `JsonArray` para determinar se todos os elementos não nulos têm o mesmo tipo.
     *
     * Este método filtra os elementos nulos do array (`JsonNull`) e avalia se todos
     * os elementos restantes pertencem à mesma classe. O resultado desta avaliação atualiza
     * a propriedade `isSameType` do visitor.
     *
     * @param array O `JsonArray` a ser processado para verificar a consistência de tipos entre elementos.
     */

    override fun visitorArray(array: JsonArray) {
        val elements = array.getValues().filter { it != JsonNull }
        if (elements.isNotEmpty()) {
            isSameType = elements.all { it::class == elements.first()::class }
        }
    }

    override fun visitorObject(obj: JsonObject) {}
    override fun visitorString(str: JsonString) {}
    override fun visitorNumber(num: JsonNumber) {}
    override fun visitorBoolean(bool: JsonBoolean) {}
    override fun visitorNull(nullValue: JsonNull) {}
}