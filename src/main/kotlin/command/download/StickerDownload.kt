package command.download

import LANG
import callback.deleteButton
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.Update
import command.cache.StatusLock
import dsl.edit
import dsl.replyToText
import jni.NativeBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

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

        val filePath = bot.getFile(sticker.fileId).first!!.body()!!.result!!.filePath!!
        val stickerType = filePath.substring(filePath.length - 4, filePath.length)

        when (stickerType) {
            ".tgs" -> {
                var tgsFileTemp: File? = null
                var outFile: File? = null
                try {
                    tgsFileTemp = File.createTempFile("stickers", ".tgs")
                    val tgsFileTempPath = Paths.get(tgsFileTemp.toURI())
                    Files.write(tgsFileTempPath, stickerByteArray!!)
                    message.edit(bot, editMessageId, LANG["converting"]!!)

                    val ofp = NativeBuilder().generateGif(tgsFileTemp.absolutePath)
                    if (ofp == "") {
                        message.edit(bot, editMessageId, LANG["process_error"]!!);return
                    }
                    outFile = File(NativeBuilder().generateGif(tgsFileTemp.absolutePath))


                    message.edit(bot, editMessageId, LANG["sending"]!!)

                    bot.sendDocument(
                        chatId = ChatId.fromId(update.message!!.chat.id),
                        document = TelegramFile.ByByteArray(
                            outFile.readBytes(),
                            "GIF-${(1000000..9999999).random()}.gifx"
                        ),
                        caption = LANG["gif_hint"],
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
            }
            "webp" -> {
                try {
                    val pngArray = NativeBuilder()
                        .generatePNGFromWebP(stickerByteArray, stickerByteArray!!.size)
                    bot.sendDocument(
                        chatId = ChatId.fromId(update.message!!.chat.id),
                        document = TelegramFile.ByByteArray(
                            pngArray,
                            "${sticker.setName}-${(1000000..9999999).random()}.png"
                        ),
                        caption = LANG["gif_hint"],
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
            "webm" -> {
                try {
                    val outGif = toGif(stickerByteArray!!, ".webm")
                    bot.sendDocument(
                        chatId = ChatId.fromId(update.message!!.chat.id),
                        document = TelegramFile.ByByteArray(
                            outGif!!,
                            "${sticker.setName}-${(1000000..9999999).random()}.gifx"
                        ),
                        caption = LANG["gif_hint"],
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
        }
    } else {
        message.replyToText(bot, update, LANG["no_sticker"]!!, deleteButton(messageId = message.messageId))
    }
}