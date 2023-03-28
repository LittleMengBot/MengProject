package command.api

import ConfigLoader.configCache
import LANG
import callback.deleteButton
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Update
import com.google.gson.Gson
import command.net.NetUtils
import dsl.edit
import dsl.replyToText
import type.corona.CoronaData
import java.text.SimpleDateFormat
import java.util.*

fun getCoronaData(country: String): String {
    val calendar = Calendar.getInstance(Locale.CHINA)
    val time = calendar.time
    val datafmt = SimpleDateFormat("yyyy-MM-dd")
    val today = datafmt.format(time)
    calendar.add(Calendar.DATE, -1)
    val yesterday = datafmt.format(calendar.time)

    val headers = mapOf(
        "x-rapidapi-key" to configCache!!.corona_headers.`x-rapidapi-key`,
        "x-rapidapi-host" to configCache!!.corona_headers.`x-rapidapi-host`
    )

    val todayResult = NetUtils.sendGet(
        url = "https://covid-193.p.rapidapi.com/history",
        header = headers, args = listOf(Pair("country", country), Pair("day", today))
    )

    val yesResult = NetUtils.sendGet(
        url = "https://covid-193.p.rapidapi.com/history",
        header = headers, args = listOf(Pair("country", country), Pair("day", yesterday))
    )

    try {
        val todayCoronaData = Gson().fromJson(todayResult, CoronaData::class.java)
        val yesCoronaData = Gson().fromJson(yesResult, CoronaData::class.java)
        val total: Int
        val nowTime: String
        val totalDeath: Int
        if (todayCoronaData.response.isEmpty()) {
            total = yesCoronaData.response[0].cases.total
            nowTime = yesCoronaData.response[0].time
            totalDeath = yesCoronaData.response[0].deaths.total
        } else {
            total = todayCoronaData.response[0].cases.total
            nowTime = todayCoronaData.response[0].time
            totalDeath = todayCoronaData.response[0].deaths.total
        }
        val nowDay = nowTime.substring(8, 10)
        val nowHour = nowTime.substring(11, 13)
        val nowMinute = nowTime.substring(14, 16)

        val yesResultIndex = yesCoronaData.results - 1

        val yesTotal = yesCoronaData.response[yesResultIndex].cases.total
        val yesTotalDeath = yesCoronaData.response[yesResultIndex].deaths.total

        val newCase = total - yesTotal
        val newDeath = totalDeath - yesTotalDeath

        return LANG[country]!!.format(
            nowDay, nowHour, nowMinute, total.toString().substring(0, 4),
            total, totalDeath.toString().substring(0, 2), totalDeath, newCase, newDeath
        )
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    }
}

fun meiguoCommand(bot: Bot, update: Update) {
    val message = update.message!!

    val editMessageId: Long = message.replyToText(bot, update, LANG["getting"]!!)
    try {
        val coronaData = getCoronaData("usa")
        update.message!!.edit(bot, editMessageId, coronaData, deleteButton(message.messageId))
    } catch (e: Exception) {
        e.printStackTrace()
        update.message!!.edit(bot, editMessageId, LANG["find_empty"]!!, deleteButton(message.messageId))
    }

}

fun indiaCommand(bot: Bot, update: Update) {
    val message = update.message!!

    val editMessageId: Long = message.replyToText(bot, update, LANG["getting"]!!)
    try {
        val coronaData = getCoronaData("india")
        update.message!!.edit(bot, editMessageId, coronaData, deleteButton(message.messageId))
    } catch (e: Exception) {
        e.printStackTrace()
        update.message!!.edit(bot, editMessageId, LANG["find_empty"]!!, deleteButton(message.messageId))
    }

}