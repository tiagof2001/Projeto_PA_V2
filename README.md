Projeto GetJson e JSON Manipulation Library
Programação avançada 2025

Este projeto consiste numa framework minimalista em Kotlin para criação de endpoints HTTP/GET que retornam JSON, juntamente com uma biblioteca de manipulação de JSON em memória. O objetivo é permitir a conversão automática de objetos de Kotlin para JSON e operações avançadas sosbre modelos JSON, sem dependências externas além das biblibotecas JUnit e uma para testes HTTP.

Funcionalidades:
-
- Serialização de modelos JSON para string compatível com o padrão.
- Manipulação em memória de modelos JSON: filtragem, mapeamento, visitors;
- Validação de objetos e arrays via visitor;
- Conversão automática de tipos Kotlin para JSON (incluindo listas, mapas, enums, data classes e null);
- Definição de endpoints HTTP/GET via anotações (@Mapping, @PathParam, @QueryParam);
- Testes unitários com JUnit;

Bibliotecas adicionais:
-
- org.jetbrains.kotlin:kotlin-reflect:1.9.25 -> Reflection para inferência de tipos e conversão
- junit - Testes unitários
- squareup.okhttp3.okhttp -> Utilização do OkHttp para a realização testes de pedidos API REST

Instruções para utilizar as classes de conversão para JSON:
-
1. object JsonNull(): JsonValue -> Criar uma representação de JSON para valores do tipo null
2. class JsonNumber(val value = number): JsonValue -> Criar uma representação de JSON para valores do tipo inteiro
3. class JsonBoolean(val value = boolean): JsonValue -> Criar uma representação de JSON para valores do tipo boolean
4. class JsonString(val value = string): JsonValue -> Criar uma representação de JSON para valores do tipo string
   Excepções -> Não irá criar, se o conteúdo da value tiver só valores númericos
5. class JsonArray(val value = List<JsonValue>): JsonValue -> Criar uma representação de JSON para uma lista de valores do tipo JsonValue
6. class JsonObject(val value = List<Pair<String, JsonValue>>): JsonValue -> Criar uma representação de JSON para uma lista de pares de um tipo JsonValue, associados à uma key

Manipulação Avançada de JSON:
-
Filtragem e Mapeamento para:
- JsonArray:
  - filter { ... } → retorna novo JsonArray filtrado
  - map { ... } → retorna novo JsonArray mapeado
- JsonObject:
  - filter { ... } → retorna novo JsonObject filtrado
  
Exemplo:
    val array = JsonArray(listOf(JsonNumber(1), JsonNumber(2), JsonNumber(3)))
    val filtered = array.filter { (it as JsonNumber).toJson().toInt() % 2 == 0 }
    println(filtered.toJson()) // [2]

Implementação de Visitores
-
São classes do tipo JsonVisitor como extensão para inplemetação de novas funcionalidades para classes do tipo JsonValue

Visitores implementados:
- Validar se todas as chaves de um JsonObject são únicas e não vazias (VisitorValidateKeys)
- Verificar se todos os elementos de um JsonArray são do mesmo tipo (exceto null) (VisitorArrayElementsSameType)

Criação de novos visitores:
- Para novas validações, implementa um novo visitor do tipo JsonVisitor, dentro da pasta "src/visitorToJson" usando a interface JsonVisitor

Exemplo de utilização de um visitor:
- val obj = JsonObject(listOf("a" to JsonNumber(1), "b" to JsonNumber(2)))
- val visitor = VisitorValidateKeys()
- obj.accept(visitor)
- println(visitor.isValid) // true


Função de extensão convertToJson()
-
fun Any?.convertToJson() : JsonValue - É uma função de extensão que quando executa irá retornar um object do tipo JsonValue corresponde ao objeto que recebe, se não existir um tratamento para esse tipo de objeto, irá lançar um throw IllegalArgumentException a indicar que não existe conversão para esse objeto

Para criar uma nova opção de conversão para JsonValue, deve ir ao ficheiro /src/convertObjectToJson/objectToJson.kt e adicionar uma nova opção seguindo está estrutura: 
"is (tipo de objeto) -> { (operações a ser realizadas) ... return (objeto do tipo JsonValue) }

Caso não exista uma forma direta de verificar o tipo desse objeto, adicionar essa conversão no 1º else neste formato:
if(nova comparação para o tipo de objeto utilizando KClass){ (operações a ser realizadas) ... return (objeto do tipo JsonValue) }

Exemplos de Conversão
1. Converter uma string para JSON:
   - val jsonString = "Olá Mundo".convertToJson()
   - println(jsonString.toJson())  // Saída: "Olá Mundo"
 
2. Converter null para Json
   - val jsonNull = null.convertToJson()
   - println(jsonNull.toJson()) // Saída: null

3. Converter Boolean para Json
   - val jsonBoolean = true.convertToJson()
   - println(jsonBoolean.toJson()) // Saída: true

4. Converter um número para JSON:
   - val jsonNumber = 42.convertToJson()
   - println(jsonNumber.toJson())  // Saída: 42

5. Converter uma lista para JSON:
   - val jsonArray = listOf("A", "B", "C").convertToJson()
   - println(jsonArray.toJson())  // Saída: ["A", "B", "C"]

6. Converter uma lista de pares<String, *> para JSON:
   - val jsonlist = listOf("chave1" to "valor1", "chave2" to 2).convertToJson()
   - println(jsonlist.toJson())  // Saída: {"chave1": "valor1", "chave2": 2}

Framweork HTTP/GET: Criar Endpoinss REST
-------------------------

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

Diagrama UML
-
![Diagrama UML](https://github.com/user-attachments/assets/9ddf5d08-9954-473d-9eb9-9f0ac2f5aeae)


