package command.rawtext

import LANG
import callback.deleteButton
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Update
import command.cache.MusicCache
import dsl.edit
import dsl.generateUrlButton
import dsl.replyToText
import java.util.regex.Matcher
import java.util.regex.Pattern

fun searchMusic(name: String): String? {
    val pattern: Pattern = Pattern.compile(name)
    val nameList = MusicCache.musicListCache!!.music
    for (i in nameList.indices) {
        val matcher: Matcher = pattern.matcher(nameList[i])
        if (matcher.find()) {
            return MusicCache.musicListCache!!.url[i]
        }
    }
    return null
}

fun musicCommand(bot: Bot, update: Update, args: List<String>) {
    val message = update.message!!
    if (args.isNotEmpty()){
        val editMessageId = message.replyToText(bot, update, LANG["finding"]!!)
        val music = args[0]
        val result = searchMusic(music)
        if (result != null){
            message.edit(bot, editMessageId, result, result.generateUrlButton(LANG["music_button_hint"]!!))
        }else{
            message.edit(bot, editMessageId, LANG["find_empty"]!!, deleteButton(messageId = message.messageId))
        }
    }else{
        message.replyToText(bot, update, LANG["music_args_empty"]!!)
    }
}