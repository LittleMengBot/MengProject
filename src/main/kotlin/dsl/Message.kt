package dsl

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.*
import com.github.kotlintelegrambot.network.fold

fun Message.replyToText(
    bot: Bot,
    update: Update,
    text: String,
    replyMarkup: ReplyMarkup? = null,
    parseMode: ParseMode? = null
): Long {
    var currentMessageId: Long = 0
    bot.sendMessage(
        chatId = ChatId.fromId(this.chat.id), text = text,
        replyToMessageId = update.message!!.messageId, replyMarkup = replyMarkup, parseMode = parseMode
    ).fold({
        currentMessageId = it!!.result!!.messageId
    }, {
        currentMessageId = 0
    })
    return currentMessageId
}

fun Message.edit(
    bot: Bot,
    editId: Long,
    text: String,
    replyMarkup: ReplyMarkup? = null,
    parseMode: ParseMode? = null
) {
    bot.editMessageText(
        chatId = ChatId.fromId(this.chat.id), messageId = editId, text = text,
        replyMarkup = replyMarkup, parseMode = parseMode
    ).fold({ }, {
        println(it.errorBody.toString())
    }
    )
}

fun Message.delete(bot: Bot) {
    try {
        bot.deleteMessage(
            chatId = ChatId.fromId(this.chat.id),
            messageId = this.messageId
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Message.getFullName(): String {
    if (this.senderChat != null){
        return this.senderChat!!.title!!
    }
    return this.from!!.fullName()
}
