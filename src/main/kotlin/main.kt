import ConfigLoader.configCache
import callback.callbackMethod
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.telegramError
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.webhook
import command.api.*
import command.download.downloadCommand
import command.download.getAnimationCommand
import command.download.getStickerCommand
import command.method.deleteCommand
import command.method.sendCommand
import command.net.constellationCommand
import command.net.searchCommand
import command.net.shotCommand
import command.rawtext.*
import command.rawtext.LunarCommand.lunarHandler
import envirenment.EnvironmentStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

// ANTI WINDOWS AND FUCK MICROSOFT
fun init(): Boolean {
    return EnvironmentStatus.check()
}

fun main() {

    if (!init()) {
        exitProcess(1)
    }

    val bot = bot {
        timeout = 30
        logLevel = LogLevel.Error
        token = configCache!!.bot_token

        webhook {
            url = "${configCache!!.webhook_url}${configCache!!.bot_token}"
            maxConnections = 50
            allowedUpdates = listOf("message", "callback_query")
        }

        dispatch {
            command("start") { startCommand(bot, this.update) }
            command("help") { helpCommand(bot, this@command.update) }
            command("dream") { dreamCommand(bot, this.update) }
            command("xi") { CoroutineScope(Dispatchers.IO).launch { getSpeechByteArray(bot, update, args) } }
            command("del") { deleteCommand(bot, this.update) }
            command("send") { sendCommand(bot, this.update) }
            command("chongkai") { remakeCommand(bot, this.update) }
            command("remake") { remakeCommand(bot, this.update) }
            command("search") { CoroutineScope(Dispatchers.IO).launch { searchCommand(bot, this@command.update) } }
            command("music") { musicCommand(bot, this.update, args) }
            command("download") { CoroutineScope(Dispatchers.IO).launch { downloadCommand(bot, update, args) } }
            command("xingzuo") {
                CoroutineScope(Dispatchers.IO).launch {
                    constellationCommand(
                        bot,
                        this@command.update,
                        args
                    )
                }
            }
            command("shot") { CoroutineScope(Dispatchers.IO).launch { shotCommand(bot, this@command.update, args) } }
            command("wiki") { CoroutineScope(Dispatchers.IO).launch { wikiCommand(bot, this@command.update, args) } }
            command("huangli") { CoroutineScope(Dispatchers.IO).launch { lunarHandler(bot, this@command.update) } }
            command("meiguo") { CoroutineScope(Dispatchers.IO).launch { meiguoCommand(bot, this@command.update) } }
            command("india") { CoroutineScope(Dispatchers.IO).launch { indiaCommand(bot, this@command.update) } }
            command("gs") { CoroutineScope(Dispatchers.IO).launch { getStickerCommand(bot, this@command.update) } }
            command("getsticker") {
                CoroutineScope(Dispatchers.IO).launch {
                    getStickerCommand(
                        bot,
                        this@command.update
                    )
                }
            }
            command("gif") { CoroutineScope(Dispatchers.IO).launch { getAnimationCommand(bot, this@command.update) } }
            command("toutou") { PeopleCommand.withCommand(bot, this.update, message.text!!) }
            command("tietie") { PeopleCommand.withCommand(bot, this.update, message.text!!) }
            command("zouzou") { PeopleCommand.withCommand(bot, this.update, message.text!!) }
            command("momo") { PeopleCommand.withCommand(bot, this.update, message.text!!) }
            command("qr") { CoroutineScope(Dispatchers.IO).launch { qrCommand(bot, this@command.update, args) } }
            command("replace") { replaceCommand(bot, this@command.update, args) }
            command("b") { cryptoCommand(bot, this@command.update, args) }
            command("what") { whatCommand(bot, this@command.update) }

            callbackQuery { CoroutineScope(Dispatchers.IO).launch { callbackMethod(bot, this@callbackQuery.update) } }

            telegramError {
                println(error.getErrorMessage())
            }
        }
    }

    bot.startPolling()

}
