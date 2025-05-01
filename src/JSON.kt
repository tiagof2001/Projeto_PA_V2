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
}

    /**
     *  Os objetos são especificados entre chaves e podem ser compostos por múltiplos pares nome/valor
     *  {
     *   “titulo”: “JSON x XML”,
     *   “resumo”: “o duelo de dois modelos de representação de informações”,
     *   “ano”: 2012,
     *   “genero”: [“aventura”, “ação”, “ficção”]
     *  }
    */

class JsonObject(private val members: Map<String, JsonValue>) : JsonValue {

    fun filter(predicate: (Map.Entry<String, JsonValue>) -> Boolean): JsonObject {
        return JsonObject(members.filter(predicate))
    }

    override fun toJson(): String {
        return members.entries.joinToString(prefix = "{", postfix = "}") { (key, value) ->
            "\"$key\": ${value.toJson()}"
        }
    }
}

/**
 * @constructor
 * Criar um objecto do tipo JsonValue recebendo como parametro um objeto do tipo List, aceitando objetos JsonValue
 */
class JsonArray(private var value: List<JsonValue>) : JsonValue {

    fun filter(predicate: (JsonValue) -> Boolean): JsonArray {
        return JsonArray(value.filter(predicate))
    }

    fun map(transform: (JsonValue) -> JsonValue): JsonArray {
        return JsonArray(value.map(transform))
    }

    override fun toJson(): String {
        return value.joinToString(prefix = "[", postfix = "]") { it.toJson() }
    }
}

/**
 * @constructor
 * Criar um objecto do tipo JsonValue recebendo como parametro um objeto do tipo String
 */
class JsonString(private var value: String) : JsonValue{

    //Existe possibilidade de ser removido
    fun setValue(newValue: String){
        value = newValue
    }
    //Existe possibilidade de ser removido
    fun getValueVariavel(): String{
        return value
    }

    override fun toJson(): String {
        return "\"${value}\""
    }
}

/**
 * @constructor
 * Criar um objecto do tipo JsonValue recebendo como parametro um objeto do tipo Number
 */
class JsonNumber(private var value: Number) : JsonValue{
    //Existe possibilidade de ser removido
    fun setValue(newValue: Number){
        value = newValue
    }
    //Existe possibilidade de ser removido
    fun getValueVariavel(): Number{
        return value
    }

    override fun toJson(): String {
        return value.toString()
    }
}

/**
 * @constructor
 * Criar um objecto do tipo JsonValue recebendo como parametro um objeto do tipo boolean (True/False)
 */
class JsonBoolean(private var value: Boolean) : JsonValue{
    //Existe possibilidade de ser removido
    fun setValue(newValue: Boolean){
        value = newValue
    }
    //Existe possibilidade de ser removido
    fun getValueVariavel(): Boolean{
        return value
    }

    override fun toJson(): String {
        return value.toString()
    }
}

/**
 * @constructor
 * Criar um objecto do tipo JsonValue contendo este valor -> Null
 */
object JsonNull : JsonValue {
    override fun toJson(): String = "null"
}


