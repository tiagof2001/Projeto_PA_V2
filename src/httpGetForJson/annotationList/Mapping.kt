package httpGetForJson.annotationList

/**
 * Anotação utilizada para definir um mapeamento para o tratamento de pedidos HTTP.
 *
 * Esta anotação é aplicada ao nível da classe ou da função para especificar
 * o mapeamento de caminho para um determinado controlador ou rota específica. Pode
 * também ser usada em parâmetros de função com outras anotações como
 * `@PathParam` ou `@QueryParam` para extrair partes específicas dos pedidos HTTP.
 *
 * @property path Define o caminho relativo para o mapeamento do endpoint HTTP.
 *                O valor por omissão é uma string vazia, que pode ser usada para mapeamentos de raiz ou base.
 */

@Target(AnnotationTarget.CLASS,AnnotationTarget.FUNCTION,AnnotationTarget.VALUE_PARAMETER)
annotation class Mapping(val path: String = "")