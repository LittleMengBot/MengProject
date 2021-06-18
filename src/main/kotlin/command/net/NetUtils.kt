package command.net

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import java.net.InetSocketAddress
import java.net.Proxy

object NetUtils {
    fun sendGet(
        url: String,
        withTor: Boolean = false,
        method: String? = null,
        header: Map<String, Any>? = null,
        args: List<Pair<String, String>>? = null
    ): String? {
        if (withTor){
            val torAddress = InetSocketAddress("127.0.0.1", 9050)
            FuelManager.instance.proxy = Proxy(Proxy.Type.SOCKS, torAddress)
            FuelManager.instance.timeoutInMillisecond = 30_000
        }
        var head = mapOf<String, Any>()
        if (header != null) {
            head = header
        }
        val sb = StringBuilder(url)
        if (method != null) sb.append(method)
        val (_, _, result) = sb.toString()
            .httpGet(args)
            .header(head)
            .responseString()
        return when (result) {
            is Result.Failure -> {
                val ex = result.getException()
                println(result.error)
                ex.printStackTrace()
                null
            }
            is Result.Success -> {
                result.get()
            }
        }
    }

    fun sendPost(url: String, method: String, header: Map<String, Any>? = null, requestJson: String): String? {
        var head = mapOf<String, Any>()
        if (header != null) {
            head = header
        }
        val (_, _, result) = "$url$method"
            .httpPost()
            .body(requestJson)
            .header(head)
            .responseString()
        return when (result) {
            is Result.Failure -> {
                val ex = result.getException()
                ex.printStackTrace()
                null
            }
            is Result.Success -> {
                result.get()
            }
        }
    }
}