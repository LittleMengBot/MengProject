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
import command.rawtext.LunarCommand.lunarCommand
import envirenment.EnvironmentStatus
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.GlobalScope
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
            command("help") { GlobalScope.launch { helpCommand(bot, this@command.update) } }
            command("dream") { dreamCommand(bot, this.update) }
            command("xi") { GlobalScope.launch { getSpeechByteArray(bot, update, args) } }
            command("del") { deleteCommand(bot, this.update) }
            command("send") { sendCommand(bot, this.update) }
            command("chongkai") { remakeCommand(bot, this.update) }
            command("remake") { remakeCommand(bot, this.update) }
            command("search") { GlobalScope.launch { searchCommand(bot, this@command.update) } }
            command("music") { musicCommand(bot, this.update, args) }
            command("download") { GlobalScope.launch { downloadCommand(bot, update, args) } }
            command("xingzuo") { GlobalScope.launch { constellationCommand(bot, this@command.update, args) } }
            command("shot") { GlobalScope.launch { shotCommand(bot, this@command.update, args) } }
            command("wiki") { GlobalScope.launch { wikiCommand(bot, this@command.update, args) } }
            command("huangli") { GlobalScope.launch { lunarCommand(bot, this@command.update) } }
            command("meiguo") { GlobalScope.launch { meiguoCommand(bot, this@command.update) } }
            command("india") { GlobalScope.launch { indiaCommand(bot, this@command.update) } }
            command("gs") { GlobalScope.launch { getStickerCommand(bot, this@command.update) } }
            command("getsticker") { GlobalScope.launch { getStickerCommand(bot, this@command.update) } }
            command("gif") { GlobalScope.launch { getAnimationCommand(bot, this@command.update) } }
            command("toutou") { PeopleCommand.withCommand(bot, this.update, message.text!!) }
            command("tietie") { PeopleCommand.withCommand(bot, this.update, message.text!!) }
            command("zouzou") { PeopleCommand.withCommand(bot, this.update, message.text!!) }
            command("momo") { PeopleCommand.withCommand(bot, this.update, message.text!!) }
            command("qr") { GlobalScope.launch { qrCommand(bot, this@command.update, args) } }
            command("replace") { replaceCommand(bot, this@command.update, args) }
            command("b") { cryptoCommand(bot, this@command.update, args) }
            command("what") { whatCommand(bot, this@command.update) }

            callbackQuery { GlobalScope.launch { callbackMethod(bot, this@callbackQuery.update) } }

            telegramError {
                println(error.getErrorMessage())
            }
        }
    }

//    bot.startPolling()

    bot.startWebhook()

//    Please use nginx proxy_pass to hold the webhook post.
    val env = applicationEngineEnvironment {
        module {
            routing {
                post("/${configCache!!.bot_token}") {
                    val response = call.receiveText()
                    bot.processUpdate(response)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
        connector {
            port = configCache!!.webhook_proxy_port
        }
    }

    embeddedServer(Netty, env).start(wait = true)
}