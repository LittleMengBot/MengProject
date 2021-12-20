package command.download

import ConfigLoader.configCache
import LANG
import callback.deleteButton
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.Update
import command.cache.StatusLock
import dsl.edit
import dsl.execListener
import dsl.replyToText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File



fun searchFile(file: File): String?{
    val fileList = File(file.parent).list()!!   //断言非空
    fileList.forEach {
        if (it.startsWith(file.name) && it != file.name){
            return it
        }
    }
    return null
}

fun downloadVideo(videoUrl: String): String? {
    val cmd = "${configCache!!.youget_path} -i $videoUrl"
    val result = Runtime.getRuntime().exec(cmd).execListener()
    val re = Regex("(\\d* \\wytes)")
    var videoByte: Int

    result?.let {
        if (!it.contains("[rror]") && result != ""){
            try {
                videoByte = re.findAll(it).toList()[0].value.split(" ")[0].toInt()
            }finally { }
            // 判断视频是否小于20MB，20971520 = 20 * 20 * 1024
            if (videoByte < 20971520) {
                println("video < 20MB")
                val tempFile = File.createTempFile("video", "")
                Runtime.getRuntime().exec(
                    "${configCache!!.youget_path} --no-caption " +
                        "-o ${tempFile.parent}/ " +
                        "-O ${tempFile.name} $videoUrl").waitFor()
                val videoFile = (searchFile(tempFile))
                if (videoFile != null){
                    val video = File("${tempFile.parent}/$videoFile")
                    if (videoFile.endsWith(".mp4")){
                        try {
                            return video.absolutePath
                        }finally{
                            tempFile.delete()
                        }
                    }else{
                        return if (videoFile.endsWith(".flv")){
                            val ffmpegStatus = Runtime.getRuntime().exec(
                                "${configCache!!.ffmpeg_path} -i " +
                                        "${video.absolutePath} ${video.absolutePath}.mp4").execListener()!!
                            if (!ffmpegStatus.contains("Invalid")){
                                return try{
                                    val ret = File("${video.absolutePath}.mp4")
                                    val size = ret.length()
                                    if (size < 20971520){
                                        ret.absolutePath
                                    }else{
                                        ret.delete()
                                        null
                                    }
                                }finally{
                                    video.delete()
                                    tempFile.delete()
                                }
                            }else{
                                tempFile.delete()
                                null
                            }
                        }else{
                            return try{
                                video.absolutePath
                            }finally {
                                tempFile.delete()
                            }
                        }
                    }
                }
                tempFile.delete()
                return null
            }else{
                return null
            }
        }else{
            return null
        }
    }
    return null
}

fun checkUrl(url: String): Boolean {
    return (url.contains(Regex("[a-zA-z]+://[^\\s]*")))
}

fun downloadCommand(bot: Bot, update: Update, args: List<String>) {
    val message = update.message!!
    var videoUrl: String? = null
    when {
        args.isEmpty() && message.replyToMessage == null -> {
            message.replyToText(bot, update, LANG["no_video_link"]!!, deleteButton(update.message!!.messageId));return
        }
        args.isNotEmpty() -> {
            if (checkUrl(args[0])) videoUrl = args[0]
        }
        message.replyToMessage != null -> {
            if (message.replyToMessage!!.text != null){
                if (checkUrl(message.replyToMessage!!.text!!)) videoUrl = message.replyToMessage!!.text!!
            }
        }
    }

    if (videoUrl != null){

        val lockCode = StatusLock.generateLock(message.from!!.id, "VideoDownload", listOf(videoUrl))

        if (!StatusLock.checkLock(lockCode)) {
            StatusLock.lock(lockCode)
        } else {
            GlobalScope.launch {
                val cacheInMessageId: Long = message.replyToText(bot, update, LANG["lock_true"]!!)
                delay(5000L)
                bot.deleteMessage(chatId = ChatId.fromId(update.message!!.chat.id), messageId = cacheInMessageId)
            }
            return
        }

        val editMessageId: Long = message.replyToText(bot, update, LANG["video_downloading"]!!)
        val videoPath = downloadVideo(videoUrl)
        if (videoPath != null){
            val videoFile = File(videoPath)
            bot.sendVideo(ChatId.fromId(message.chat.id),
                TelegramFile.ByFile(videoFile),
                replyMarkup = deleteButton(update.message!!.messageId), replyToMessageId = message.messageId)
            bot.deleteMessage(ChatId.fromId(message.chat.id), editMessageId)
            videoFile.delete()
            StatusLock.freeze(lockCode)
        }else{
            message.edit(bot, editMessageId, LANG["video_download_failed"]!!, deleteButton(update.message!!.messageId))
            StatusLock.freeze(lockCode)
        }
    }else{
        message.replyToText(bot, update, LANG["no_video_link"]!!, deleteButton(update.message!!.messageId));return
    }
}