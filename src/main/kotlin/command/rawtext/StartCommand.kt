package command.rawtext

import LANG
import callback.deleteButton
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Update
import dsl.replyToText

fun startCommand(bot: Bot, update: Update) {
    update.message!!.replyToText(bot, update, LANG["start_command"]!!, deleteButton(update.message!!.messageId))

}