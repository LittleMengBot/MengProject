import com.google.gson.Gson
import envirenment.EnvironmentStatus
import type.config.Config
import java.io.File

object ConfigLoader {

    var configCache: Config? = null

    fun init(): Boolean{
        val config = File(EnvironmentStatus.currentPath + "config.json").readText()
        return try {
            configCache = Gson().fromJson(config, Config::class.java)
            true
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }
}