package command.rawtext

import LANG
import callback.deleteButton
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.Update
import dsl.replyToText

fun replaceCommand(bot: Bot, update: Update, args: List<String>) {
    val message = update.message!!
    if (args.isNotEmpty() && args.size == 2 && message.text != null && message.replyToMessage?.text != null) {
        message.replyToText(bot, update,
            "`${ message.replyToMessage?.text!!.replace(args[0], args[1]) }`",
            deleteButton(update.message!!.messageId), ParseMode.MARKDOWN)
    }else{
        message.replyToText(bot, update,
            LANG["replace_args_error"]!!,
            deleteButton(update.message!!.messageId), ParseMode.MARKDOWN)
    }
}