package command.method

import LANG
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Update
import dsl.delete
import dsl.isAdmin
import dsl.replyToText

fun deleteCommand(bot: Bot, update: Update) {
    val message = update.message!!
    val operator = message.from!!
    if (message.replyToMessage != null) {
        if (operator.isAdmin()) {
            message.replyToMessage!!.delete(bot)
        } else {
            message.replyToText(bot, update, LANG["permission_failed"]!!)
        }
    }
}