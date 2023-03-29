package dsl

import ConfigLoader.configCache
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.User
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
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
            logger.error(it.toString())
            check = false
        })
    return check
}

fun User.fullName(): String {
    return if (this.lastName != null) "${this.firstName} ${this.lastName}" else this.firstName
}
