package command.cache

import com.google.gson.Gson
import envirenment.EnvironmentStatus
import type.music.MusicType
import java.io.File

object MusicCache {

    var musicListCache: MusicType? = null

    fun init(): Boolean{
        val musicJson = File(EnvironmentStatus.currentPath + "music.json").readText()
        return try {
            musicListCache = Gson().fromJson(musicJson, MusicType::class.java)
            true
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }
}