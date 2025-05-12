import jsonAlternative.*
import visitorToJson.*
import httpGetForJson.*
import junit.framework.TestCase.assertEquals
import org.junit.Test

import java.io.IOException
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

@Mapping("api")
class Controller {
    @Mapping("ints")
    fun demo(): List<Int> = listOf(1, 2, 3)

    @Mapping("pair")
    fun obj(): Pair<String, String> = Pair("um", "dois")

    @Mapping("path/{pathvar}")
    fun path(
        @Path pathvar: String
    ): String = "$pathvar!"

    @Mapping("args")
    fun args(
        @Param n: Int,
        @Param text: String
    ): Map<String, String> = mapOf(text to text.repeat(n))
}

/**
 * /api/ints [1, 2, 3]
 * /api/pair {"first": "um", "second": "dois"}
 * /api/path/a "a!"
 * /api/path/b "b!"
 * /api/args?n=3&text=PA {"PA": "PAPAPA"}
 */

class TestFase3 {


}

fun main() {
    val app = GetJson(listOf(Controller::class))
    app.start(8080)



    /**
     * fun request(caminho: String) -> retorna o conteúdo desse caminho executando as funções,
     * depois pegar nesses resultados e converter para json
     */





}
