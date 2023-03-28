package command.api

import ConfigLoader.configCache
import LANG
import callback.deleteButton
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.Update
import com.google.gson.Gson
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import command.net.NetUtils
import dsl.edit
import dsl.replyToText
import file.FileUtils.convertToTemp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import type.speech.AudioStatus
import type.speech.RequestStartType
import type.speech.TaskStatus
import java.util.*

@DelicateCoroutinesApi
suspend fun getAudioBytes(key: String): ByteArray? {
    val gsonParser = Gson()
    val header = mapOf("content-type" to "application/json")

    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val requestAdapter = moshi.adapter(RequestStartType::class.java)
    val requestText = RequestStartType(text = key)
    val requestJson = requestAdapter.toJson(requestText)

    val taskStatus: TaskStatus

    try {
        val r = NetUtils.sendPost(
            url = configCache!!.speech_api,
            method = "task",
            header = header,
            requestJson = requestJson
        )
        taskStatus = gsonParser.fromJson(r, TaskStatus::class.java)
    } catch (e: Exception) {
        return null
    }

    return if (taskStatus.request_successful) {
        var requestId: String
        var audioJson: String?
        val audioStatus: AudioStatus = GlobalScope.async {
            delay(3000L)
            requestId = taskStatus.id
            audioJson =
                NetUtils.sendGet(url = configCache!!.speech_api, false, "result", header, listOf(Pair("id", requestId)))
            return@async gsonParser.fromJson(audioJson, AudioStatus::class.java)
        }.await()

        if (audioStatus.request_successful && audioStatus.result.synthesis_successful) {
            Base64.getDecoder().decode(audioStatus.result.audio)
        } else null
    } else null
}

@DelicateCoroutinesApi
suspend fun getSpeechByteArray(bot: Bot, update: Update, args: List<String>) {
    val message = update.message!!
    var key: String? = null

    if (args.isNotEmpty()) {
        key = args[0]
    } else if (message.replyToMessage?.text != null) {
        key = message.replyToMessage!!.text!!
    }

    if (key != null) {
        if (key.length > 500) {
            message.replyToText(bot, update, LANG["xi_too_long"]!!)
            return
        }
        val editMessageId = message.replyToText(bot, update, LANG["getting"]!!)
        val result = getAudioBytes(key)
        message.edit(bot, editMessageId, LANG["processing"]!!)
        key = if (key.length > 5) "${key.substring(0, 5)}..." else "$key-"
        if (result != null) {
            val tempAudio = convertToTemp(result, "audio", ".mp3")
            message.edit(bot, editMessageId, LANG["sending"]!!)
            bot.deleteMessage(ChatId.fromId(message.chat.id), editMessageId)
            bot.sendAudio(
                chatId = ChatId.fromId(message.chat.id), audio = TelegramFile.ByFile(tempAudio),
                title = "$key${(1000..9999).random()}",
                replyToMessageId = message.messageId, replyMarkup = deleteButton(message.messageId)
            )
            tempAudio.delete()
        } else {
            message.edit(bot, editMessageId, LANG["process_error"]!!, deleteButton(message.messageId))
        }
    } else {
        message.replyToText(bot, update, LANG["xi_empty"]!!, deleteButton(message.messageId))
    }
}