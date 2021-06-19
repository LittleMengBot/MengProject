package envirenment

import ConfigLoader
import ConfigLoader.configCache
import command.cache.ChromeDriverCache
import command.cache.MusicCache
import dsl.execListener
import java.io.File

object EnvironmentStatus {

    val currentPath = "${System.getProperty("user.dir")}/"
    fun check(): Boolean{
        println("Current Path： $currentPath")
        val prop = System.getProperty("os.name")
        return when {
            prop.equals("Windows") -> {
                println("Shhh...! Fuck Windows! \n\n------\nANTI WINDOWS AND FUCK MICROSOFT\n------");false
            }

            !ConfigLoader.init() -> {
                println("Please check the config file.");false
            }
            !MusicCache.init() -> false
            ChromeDriverCache.init() == null -> false
            !File(configCache!!.python3_path).exists() -> {
                println("Please install python3.");false
            }
            !File(configCache!!.youget_path).exists() -> {
                println("Please install you-get, do: \npip3 install you-get");false
            }
            !File(configCache!!.ffmpeg_path).exists() -> {
                val uname = Runtime.getRuntime().exec("uname -a").execListener()!!
                //已Fuck Windows，所以断言非空
                when {
                    uname.contains("Darwin") -> {
                        println("Please install ffmpeg, do: \nbrew install ffmpeg");false
                    }
                    uname.contains("Ubuntu") -> {
                        println("Please install ffmpeg, do: \napt install ffmpeg");false
                    }
                    uname.contains("Cent") -> {
                        println("Please install ffmpeg, do: \nyum install ffmpeg");false
                    }
                    else -> {
                        println("Please install ffmpeg and set the bin path in the Config File.");false
                    }
                }

            }
            else -> true
        }
    }
}