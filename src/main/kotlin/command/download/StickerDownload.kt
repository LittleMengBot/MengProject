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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging
import java.io.IOException

private val logger = KotlinLogging.logger {}
fun getStickerCommand(bot: Bot, update: Update) {
    val message = update.message

    if (message!!.replyToMessage != null && message.replyToMessage!!.sticker != null) {
        val sticker = message.replyToMessage!!.sticker!!
        val lockCode = StatusLock.generateLock(message.from!!.id, "StickerDownload")

        if (!StatusLock.checkLock(lockCode)) {
            StatusLock.lock(lockCode)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val cacheInMessageId: Long = message.replyToText(bot, update, LANG["lock_true"]!!)
                delay(5000L)
                bot.deleteMessage(chatId = ChatId.fromId(update.message!!.chat.id), messageId = cacheInMessageId)
            }
            return
        }
        val editMessageId: Long = message.replyToText(bot, update, LANG["getting"]!!)
        val stickerByteArray = bot.downloadFileBytes(sticker.fileId)

        val filePath = bot.getFile(sticker.fileId).first!!.body()!!.result!!.filePath!!

        when (filePath.substring(filePath.length - 4, filePath.length)) {
            ".tgs" -> {
                try {
                    message.edit(bot, editMessageId, LANG["converting"]!!)

                    val ofp = NativeBuilder().generateGif(stickerByteArray)
                    if (ofp.isEmpty()) {
                        message.edit(bot, editMessageId, LANG["process_error"]!!);return
                    }
                    message.edit(bot, editMessageId, LANG["sending"]!!)

                    bot.sendDocument(
                        chatId = ChatId.fromId(update.message!!.chat.id),
                        document = TelegramFile.ByByteArray(
                            ofp, "GIF-${(1000000..9999999).random()}.gifx"
                        ),
                        caption = LANG["gif_hint"],
                        replyMarkup = deleteButton(update.message!!.messageId),
                        replyToMessageId = message.messageId
                    )
                    bot.deleteMessage(chatId = ChatId.fromId(update.message!!.chat.id), messageId = editMessageId)
                } catch (e: IOException) {
                    logger.error(e.toString())
                    message.edit(bot, editMessageId, LANG["process_error"]!!)
                } finally {
                    StatusLock.freeze(lockCode)
                }
            }

            "webp" -> {
                try {
                    val pngArray = NativeBuilder().generatePNGFromWebP(stickerByteArray, stickerByteArray!!.size)
                    bot.sendDocument(
                        chatId = ChatId.fromId(update.message!!.chat.id),
                        document = TelegramFile.ByByteArray(
                            pngArray, "${sticker.setName}-${(1000000..9999999).random()}.png"
                        ),
                        caption = LANG["qr_caption"],
                        replyToMessageId = update.message!!.messageId,
                        replyMarkup = deleteButton(update.message!!.messageId)
                    )
                } catch (e: IOException) {
                    logger.error(e.toString())
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
                            outGif!!, "${sticker.setName}-${(1000000..9999999).random()}.gifx"
                        ),
                        caption = LANG["gif_hint"],
                        replyToMessageId = update.message!!.messageId,
                        replyMarkup = deleteButton(update.message!!.messageId)
                    )
                } catch (e: IOException) {
                    logger.error(e.toString())
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
