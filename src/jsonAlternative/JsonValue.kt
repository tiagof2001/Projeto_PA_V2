package jsonAlternative

import visitorToJson.*

/**
 * Goal: Develop classes to represent JSON values (model), allowing manipulation operations
 * and serialization to strings (standard format).
 * {
 *   "nome_variavel": value
 * }
 * The API of the library classes must allow programmers to:
 * ● Instantiate JSON values programmatically, creating and composing objects from the library classes;
 * Objects
 * Arrays
 * Strings
 * Numbers
 * Booleans
 * Null
 * ● Perform filtering that produces new JSON Objects or Arrays without changing existing ones:
 * ○ JSON Object → JSON Object
 * ○ JSON Array → JSON Array
 * ● Perform mapping operations on Arrays (map):
 * ○ JSON Array → JSON Array
 * ● Use visitors, to facilitate the developed of new features that involve traversing the structure recursively.
 * This characteristic should be illustrated with functionalities to:
 * ○ Validate whether all JSON objects are valid (keys with valid content and unique);
 * ○ Check if all JSON Arrays contain values of the same type (not Null)
 * ● Serialize a model to a string, ensuring compatibility with the standard.
 * (Formatting issues are not very relevant, as long as the output strings are valid JSON).
 */


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