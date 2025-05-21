package jsonAlternative

import visitorToJson.JsonVisitor

/**
 * Representa um objeto JSON como uma coleção de pares chave-valor, onde cada chave é única e associada a um valor do tipo `JsonValue`.
 * Permite operações como filtragem, serialização para string JSON e aceitação de visitors para processamento personalizado.
 *
 * @constructor Inicializa uma instância de `JsonObject` validando que todas as chaves são únicas. Caso contrário, será lançada uma exceção.
 * @param members Lista de pares chave-valor que compõem o conteúdo do objeto JSON.
 */

class JsonObject(private val members: List<Pair<String, JsonValue>>) : JsonValue {

    /**
     * Inicializa um JsonObject validando que não existem chaves duplicadas na lista de membros fornecida.
     *
     * @throws IllegalArgumentException se forem encontradas chaves duplicadas na lista de membros.
     *         A mensagem da exceção incluirá a lista das chaves duplicadas.
     */

    init {
        val countKeys = members.groupingBy { it.first }.eachCount()
        val duplicates = countKeys.filter { it.value > 1 }.keys
        if (duplicates.isNotEmpty()) {
            throw IllegalArgumentException("Chaves duplicadas não são permitidas: ${duplicates.joinToString(", ")}")
        }
    }

    /**
     * Obtém o conteúdo que está associado a uma key
     *
     * @return Retorna um objeto do tipo JsonValue
     *
     * @exception NoSuchElementException Caso não exista uma key com esse conteúdo
     */
    fun getJsonValue(key: String): JsonValue {
        return members.firstOrNull { it.first == key }
            ?.second
            ?: throw NoSuchElementException("A chave '$key' não existe no JsonObject.")
    }

    /**
     * Filtra os membros do JsonObject com base num predicado fornecido.
     *
     * @param predicate Uma função que determina se um par chave-valor (do tipo Pair<String, JsonValue>)
     *                  deve ser incluído no JsonObject resultante. A função deve devolver true para incluir
     *                  o par e false para o excluir.
     * @return Um novo JsonObject contendo apenas os pares chave-valor que satisfazem o predicado fornecido.
     */

    fun filter(predicate: (Pair<String, JsonValue>) -> Boolean): JsonObject =
        JsonObject(members.filter(predicate))

    /**
     * Converte a instância de `JsonObject` para a sua representação em string JSON.
     *
     * O método percorre cada par chave-valor na propriedade `members` do `JsonObject`,
     * serializa-os para o formato JSON e combina-os numa única string de objecto JSON.
     *
     * @return Uma string que representa os dados codificados em JSON da instância atual de `JsonObject`.
     */

    override fun toJson(): String =
        members.joinToString(prefix = "{", postfix = "}") {
                (key, value) -> "\"$key\": ${value.toJson()}"
        }

    /**
     * Aceita um `JsonVisitor` e permite que o visitor processe esta instância de `JsonObject`
     * e todos os seus membros de forma recursiva.
     *
     * O método `visitorObject` do `JsonVisitor` é chamado para este objeto, e o método `accept`
     * é posteriormente chamado para o valor de cada membro.
     *
     * @param visitor Uma implementação de `JsonVisitor` que define o comportamento para processar
     *                este `JsonObject` e os seus membros.
     */

    override fun accept(visitor: JsonVisitor) {
        visitor.visitorObject(this)

        for ((_, value) in getMembers()) {
            value.accept(visitor)
        }
    }

    /**
     * Obtém a lista de pares chave-valor (membros) contidos neste `JsonObject`.
     *
     * @return Uma lista de pares, em que cada par é composto por uma chave do tipo `String` e o respectivo `JsonValue` associado.
     */

    fun getMembers(): List<Pair<String, JsonValue>> = members
}
