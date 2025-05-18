package httpGetForJson.annotationList

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class QueryParam(val name: String = "")