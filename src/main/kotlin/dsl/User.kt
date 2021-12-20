package dsl

import ConfigLoader.configCache
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.User
import com.github.kotlintelegrambot.network.fold

fun User.isAdmin(): Boolean {
    return configCache!!.admin_id.contains(this.id)
}

fun User.isChatMember(bot: Bot): Boolean {
    var check = false
    bot.getChatMember(ChatId.fromId(configCache!!.group_id), this.id).fold(
        {
            if (it.status in listOf("creator", "administrator", "member")) {
                check = true
            }
        }, {
            check = false
        })
    return check
}

fun User.fullName(): String {
    if (this.lastName != null) {
        return "${this.firstName}${this.lastName}"
    }
    return this.firstName
}