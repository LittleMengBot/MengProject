package command.download

import LANG
import callback.deleteButton
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Update
import command.cache.StatusLock
import dsl.edit
import dsl.replyToText
import jni.GifBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO

fun getStickerCommand(bot: Bot, update: Update) {
    val message = update.message

    if (message!!.replyToMessage != null && message.replyToMessage!!.sticker != null) {
        val sticker = message.replyToMessage!!.sticker!!
        val lockCode = StatusLock.generateLock(message.from!!.id, "StickerDownload", listOf(sticker.fileId))

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
        val editMessageId: Long = message.replyToText(bot, update, LANG["getting"]!!)
        val stickerByteArray = bot.downloadFileBytes(sticker.fileId)

        if (sticker.isAnimated) {
            var tgsFileTemp: File? = null
            var outFile: File? = null
            try {
                tgsFileTemp = File.createTempFile("stickers", ".tgs")
                val tgsFileTempPath = Paths.get(tgsFileTemp.toURI())
                Files.write(tgsFileTempPath, stickerByteArray!!)
                message.edit(bot, editMessageId, LANG["converting"]!!)

                outFile = File(GifBuilder().generateGif(tgsFileTemp.absolutePath))

                message.edit(bot, editMessageId, LANG["sending"]!!)

                bot.sendDocument(
                    chatId = ChatId.fromId(update.message!!.chat.id), fileBytes = outFile.readBytes(),
                    caption = LANG["gif_hint"],
                    fileName = "GIF-${(1000000..9999999).random()}.gifx",
                    replyMarkup = deleteButton(update.message!!.messageId), replyToMessageId = message.messageId
                )
                bot.deleteMessage(chatId = ChatId.fromId(update.message!!.chat.id), messageId = editMessageId)
            } catch (e: Exception) {
                e.printStackTrace()
                message.edit(bot, editMessageId, LANG["process_error"]!!)
            } finally {
                StatusLock.freeze(lockCode)
                tgsFileTemp?.delete()
                outFile?.delete()
            }
        } else {
            try {
                val stickerProgressStream = ByteArrayInputStream(stickerByteArray)
                val pngOutStream = ByteArrayOutputStream()
                val image = ImageIO.read(stickerProgressStream)
                ImageIO.write(image, "png", pngOutStream)
                bot.sendDocument(
                    chatId = ChatId.fromId(update.message!!.chat.id),
                    fileBytes = pngOutStream.toByteArray(),
                    caption = LANG["gif_hint"],
                    fileName = "${sticker.setName}-${(1000000..9999999).random()}.png",
                    replyToMessageId = update.message!!.messageId,
                    replyMarkup = deleteButton(update.message!!.messageId)
                )
            } catch (e: Exception) {
                e.printStackTrace()
                message.edit(bot, editMessageId, LANG["process_error"]!!)
            } finally {
                StatusLock.freeze(lockCode)
                bot.deleteMessage(chatId = ChatId.fromId(update.message!!.chat.id), messageId = editMessageId)
            }

        }
    } else {
        message.replyToText(bot, update, LANG["no_sticker"]!!, deleteButton(messageId = message.messageId))
    }
}