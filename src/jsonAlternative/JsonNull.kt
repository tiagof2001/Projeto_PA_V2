package jsonAlternative
import visitorToJson.JsonVisitor

/**
 * Representa um valor `null` em JSON. É utilizado para modelar o conceito de `null`
 * em estruturas JSON.
 *
 * O objeto `JsonNull` implementa a interface `JsonValue`, disponibilizando métodos
 * para serializar o valor `null` para a sua representação em string JSON e para aceitar
 * um `JsonVisitor` que permita percorrer ou processar o valor.
 *
 *  @constructor
 *  Criar um objecto do tipo json_alternative.JsonValue contendo este valor -> Null
 */


object JsonNull : JsonValue {
    /**
     * Converte a instância nula atual na sua representação numa string JSON.
     *
     * @return Uma string que representa o equivalente em JSON da instância atual,
     *          que vai identificar o valor como null.
     */
    override fun toJson(): String = "null"

    /**
     * Aceita um `JsonVisitor` para processar ou percorrer a instância actual de `JsonNull`.
     *
     * O método invoca o método `visitorNull` do visitor fornecido, passando a instância actual.
     *
     * @param visitor Uma implementação de `JsonVisitor` que define o comportamento para o tipo `JsonNull`.
     */

    override fun accept(visitor: JsonVisitor) {
        visitor.visitorNull(this)
    }
}


