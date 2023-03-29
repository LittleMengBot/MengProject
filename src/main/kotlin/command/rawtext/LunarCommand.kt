package command.rawtext

import LANG
import callback.deleteButton
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.Update
import com.nlf.calendar.Solar
import mu.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger {}

object LunarCommand {
    private fun lunarInfo(): String {
        val calendar = Calendar.getInstance(Locale.CHINA)
        val solar = Solar.fromDate(calendar.time)
        val lunar = solar.lunar
        val pengZu = StringBuilder("${lunar.pengZuGan} ${lunar.pengZuZhi}")
        pengZu.insert(4, " ").insert(14, " ")
        var result = LANG["lunar_data_fmt"]!!
        result = result.format(
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE),
            lunar.yearInGanZhi, lunar.yearShengXiao, lunar.monthInChinese, lunar.dayInChinese,
            pengZu.toString(),
            lunar.dayYi.toString().replace(',', '，').replace("[", "").replace("]", "").replace(" ", ""),
            lunar.dayJi.toString().replace(',', '，').replace("[", "").replace("]", "").replace(" ", ""),
            lunar.eightChar,
            "喜神${lunar.dayPositionXiDesc}，财神${lunar.dayPositionCaiDesc}，福神${lunar.dayPositionFuDesc}，" +
                    "阳贵${lunar.dayPositionYangGuiDesc}，阴贵${lunar.dayPositionYinGuiDesc}"
        )
        return result
    }

    fun lunarHandler(bot: Bot, update: Update) {
//        println(lunarInfo())
        bot.sendMessage(
            chatId = ChatId.fromId(update.message!!.chat.id),
            text = lunarInfo(),
            replyToMessageId = update.message!!.messageId,
            replyMarkup = deleteButton(update.message!!.messageId),
            parseMode = ParseMode.HTML
        ).fold({}, {
            logger.error(it.toString())
        })
    }
}
