package command.rawtext

import LANG
import callback.deleteButton
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.Update
import dsl.replyToText

fun helpCommand(bot: Bot, update: Update) {
    update.message!!.replyToText(
        bot,
        update,
        LANG["help_command"]!!,
        deleteButton(update.message!!.messageId),
        ParseMode.MARKDOWN
    )
}