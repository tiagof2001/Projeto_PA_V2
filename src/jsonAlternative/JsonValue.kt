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

interface JsonValue {
    fun toJson() : String
    fun accept(visitor: JsonVisitor)
}