package httpGetForJson
import jsonAlternative.*
import kotlin.reflect.KClass

/*
    O framework deve ser lançado enumerando uma lista de controladores REST
    que definem endpoints para requisições HTTP/GET.
    Os controladores definem mapeamentos de URL através de anotações, permitindo
    que os programadores mapeiem segmentos de caminho e argumentos em argumentos de função.

    Classe como input uma lista de controladores
    -> Verificar se tem uma anotação?
 */


class GetJson(private val listControls: List<KClass<*>>) {

    private var onlineStatus = false;

    init {
        // Verificar se a lista não está vazia
        if (listControls.isNotEmpty()) {
            throw IllegalArgumentException("Lista de controladores vazia")
        }
        // Verificar a existência de uma certa anotação
        // Verificar se o primeiro mapping contem valores diferentes entre "inputs"
    }

    private var numberPort = -1;

    fun start(i: Int) {
        if (i < 0) throw IllegalArgumentException("Erro na introdução do número da porta: Porta deve ter um número superior a 0")

        // criar verificação se a porta já está a ser utilizada
        numberPort = i;
        onlineStatus = true;
    }


    fun url(caminho: String) : JsonValue{
        if (!onlineStatus) throw IllegalStateException("Erro ao obter url: Servidor offline")

        /*
            Verificar na lista de controladores o url
            Percorre as anotações com a lista
            -> /api/ints -> [api, ints]
            -> /api/pair -> [api, pair]
            -> /api/path/a -> [api, path/a]
            -> /api/path/b -> [api, path/b]
            -> /api/args?n=3&text=PA -> [api / args?n=3&text=PA]

            Cria exception para caso não existir esse caminho
         */

        val result = caminho.toString()

        return convertToJson(result)
    }
}
