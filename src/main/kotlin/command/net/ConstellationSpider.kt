package command.net

import LANG
import callback.deleteButton
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.Update
import dsl.edit
import dsl.replyToText
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*

fun convert(s: String): String? {
    return when (s) {
        "白羊座" -> "aries"
        "金牛座" -> "taurus"
        "双子座" -> "aries"
        "雙子座" -> "aries"
        "巨蟹座" -> "cancer"
        "狮子座" -> "leo"
        "獅子座" -> "leo"
        "处女座" -> "virgo"
        "處女座" -> "virgo"
        "天秤座" -> "libra"
        "天蝎座" -> "scorpio"
        "天蠍座" -> "scorpio"
        "射手座" -> "virgo"
        "摩羯座" -> "capricorn"
        "水瓶座" -> "aquarius"
        "双鱼座" -> "pisces"
        "雙魚座" -> "pisces"
        else -> null
    }
}

fun parseFortune(fortune: String): String {
    return fortune.replace("星[^\\s]座[^\\s]屋".toRegex(), "")
}

fun getStar(s: String): String {
    val i = s.substring(s.indexOf("width:") + 6, s.indexOf("px;")).toInt() / 16
    val sb = StringBuilder()
    (1..i).forEach { _ ->
        sb.append("⭐️")
    }
    return sb.toString()
}

fun constellationStatus(constellation: String): String? {
    val en = convert(constellation)
    if (en != null) {
        val header =
            mapOf("User-Agent" to "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:61.0) Gecko/20100101 Firefox/89.0")
        val url = "https://www.xzw.com/fortune/$en/"

        val calendar = Calendar.getInstance(Locale.CHINA)
        val time = calendar.time
        val datafmt = SimpleDateFormat("yyyy年MM月dd日")
        val today = datafmt.format(time)


        val page = NetUtils.sendGet(url, withTor = true, header = header) // 墙内网站请使用Tor访问
        val document = page?.let { Jsoup.parse(it) }
        if (document != null) {
            val s = document.select("span")
            val totalStar = getStar(s[3].select("em").attr("style")) // 综合运势星星
            val loveStar = getStar(s[4].select("em").attr("style")) // 爱情运势星星
            val workStar = getStar(s[5].select("em").attr("style"))  // 事业运势星星
            val worthStar = getStar(s[6].select("em").attr("style"))  // 事业运势星星

            val total = parseFortune(s[7].text())
            val love = parseFortune(s[8].text())
            val work = parseFortune(s[9].text())
            val worth = parseFortune(s[10].text())
            val health = parseFortune(s[11].text())
            return LANG["constellation_fmt"]!!.format(
                today, constellation, totalStar, total, loveStar, love, workStar, work,
                worthStar, worth, health
            )
        } else return null

    } else {
        return null
    }
}

fun constellationCommand(bot: Bot, update: Update, args: List<String>) {
    val message = update.message!!
    if (args.isNotEmpty() && convert(args[0]) != null) {
        val editMessageId = message.replyToText(bot, update, LANG["getting"]!!)
        val text = constellationStatus(args[0])
        if (text != null) {
            message.edit(bot, editMessageId, text, deleteButton(message.messageId), ParseMode.MARKDOWN)
        } else {
            message.edit(bot, editMessageId, LANG["find_empty"]!!, deleteButton(message.messageId))
        }
    } else {
        message.replyToText(bot, update, LANG["constellation_hint"]!!, deleteButton(message.messageId))
    }
}
