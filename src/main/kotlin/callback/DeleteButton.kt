package callback

import LANG
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton

fun deleteButton(messageId: Long): InlineKeyboardMarkup {
    return InlineKeyboardMarkup.create(
        listOf(
            InlineKeyboardButton.CallbackData(text = LANG["delete_button"]!!, callbackData = "${messageId}:delete")
        )
    )
}
