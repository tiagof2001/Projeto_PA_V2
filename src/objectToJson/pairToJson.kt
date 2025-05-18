package objectToJson

import jsonAlternative.JsonObject
import jsonAlternative.JsonValue
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor

fun Pair<*, *>.convertToJson() : JsonValue {

    var jsonFields: List<Pair<String, JsonValue>> = listOf()
    val kClass = this::class as KClass<*>
    kClass.primaryConstructor?.parameters?.forEach { p ->
        val properties = kClass.declaredMemberProperties.first { it.name == p.name }
        jsonFields = jsonFields + listOf(properties.name to properties.call(p).convertToJson())
    }
    return JsonObject(jsonFields)
}