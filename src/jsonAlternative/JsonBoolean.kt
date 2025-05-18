package jsonAlternative

import visitorToJson.JsonVisitor

/**
 * @constructor
 * Criar um objecto do tipo json_alternative.JsonValue
 * recebendo como parametro um objeto do tipo boolean (True/False)
 */

/**
 * Representa um valor booleano JSON num modelo de dados JSON. Esta classe permite a gestão,
 * manipulação e serialização de valores booleanos compatíveis com JSON.
 *
 * @property value O valor booleano encapsulado pela instância de JsonBoolean.
 */


class JsonBoolean(private var value: Boolean) : JsonValue{

    /**
     * Converte a instância atual do JsonBoolean para a sua representação numa string JSON
     *
     * O output é "true" para um valor booleano verdadeiro e "falso" para um valor booleano falso
     *
     * @return Uma representação em string compatível com JSON do valor do boolean.
     */

    override fun toJson(): String = value.toString()

    /**
     * Aceita um objecto visitor que implementa a interface JsonVisitor.
     *
     * Este método invoca o método `visitorBoolean` do visitor, passando a instância actual
     * de JsonBoolean para permitir o processamento ou tratamento do seu tipo específico.
     *
     * @param visitor A instância de JsonVisitor responsável por tratar o JsonBoolean actual.
     */

    override fun accept(visitor: JsonVisitor) {
        visitor.visitorBoolean(this)
    }
}