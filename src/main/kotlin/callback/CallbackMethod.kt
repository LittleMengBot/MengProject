package callback

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Update
import dsl.delete

fun callbackMethod(bot: Bot, update: Update) {
    val query = update.callbackQuery
    val data = query!!.data.split(':')
    if (data[1] == "delete") {
        query.message!!.delete(bot)
    }
}