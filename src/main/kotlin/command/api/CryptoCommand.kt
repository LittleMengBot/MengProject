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
import type.crypto.CryptoInfo
import java.util.*

fun getCryptoInfo(symbol: String): String {
    val headers = mapOf(
        "Accepts" to "application/json",
        "X-CMC_PRO_API_KEY" to configCache!!.crypto_key
    )
    val url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest"
    val resp = NetUtils.sendGet(url, false, null, headers, listOf(Pair("symbol", symbol)))

    val data = Gson().fromJson(resp, CryptoInfo::class.java)
    val price = data.data.getValue(symbol).quote.USD.price
    val last1h = data.data.getValue(symbol).quote.USD.percent_change_1h
    val last24h = data.data.getValue(symbol).quote.USD.percent_change_24h
    val last7d = data.data.getValue(symbol).quote.USD.percent_change_7d
    return "$symbol\n当前价格为：$price USD\n" +
            "过去1h变化：$last1h %\n" +
            "过去24h变化：$last24h %\n" +
            "过去一周变化：$last7d %\n"
}

fun cryptoCommand(bot: Bot, update: Update, args: List<String>) {
    val message = update.message!!
    if (args.isEmpty()) {
        message.replyToText(bot, update, LANG["coin_empty"]!!, deleteButton(update.message!!.messageId))
        return
    }
    val editMessageId: Long = message.replyToText(bot, update, LANG["getting"]!!)
    try {
        val cryptoData = getCryptoInfo(args[0].uppercase(Locale.getDefault()))
        update.message!!.edit(bot, editMessageId, cryptoData, deleteButton(message.messageId))
    } catch (e: Exception) {
        e.printStackTrace()
        update.message!!.edit(bot, editMessageId, LANG["find_empty"]!!, deleteButton(message.messageId))
    }
}