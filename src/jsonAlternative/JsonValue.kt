package jsonAlternative

import visitorToJson.*

/**
 * Representa um valor genérico numa estrutura JSON. Esta é uma interface de marcação para qualquer objeto
 * que possa ser serializado para o formato JSON ou processado por um visitor.
 *
 * As classes que implementam esta interface devem definir como os seus tipos específicos são transformados
 * em strings JSON e como interagem com um `JsonVisitor`.
 */

interface JsonValue {
    /**
     * Converte a instância atual para a sua representação em string JSON.
     *
     * @return Uma string formatada em JSON que representa a instância actual.
     */

    fun toJson() : String

    /**
     * Aceita um `JsonVisitor` e permite que este processe ou percorra a instância actual.
     *
     * O método específico do visitor invocado depende da implementação da interface `JsonValue`.
     *
     * @param visitor A instância de `JsonVisitor` responsável por processar o `JsonValue` actual.
     */

    fun accept(visitor: JsonVisitor)
}