package command.api

import LANG
import callback.deleteButton
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.Update
import com.google.zxing.*
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import command.cache.QRCache.formatReader
import command.cache.QRCache.formatWriter
import dsl.replyToText
import mu.KotlinLogging
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.imageio.ImageIO

private val logger = KotlinLogging.logger {}
const val WIDTH = 250
const val HEIGHT = 250
const val FMT = "png"

fun parseQR(imageBytes: ByteArray): String? {
    return try {
        val img = ImageIO.read(ByteArrayInputStream(imageBytes))
        val bitmap = BinaryBitmap(GlobalHistogramBinarizer(BufferedImageLuminanceSource(img)))
        val map = HashMap<DecodeHintType, String>()
        map[DecodeHintType.CHARACTER_SET] = "utf-8"
        val result = formatReader!!.decode(bitmap, map)
        result.text
    } catch (e: IOException) {
        logger.error(e.toString())
        null
    } catch (e: NotFoundException) {
        logger.error(e.toString())
        null
    }
}

fun generateQR(text: String): ByteArray? {
    val map = HashMap<EncodeHintType, Any>()
    map[EncodeHintType.CHARACTER_SET] = "utf-8"
    map[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.M
    map[EncodeHintType.MARGIN] = 2

    return try {
        val bitMatrix = formatWriter!!.encode(text, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, map)
        val bs = ByteArrayOutputStream()
        MatrixToImageWriter.writeToStream(bitMatrix, FMT, bs)
        bs.toByteArray()
    } catch (e: IOException) {
        logger.error(e.toString())
        null
    } catch (e: WriterException) {
        logger.error(e.toString())
        null
    }

}

fun qrCommand(bot: Bot, update: Update, args: List<String>) {
    val message = update.message!!
    var photoByteArray: ByteArray? = null
    if (message.replyToMessage?.photo != null) {
        photoByteArray = bot.downloadFileBytes(
            message.replyToMessage!!.photo!![message.replyToMessage!!.photo!!.size - 1].fileId
        )
    }

    if (message.replyToMessage?.document != null) {
        if (message.replyToMessage!!.document?.mimeType == "image/png") {
            val document = message.replyToMessage!!.document!!
            photoByteArray = bot.downloadFileBytes(document.fileId)
        }
    }

    if (photoByteArray != null) {
        val text = parseQR(photoByteArray)
        if (text != null) {
            message.replyToText(
                bot, update, LANG["qr_head"]!!.format(text),
                deleteButton(update.message!!.messageId),
                ParseMode.MARKDOWN
            );return
        } else {
            message.replyToText(
                bot, update, LANG["qr_not_found"]!!,
                deleteButton(update.message!!.messageId),
                ParseMode.MARKDOWN
            );return
        }
    } else if (args.isNotEmpty()) {
        val sb = StringBuilder()
        args.forEach { sb.append(it);sb.append(" ") }
        val qrByte = generateQR(sb.toString().trim())
        if (qrByte != null) {
            bot.sendDocument(
                chatId = ChatId.fromId(message.chat.id),
                document = TelegramFile.ByByteArray(qrByte, "QR-${(1000000..9999999).random()}.png"),
                caption = LANG["qr_caption"],
                replyMarkup = deleteButton(update.message!!.messageId), replyToMessageId = message.messageId
            )
        } else {
            message.replyToText(
                bot, update, LANG["qr_generate_error"]!!,
                deleteButton(update.message!!.messageId),
                ParseMode.MARKDOWN
            );return
        }
    } else {
        message.replyToText(
            bot, update, LANG["qr_hint"]!!,
            deleteButton(update.message!!.messageId),
            ParseMode.MARKDOWN
        );return
    }
}
