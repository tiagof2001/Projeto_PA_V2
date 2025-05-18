package jsonAlternative

import visitorToJson.JsonVisitor

/**
 * @constructor
 * Criar um objecto do tipo json_alternative. JsonValue recebendo
 * como parametro um objeto do tipo List, aceitando objetos json_alternative. JsonValue
 */

/**
 * Representa um array JSON. É composto por uma lista de elementos `JsonValue` e disponibiliza
 * operações para filtrar, mapear e interagir com o seu conteúdo. O array suporta ainda
 * a serialização para uma string JSON válida e pode ser percorrido utilizando o padrão visitor.
 *
 * @constructor Inicializa o `JsonArray` com uma lista fornecida de elementos `JsonValue`.
 * @param value A lista de elementos `JsonValue` contidos neste array.
 */

class JsonArray(private var value: List<JsonValue>) : JsonValue {

    /**
     * Filtra os elementos do JsonArray baseado nos critérios estabelecidos.
     *
     * @param predicate Uma função que recebe um JsonValue e devolve um boolean.
     *                  A função deve devolver o valor 'true' para os elementos a serem incluídos no JsonArray final.
     *
     * @return Um novo JsonArray que contem apenas os elementos que satisfazem os critérios estabelecidos.
     */

    fun filter(predicate: (JsonValue) -> Boolean): JsonArray = JsonArray(value.filter(predicate))

    /**
     *  O JsonArray vai ser transformado e vai ser applicada uma função expecífica a cada elemento
     *
     *  @param transform Transforma uma função que recebe um JsonValue como entrada e devolve um JsonValue transformado.
     *  @return Um novo JsonArray que contem os elementos transformados.
     */

    fun map(transform: (JsonValue) -> JsonValue): JsonArray = JsonArray(value.map(transform))

    /**
     * Converte a instância actual de JsonArray para a sua representação em string JSON.
     *
     * A string resultante é formatada como um array JSON, sendo que cada elemento é
     * serializado através da chamada do método `toJson` da respectiva instância de `JsonValue`.
     *
     * @return Uma string que representa o array JSON no formato padrão JSON.
     */


    override fun toJson(): String = value.joinToString(prefix = "[", postfix = "]") { it.toJson() }

    /**
     * Aceita um `JsonVisitor` para percorrer ou processar a estrutura do `JsonArray`.
     *
     * O método `visitorArray` do visitante é invocado para este `JsonArray`, seguido do
     * percurso e visita de cada valor presente no array, utilizando os respectivos métodos `accept`.
     *
     * @param visitor Implementação de `JsonVisitor` que define o comportamento para processar
     *                ou percorrer diferentes tipos de valores JSON.
     */


    override fun accept(visitor: JsonVisitor) {

        visitor.visitorArray(this)
        for (value in getValues()) {
            value.accept(visitor)
        }
    }

    /**
     * Obtém a lista subjacente de elementos `JsonValue` contidos no `JsonArray`.
     *
     * @return Uma lista de elementos `JsonValue` que representa o conteúdo do `JsonArray`.
     */

    fun getValues(): List<JsonValue> = value
}