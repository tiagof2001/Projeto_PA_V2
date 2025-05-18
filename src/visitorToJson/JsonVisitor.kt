package visitorToJson

import jsonAlternative.*

/**
 * Define uma interface de visitor para percorrer e processar diferentes tipos de estruturas JSON.
 *
 * Esta interface baseia-se no padrão de design Visitor, permitindo a personalização de comportamentos externos
 * para vários tipos de valores JSON (`JsonObject`, `JsonArray`, `JsonString`, `JsonNumber`, `JsonBoolean` e `JsonNull`)
 * sem necessidade de modificar a sua implementação.
 *
 * As implementações desta interface podem definir lógica de processamento específica para cada tipo de elemento JSON.
 */


interface JsonVisitor {
    /**
     * Processa uma instância de `JsonObject` com a lógica específica definida na classe que implementa.
     *
     * @param obj O `JsonObject` a ser processado pelo visitor implementado.
     */

    fun visitorObject(obj: JsonObject)

    /**
     * Processa uma instância de `JsonArray`, permitindo definir comportamentos ou lógica de percurso
     * específicos para a estrutura do array e os seus elementos.
     *
     * @param array O `JsonArray` a ser processado ou percorrido pelo visitor implementado.
     */

    fun visitorArray(array: JsonArray)

    /**
     * Processa uma instância de `JsonString` com a lógica específica implementada no visitor.
     *
     * @param str A instância de `JsonString` a ser processada pelo visitor implementado.
     */

    fun visitorString(str: JsonString)

    /**
     * Processa uma instância de `JsonNumber` utilizando a lógica específica definida no visitor implementado.
     *
     * @param num A instância de `JsonNumber` a ser processada pelo visitor implementado.
     */

    fun visitorNumber(num: JsonNumber)

    /**
     * Processa uma instância de `JsonBoolean` com a lógica específica definida na implementação do visitor.
     *
     * @param bool A instância de `JsonBoolean` a ser processada pelo visitor implementado.
     */

    fun visitorBoolean(bool: JsonBoolean)

    /**
     * Processa uma instância de `JsonNull` com a lógica específica definida no visitor implementado.
     *
     * @param nullValue A instância de `JsonNull` a ser processada pelo visitor implementado.
     */

    fun visitorNull(nullValue: JsonNull)
}