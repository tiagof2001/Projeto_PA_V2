package httpGetForJson

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class Mapping(
    val url: String
)
