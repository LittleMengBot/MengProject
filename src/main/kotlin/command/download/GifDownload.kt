package command.download

import ConfigLoader.configCache
import LANG
import callback.deleteButton
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.files.Animation
import com.github.kotlintelegrambot.entities.files.Document
import dsl.edit
import dsl.replyToText
import file.FileUtils
import mu.KotlinLogging
import java.io.File
import java.io.IOException
import java.nio.file.Files

private val logger = KotlinLogging.logger {}
fun getAnimationCommand(bot: Bot, update: Update) {
    val message = update.message
    var animation: Animation? = null
    var document: Document? = null
    val tempByteArray: ByteArray?
    if (message!!.replyToMessage != null) {
        val editMessageId: Long

        when {
            message.replyToMessage!!.document?.mimeType == "image/gif" -> {
                editMessageId = message.replyToText(bot, update, LANG["getting"]!!)
                document = message.replyToMessage!!.document!!
                tempByteArray = bot.downloadFileBytes(document.fileId)
            }

            message.replyToMessage!!.animation != null -> {
                editMessageId = message.replyToText(bot, update, LANG["getting"]!!)
                animation = message.replyToMessage!!.animation!!
                tempByteArray = bot.downloadFileBytes(animation.fileId)
            }

            else -> {
                message.replyToText(bot, update, LANG["no_gif"]!!, deleteButton(messageId = message.messageId))
                return
            }
        }

        when {
            animation != null && animation.fileName!!.endsWith(".gif") -> {
                message.edit(bot, editMessageId, LANG["sending"]!!)
                bot.sendDocument(
                    chatId = ChatId.fromId(update.message!!.chat.id),
                    document = TelegramFile.ByByteArray(tempByteArray!!, "GIF-${(1000000..9999999).random()}.gifx"),
                    caption = LANG["gif_hint"],
                    replyToMessageId = update.message!!.messageId,
                    replyMarkup = deleteButton(update.message!!.messageId)
                )
                bot.deleteMessage(chatId = ChatId.fromId(update.message!!.chat.id), messageId = editMessageId)

            }

            animation != null && animation.fileName!!.endsWith(".mp4") -> {
                message.edit(bot, editMessageId, LANG["converting"]!!)
                val temp = toGif(tempByteArray!!, ".mp4")

                if (temp != null) {
                    message.edit(bot, editMessageId, LANG["sending"]!!)
                    bot.sendDocument(
                        chatId = ChatId.fromId(update.message!!.chat.id),
                        document = TelegramFile.ByByteArray(temp, "GIF-${(1000000..9999999).random()}.gifx"),
                        caption = LANG["gif_hint"],
                        replyToMessageId = update.message!!.messageId,
                        replyMarkup = deleteButton(update.message!!.messageId)
                    )
                    bot.deleteMessage(chatId = ChatId.fromId(update.message!!.chat.id), messageId = editMessageId)
                } else {
                    message.edit(bot, editMessageId, LANG["convert_failed"]!!)
                }

            }

            document != null -> {
                message.edit(bot, editMessageId, LANG["sending"]!!)
                bot.sendDocument(
                    chatId = ChatId.fromId(update.message!!.chat.id),
                    document = TelegramFile.ByByteArray(tempByteArray!!, "GIF-${(1000000..9999999).random()}.gifx"),
                    caption = LANG["gif_hint"],
                    replyToMessageId = update.message!!.messageId,
                    replyMarkup = deleteButton(update.message!!.messageId)
                )
                bot.deleteMessage(chatId = ChatId.fromId(update.message!!.chat.id), messageId = editMessageId)
            }
        }
    } else {
        message.replyToText(bot, update, LANG["no_sticker"]!!, deleteButton(messageId = message.messageId))
    }
}

fun toGif(tempByteArray: ByteArray, suffix: String): ByteArray? {
    val vFile = FileUtils.convertToTemp(tempByteArray, "gif", suffix)
    val ffmpegPath = configCache!!.ffmpeg_path // FFmpeg 路径

    // 转换文件为 GIF 格式
    val gifFile = convertFileToGif(vFile, ffmpegPath)
    if (gifFile == null || !gifFile.exists()) {
        vFile.delete()
        return null
    }

    // 读取 GIF 文件内容
    val gifData = readFile(gifFile)
    gifFile.delete()
    vFile.delete()

    return gifData
}

private fun convertFileToGif(sourceFile: File, ffmpegPath: String): File? {
    val gifFile = File(sourceFile.absolutePath + ".gif")
    val cmd = "$ffmpegPath -i ${sourceFile.absolutePath} ${gifFile.absolutePath}"

    try {
        val process = Runtime.getRuntime().exec(cmd)
        process.waitFor()
        if (process.exitValue() != 0) {
            return null
        }
        return gifFile
    } catch (e: IOException) {
        logger.error("Failed to execute FFmpeg: ${e.message}")
        return null
    } catch (e: InterruptedException) {
        logger.error("FFmpeg execution interrupted: ${e.message}")
        return null
    }
}

private fun readFile(file: File): ByteArray? {
    if (!file.exists() || !file.isFile || file.length() > 20971520) {
        return null
    }
    return try {
        Files.readAllBytes(file.toPath())
    } catch (e: IOException) {
        logger.error("Failed to read file: ${e.message}")
        null
    }
}
