package command.net

import LANG
import callback.deleteButton
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.network.fold
import command.cache.ChromeDriverCache
import command.cache.StatusLock
import dsl.edit
import dsl.replyToText
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.openqa.selenium.Dimension
import org.openqa.selenium.OutputType
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.chrome.ChromeDriver

private val logger = KotlinLogging.logger {}
suspend fun shotWeb(url: String, w: Int, h: Int): ByteArray? {
    val driver: ChromeDriver = ChromeDriverCache.init() ?: return null
    driver.manage().window().size = Dimension(w, h)
    withContext(Dispatchers.Default) {
        driver.get(url)
        delay(3000L)
    }

    return try {
        driver.getScreenshotAs(OutputType.BYTES)
    } catch (e: WebDriverException) {
        logger.error(e.toString())
        null
    } finally {
        driver.quit()
    }
}

fun checkUrl(url: String?): Boolean {
    return url?.matches("[a-zA-z]+://[^\\s]*".toRegex()) ?: false
}

suspend fun shotCommand(bot: Bot, update: Update, args: List<String>) {
    val message = update.message!!

    val url: String?
    val w: Int?
    val h: Int?

    if (message.replyToMessage == null) {
        message.replyToText(bot, update, LANG["shot_reply_hint"]!!);return
    }
    if (args.isEmpty()) {
        w = 1920
        h = 1080
    } else {
        try {
            w = args[0].toInt()
            h = args[1].toInt()
        } catch (e: NumberFormatException) {
            logger.error(e.toString())
            message.replyToText(bot, update, LANG["shot_args_error"]!!);return
        }
        if (w > 20000 || h > 20000 || w < 500 || h < 500) {
            message.replyToText(bot, update, LANG["shot_args_error"]!!);return
        }
    }

    if (checkUrl(message.replyToMessage?.text)) {
        url = message.replyToMessage!!.text
    } else {
        message.replyToText(bot, update, LANG["shot_url_error"]!!);return
    }

    if (url == null) {
        message.replyToText(bot, update, LANG["shot_reply_hint"]!!);return
    }

    val lockCode = StatusLock.generateLock(message.from!!.id, "shot", listOf(url))

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
    val editMessageId = message.replyToText(bot, update, LANG["shot_in"]!!)

    val screenShot = shotWeb(url, w, h)
    if (screenShot != null) {
        message.edit(bot, editMessageId, LANG["sending"]!!)
        bot.sendDocument(
            chatId = ChatId.fromId(message.chat.id),
            document = TelegramFile.ByByteArray(screenShot, "shot-${(10000000..99999999).random()}.png"),
            replyMarkup = deleteButton(message.from!!.id)
        ).fold({}, {
            it.exception?.printStackTrace()
            println(it.errorBody)
        })
        bot.deleteMessage(chatId = ChatId.fromId(message.chat.id), messageId = editMessageId)
        StatusLock.freeze(lockCode)
    } else {
        message.edit(bot, editMessageId, LANG["shot_error"]!!, deleteButton(message.from!!.id))
        StatusLock.freeze(lockCode)
    }
}
