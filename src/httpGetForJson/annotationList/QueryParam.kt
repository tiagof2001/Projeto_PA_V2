package httpGetForJson.annotationList

/**
 * Anotação utilizada para extrair parâmetros de query de um pedido HTTP.
 *
 * Esta anotação é tipicamente aplicada a parâmetros de função em conjunto
 * com a anotação `@Mapping`. Permite que o valor de um parâmetro de query no URI do pedido
 * seja mapeado para um argumento específico na função correspondente.
 *
 * A propriedade `name` desta anotação específica a chave do parâmetro de query que
 * deve ser extraída do URI do pedido. Se o `name` for deixado como string vazia,
 * é utilizado por omissão o nome do parâmetro.
 *
 * Exemplo:
 * Se um endpoint esperar um URI como `/endpoint?param=valor`,
 * um parâmetro de função anotado com `@QueryParam("param")` será definido com o
 * valor correspondente ao parâmetro de query `param`.
 *
 * @property name O nome do parâmetro de query no URI do pedido a extrair. Se omitido,
 *                é utilizado por omissão o nome do parâmetro.
 */

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class QueryParam(val name: String = "")