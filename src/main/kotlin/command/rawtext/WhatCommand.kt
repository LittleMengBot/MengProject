package command.rawtext

import callback.deleteButton
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.Update
import dsl.replyToText

fun whatCommand(bot: Bot, update: Update) {
    val message = update.message!!
    if (message.text != null) {
        update.message!!.replyToText(
            bot,
            update,
            update.message!!.text!!,
            deleteButton(update.message!!.messageId),
            ParseMode.MARKDOWN
        )
    }else return
}