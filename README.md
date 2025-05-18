Instruções de utilização

Bibliotecas adicionais:
- org.jetbrains.kotlin:kotlin-reflect:1.9.25 -> ...
- squareup.okhttp3.okhttp -> Utilização do OkHttp para a realização testes de pedidos API REST

Instruções para utilizar as classes de conversão para JSON:
1. Converter uma string para JSON:
  val jsonString = "Olá Mundo".convertToJson()
  println(jsonString.toJson())  // Saída: "Olá Mundo"
 
2. Converter um número para JSON:
  val jsonNumber = JsonNumber(42)
  println(jsonNumber.toJson())  // Saída: 42

3. Converter uma boolean para JSON:
   val jsonBoolean = listOf("A", "B", "C")
   println(jsonList.toJson())  // Saída: ["A", "B", "C"]

4. Converter null para Json
   json
6. 

7. Converter uma lista de pares<String, JsonValue> para JSON:
   val jsonlist = listOf("chave1" to "valor1", "chave2" to 2).convertToJson()
   println(jsonlist.toJson())  // Saída: {"chave1": "valor1", "chave2": 2}

8. Converter um array para JsonArray:
   val jsonArray = JsonArray(listOf(JsonString("A"), JsonNumber(1), JsonBoolean(true)))
   println(jsonArray.toJson())  // Saída: ["A", 1, true]

9. Filtrar um JsonArray:
   val filteredArray = jsonArray.filter { it is JsonNumber }
   println(filteredArray.toJson())  // Saída: [1]

fun (...).convertToJson() - É uma função de extensão que quando executa irá retornar um object do tipo JsonValue corresponde ao objeto que recebe, se não existir um tratamento para esse tipo de objeto, irá lançar um throw IllegalArgumentException a indicar que não existe conversão para esse objeto

Para criar uma nova opção de conversão para JsonValue, deve criar um novo ficheiro (Nota: tem de estar localizado na mesma pasta onde está localizado a função), criar uma nova função, seguindo está estrutura "fun (tipo de objeto).convertToJson() : JsonValue { (operações a ser realizadas) return (objeto do tipo JsonValue) }




