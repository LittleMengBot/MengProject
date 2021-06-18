package dsl

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton


fun String.generateUrlButton(hint: String): InlineKeyboardMarkup {
    return InlineKeyboardMarkup.create(
        listOf(
            InlineKeyboardButton.Url(text = hint, url = this)
        )
    )
}