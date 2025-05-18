package httpGetForJson.annotationList

/**
 * Anotação utilizada para extrair uma variável específica do caminho (path) do URI de um pedido HTTP.
 *
 * Esta anotação é tipicamente aplicada a parâmetros de função em combinação com
 * a anotação `@Mapping`, permitindo o routing e a ligação de variáveis de caminho aos argumentos
 * do método. O parâmetro `name` em `@PathParam` corresponde ao nome do placeholder
 * declarado no template de URI definido por `@Mapping`.
 *
 * Por exemplo, se um endpoint for definido com um template de caminho `"/path/{var}"`,
 * um parâmetro anotado com `@PathParam("var")` irá receber o valor correspondente
 * a `{var}` do pedido HTTP.
 *
 * @property name O nome da variável de caminho do URI a ser extraída e ligada ao parâmetro.
 *                O nome deve corresponder a um placeholder definido no template de caminho.
 */

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class PathParam(val name: String = "")
