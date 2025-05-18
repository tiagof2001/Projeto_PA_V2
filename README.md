Projeto GetJson e JSON Manipulation Library
Programação avançada 2025

Este projeto consiste numa framework minimalista em Kotlin para criação de endpoints HTTP/GET que retornam JSON, juntamente com uma biblioteca de manipulação de JSON em memória. O objetivo é permitir a conversão automática de objetos de Kotlin para JSON e operações avançadas sosbre modelos JSON, sem dependências externas além das biblibotecas JUnit e uma para testes HTTP.

Funcionalidades:
- Conversão automática de tipos Kotlin para JSON (incluindo listas, mapas, enums, data classes e null);
- Manipulação em memória de modelos JSON: filtragem, mapeamento, visitors;
- Definição de endpoints HTTP/GET via anotações (@Mapping, @PathParam, @QueryParam);
- Validação de objetos e arrays via visitor;
- Testes unitários com JUnit;
- - Serialização de modelos JSON para string compatível com o padrão.

Instruções de utilização

Bibliotecas adicionais:
- org.jetbrains.kotlin:kotlin-reflect:1.9.25 -> Refelction para inferência de tipos e conversão
- junit - Testes unitários
- squareup.okhttp3.okhttp -> Utilização do OkHttp para a realização testes de pedidos API REST

Instruções para utilizar as classes de conversão para JSON:
A função de extensão convertToJson() permite converter automaticamente objetos Kotlin suportados para o modelo JSON:
Exemplos de Conversão
1. Converter uma string para JSON:
  val jsonString = "Olá Mundo".convertToJson()
  println(jsonString.toJson())  // Saída: "Olá Mundo"
 
2. Converter um número para JSON:
  val jsonNumber = JsonNumber(42)
  println(jsonNumber.toJson())  // Saída: 42

3. Converter uma lista para JSON:
   val jsonBoolean = listOf("A", "B", "C")
   println(jsonList.toJson())  // Saída: ["A", "B", "C"]

4. Converter null para Json
   val jsonNull = null.convertToJson()
   println(jsonNull.toJson()) // Saída: null

5. Converter Boolean para Json
   val jsonBoolean = true.convertToJson()
   println(jsonBoolean.toJson()) // Saída: true

6. Converter uma lista de pares<String, JsonValue> para JSON:
   val jsonlist = listOf("chave1" to "valor1", "chave2" to 2).convertToJson()
   println(jsonlist.toJson())  // Saída: {"chave1": "valor1", "chave2": 2}

7. Converter um array para JsonArray:
   val jsonArray = JsonArray(listOf(JsonString("A"), JsonNumber(1), JsonBoolean(true)))
   println(jsonArray.toJson())  // Saída: ["A", 1, true]

8. Filtrar um JsonArray:
   val filteredArray = jsonArray.filter { it is JsonNumber }
   println(filteredArray.toJson())  // Saída: [1]


fun (...).convertToJson() - É uma função de extensão que quando executa irá retornar um object do tipo JsonValue corresponde ao objeto que recebe, se não existir um tratamento para esse tipo de objeto, irá lançar um throw IllegalArgumentException a indicar que não existe conversão para esse objeto

Para criar uma nova opção de conversão para JsonValue, deve criar um novo ficheiro (Nota: tem de estar localizado na mesma pasta onde está localizado a função), criar uma nova função, seguindo está estrutura "fun (tipo de objeto).convertToJson() : JsonValue { (operações a ser realizadas) return (objeto do tipo JsonValue) }

Manipulação Avançada de JSON:

Filtragem e Mapeamento
- JsonArray:
    - filter { ... } → retorna novo JsonArray filtrado
    - map { ... } → retorna novo JsonArray mapeado
- JsonObject:
  - filter { ... } → retorna novo JsonObject filtrado
Exemplo:
    val array = JsonArray(listOf(JsonNumber(1), JsonNumber(2), JsonNumber(3)))
    val filtered = array.filter { (it as JsonNumber).toJson().toInt() % 2 == 0 }
    println(filtered.toJson()) // [2]

Vistors:
Os visitors são implementados e utilizados para:
- Validar se todas as chaves de um JsonObject são únicas e não vazias (VisitorValidateKeys)
- Verificar se todos os elementos de um JsonArray são do mesmo tipo (exceto null) (VisitorArrayElementsSameType)
Exemplo:
val obj = JsonObject(listOf("a" to JsonNumber(1), "b" to JsonNumber(2)))
  val visitor = VisitorValidateKeys()
  obj.accept(visitor)
  println(visitor.isValid) // true

Framweork HTTP/GET: Criar Endpoinss REST

Definir Controllers e Rotas
Utiliza as anotações fornecidas para mapeaer endpoints:
@Mapping("api")
class Controller {
@Mapping("ints")
fun demo(): List<Int> = listOf(1, 2, 3)

    @Mapping("pair")
    fun obj(): Pair<String, String> = Pair("um", "dois")

    @Mapping("path/{pathvar}")
    fun path(@PathParam("pathvar") pathvar: String): String = pathvar + "!"

    @Mapping("args")
    fun args(@QueryParam("n") n: Int, @QueryParam("text") text: String): Map<String, String> =
        mapOf(text to text.repeat(n))
}
As anotações disponíveis são:
- @Mapping(path: String): Define o caminho do endpoint (classe ou função).
- @PathParam(name: String): Liga um parâmetro do caminho ao argumento da função.
- @QueryParam(name: String): Liga um parâmetro da query string ao argumento da função.

Iniciar o servidor
fun main() {
val app = GetJson(Controller::class)
app.start(8080)
}

Extensão e Personalização
- Para adicionar suporte a novos tipos, criar uma função de extensão convertToJson() para o tipo pretendido, na mesma pasta do ficheiro principal de conversão.
- Para novas validações, implementa um novo visitor