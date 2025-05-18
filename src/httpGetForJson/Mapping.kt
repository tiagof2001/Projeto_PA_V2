package httpGetForJson

@Target(AnnotationTarget.CLASS,AnnotationTarget.FUNCTION,AnnotationTarget.VALUE_PARAMETER)
annotation class Mapping(val path: String = "")