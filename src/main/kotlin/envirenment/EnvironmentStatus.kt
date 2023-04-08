package envirenment

import ConfigLoader
import ConfigLoader.configCache
import command.cache.ChromeDriverCache
import command.cache.MusicCache
import command.cache.QRCache
import mu.KotlinLogging
import java.io.File

private val logger = KotlinLogging.logger {}

object EnvironmentStatus {


    val currentPath = "${System.getProperty("user.dir")}/"
    fun check(): Boolean {
        logger.info("Current Path： $currentPath")
        val prop = System.getProperty("os.name")
        return when {
            prop.equals("Windows") -> {
                logger.error("Shhh...! Fuck Windows! \n\n------\nANTI WINDOWS AND FUCK MICROSOFT\n------");false
            }

            !ConfigLoader.init() -> {
                logger.error("Please check the config file.");false
            }

            !MusicCache.init() -> false
            !QRCache.init() -> false
            ChromeDriverCache.init() == null -> false
            !File(configCache!!.python3_path).exists() -> {
                logger.error("Please install python3.");false
            }

            !File(configCache!!.youget_path).exists() -> {
                logger.error("Please install you-get, run: \npip3 install you-get");false
            }

            !File(configCache!!.ffmpeg_path).exists() -> {
                logger.error("Please install ffmpeg and set the bin path in the Config File.");false
            }

            !(configCache!!.handle_method == "webhook" || configCache!!.handle_method == "long_poll") -> {
                logger.error("Handle method must be \"webhook\" or \"long_poll\".Please check the config file.");false
            }

            else -> true
        }
    }
}
