package command.net

import ConfigLoader.configCache
import LANG
import callback.deleteButton
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.files.PhotoSize
import com.github.kotlintelegrambot.network.fold
import dsl.edit
import dsl.replyToText
import mu.KotlinLogging
import org.jsoup.Jsoup

private val logger = KotlinLogging.logger {}
fun searchImageByUrl(imageUrl: String): List<Pair<String, String>> {
    val baseUrl = "https://www.google.com/"
    val header = mapOf("User-Agent" to "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:61.0) Gecko/20100101 Firefox/89.0")

    val html = NetUtils.sendGet(baseUrl, false, "searchbyimage", header, listOf(Pair("image_url", imageUrl)))
    var result = mutableListOf<Pair<String, String>>()

    if (html != null) {
        val document = Jsoup.parse(html)
        val parse = document.select("div.g")
        val title = parse.select("a").select("h3")
        val urlList = mutableListOf<String>()
        val titleList = mutableListOf<String>()

        result = mutableListOf()
        parse.forEach { parseSingle ->
            urlList.add(parseSingle.select("a").attr("href"))
        }


        title.forEach { titleSingle ->
            titleList.add(titleSingle.text())
        }

        for (i in title.indices) {
            result.add(Pair(titleList[i], urlList[i]))
        }
        return result
    } else {
        return result
    }
}

fun searchCommand(bot: Bot, update: Update) {
    val message = update.message!!
    val photo: PhotoSize
    var photoUrl: String? = null
    if (message.replyToMessage?.photo != null) {
        val editMessageId: Long = message.replyToText(bot, update, LANG["finding"]!!)
        photo = message.replyToMessage!!.photo!![message.replyToMessage!!.photo!!.size - 1]
        val photoFile = bot.getFile(photo.fileId)
        photoFile.fold({
            photoUrl = LANG["file_base_url"]!!.format(configCache!!.bot_token, it!!.result!!.filePath)
        }, {
            logger.error(it.exception?.toString())
        })

        if (photoUrl != null) {
            val resultList = searchImageByUrl(photoUrl!!)
            if (resultList.isNotEmpty()) {
                val sb = StringBuilder()
                resultList.forEach {
                    sb.append(LANG["image_search_single"]!!.format(it.first, it.second))
                }
                sb.append(LANG["image_search_foot"])
                message.edit(
                    bot,
                    editMessageId,
                    sb.toString(),
                    deleteButton(messageId = message.messageId),
                    ParseMode.MARKDOWN
                )
            } else {
                message.edit(bot, editMessageId, LANG["image_not_found"]!!, deleteButton(messageId = message.messageId))
            }
        }
    } else {
        message.replyToText(bot, update, LANG["image_reply"]!!, deleteButton(messageId = message.messageId))
    }
}
