package jsonAlternative

import visitorToJson.JsonVisitor

/**
 * @constructor
 * Criar um objecto do tipo json_alternative.JsonValue recebendo como parametro um objeto do tipo String
 *
 * @exception
 * Caso o conteúdo que recebe for do tipo Number
 */
class JsonString(private var value: String) : JsonValue{

    /**
     * Converte a instância actual para a sua representação em string JSON.
     *
     * @return Uma string JSON que representa a instância atual, com o valor da string
     *         devidamente codificado e entre aspas duplas.
     */

    init {
        if(value.matches(Regex("-?\\d+(\\.\\d+)?"))){
            throw NumberFormatException("\"$value\" é um número, adicionar um caractere ao \"$value\" ou usa a classe JsonNumber")
        }


    }

    override fun toJson(): String = "\"${value}\""

    /**
     * Aceita um `JsonVisitor` para processar ou percorrer a instância actual de `JsonString`.
     *
     * Este método invoca o método `visitorString` do visitor fornecido, passando a instância atual.
     *
     * @param visitor Uma implementação de `JsonVisitor` que define o comportamento para tratar instâncias de `JsonString`.
     */

    override fun accept(visitor: JsonVisitor) {
        visitor.visitorString(this)
    }
}