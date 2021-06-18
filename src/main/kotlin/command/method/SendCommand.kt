package command.method

import ConfigLoader.configCache
import LANG
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.network.fold
import dsl.isChatMember
import dsl.replyToText

fun sendCommand(bot: Bot, update: Update) {
    if (update.message!!.chat.type == "private") {
        val operator = update.message!!.from!!
        val messageText = update.message!!.text!!.replace("/send ", "")
        if (operator.isChatMember(bot)) {
            bot.sendMessage(chatId = ChatId.fromId(configCache!!.group_id), text = messageText).fold({
                update.message!!.replyToText(bot, update, LANG["send_success"]!!)
            }, {
                update.message!!.replyToText(bot, update, LANG["send_failed"]!!)
            })
        }
    }
}