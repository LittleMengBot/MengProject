package command.cache

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import envirenment.EnvironmentStatus
import mu.KotlinLogging
import type.music.MusicType
import java.io.File

private val logger = KotlinLogging.logger {}

object MusicCache {

    var musicListCache: MusicType? = null

    fun init(): Boolean {
        return try {
            val musicJson = File(EnvironmentStatus.currentPath + "music.json").readText()
            musicListCache = Gson().fromJson(musicJson, MusicType::class.java)
            true
        } catch (e: JsonSyntaxException) {
            logger.error(e.toString())
            false
        }
    }
}
