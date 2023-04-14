import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import envirenment.EnvironmentStatus
import mu.KotlinLogging
import type.config.Config
import java.io.File
import java.io.IOException

private val logger = KotlinLogging.logger {}

object ConfigLoader {

    var configCache: Config? = null

    fun init(): Boolean {
        return try {
            val config = File(EnvironmentStatus.currentPath + "config.json").readText()
            configCache = Gson().fromJson(config, Config::class.java)
            true
        } catch (e: IOException) {
            logger.error(e.toString())
            false
        } catch (e: JsonSyntaxException) {
            logger.error(e.toString())
            false
        }
    }
}
