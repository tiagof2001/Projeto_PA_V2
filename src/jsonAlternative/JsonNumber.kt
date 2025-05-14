package jsonAlternative

import visitorToJson.JsonVisitor

/**
 * Representa um valor numérico no modelo JSON.
 *
 * A classe `JsonNumber` encapsula um valor numérico e disponibiliza funcionalidades para
 * serializar esse valor para a sua representação em string JSON. Implementa a interface
 * `JsonValue`, permitindo a integração num modelo JSON mais amplo e num framework de processamento baseado no padrão visitor.
 *
 * @constructor Criar um objeto do tipo json_alternative.JsonValue recebendo como parâmetro um objeto do tipo Number.
 */

class JsonNumber(private var value: Number) : JsonValue{

    /**
     * Converte o valor numérico atual para a sua representação em uma string JSON.
     *
     * @return Uma string que representa o número no formato JSON.
     */

    override fun toJson(): String = value.toString()

    /**
     * Aceita um `JsonVisitor` para processar ou percorrer a instância actual de `JsonNumber`.
     *
     * Este método invoca o método `visitorNumber` do visitor fornecido,
     * passando a instância actual de `JsonNumber`.
     *
     * @param visitor Uma implementação de `JsonVisitor` que define o comportamento para o tipo `JsonNumber`.
     */

    override fun accept(visitor: JsonVisitor) {
        visitor.visitorNumber(this)
    }
}