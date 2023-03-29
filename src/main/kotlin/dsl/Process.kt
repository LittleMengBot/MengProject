package dsl

import mu.KotlinLogging
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

private val logger = KotlinLogging.logger {}
fun Process.execListener(): String? {
    return try {
        val bf = BufferedReader(InputStreamReader(this.inputStream))
        var line: String?
        val sb = StringBuilder()
        while (bf.readLine().also { line = it } != null) {
            sb.append(line)
        }
        sb.toString()
    } catch (e: IOException) {
        logger.error { "Runtime Error -> $e" }
        null
    }
}
