package httpGetForJson.annotationList

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class PathParam(val name: String = "")
