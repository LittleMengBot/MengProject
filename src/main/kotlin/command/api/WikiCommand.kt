package command.api

import LANG
import callback.deleteButton
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.Update
import com.google.gson.Gson
import command.net.NetUtils
import dsl.edit
import dsl.replyToText


fun getWikiPage(key: String): MutableList<Pair<String, String>> {
    val wikiUrl = "https://zh.wikipedia.org/w/api.php?action=opensearch&"
    val result = NetUtils.sendGet(wikiUrl, args = listOf(Pair("search", key)))
    val parse = Gson().fromJson(result, List::class.java)
    val title = parse[1] as List<*>
    val link = parse[3] as List<*>
    val ret = mutableListOf<Pair<String, String>>()

    if (title.isNotEmpty()) {
        for (i in title.indices) {
            ret.add(Pair(title[i] as String, link[i] as String))
        }
    }

    return ret
}

fun wikiCommand(bot: Bot, update: Update, args: List<String>) {
    val message = update.message!!
    if (args.isEmpty()) {
        message.replyToText(bot, update, LANG["wiki_empty"]!!, deleteButton(update.message!!.messageId))
        return
    }

    val editMessageId: Long = message.replyToText(bot, update, LANG["finding"]!!)

    val wikiList = getWikiPage(args[0])
    if (wikiList.isNotEmpty()) {
        val sb = StringBuilder()
        wikiList.forEach {
            sb.append(LANG["wiki_result_parse"]!!.format(it.second, it.first)).append("\n\n")
        }
        message.edit(
            bot, editMessageId, sb.toString(),
            deleteButton(update.message!!.messageId), parseMode = ParseMode.HTML
        )
    } else {
        message.edit(
            bot, editMessageId, LANG["find_empty"]!!,
            deleteButton(update.message!!.messageId), parseMode = ParseMode.HTML
        )
    }
}