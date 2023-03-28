package command.rawtext

import LANG
import callback.deleteButton
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.Update
import dsl.getFullName
import dsl.replyToText
import momo_seed_letter
import momo_seed_text

object PeopleCommand {

    private fun String.getCount(son: String): Int {
        var fromIndex = 0
        var count = 0
        while (true) {
            val index: Int = this.indexOf(son, fromIndex)
            if (-1 != index) {
                fromIndex = index + 1
                count++
            } else {
                break
            }
        }
        return count
    }

    fun withCommand(bot: Bot, update: Update, command: String) {
        val noReply: String?
        val withSelf: String?
        val withPeople: String?
        when {
            command.startsWith("/toutou") -> {
                noReply = LANG["toutou_no_reply"]!!
                withSelf = LANG["toutou_self"]!!
                withPeople = LANG["toutou_with"]!!
            }

            command.startsWith("/tietie") -> {
                noReply = LANG["tietie_no_reply"]!!
                withSelf = LANG["tietie_self"]!!
                withPeople = LANG["tietie_with"]!!
            }

            command.startsWith("/zouzou") -> {
                noReply = LANG["zouzou_no_reply"]!!
                withSelf = LANG["zouzou_self"]!!
                withPeople = LANG["zouzou_with"]!!
            }

            command.startsWith("/momo") -> {
                noReply = LANG["momo_no_reply"]!!
                withSelf = LANG["momo_self"]!!
                withPeople = LANG["momo_with"]!!
            }

            else -> return
        }
        val message = update.message
        if (message!!.replyToMessage == null) {
            update.message!!.replyToText(bot, update, noReply, deleteButton(update.message!!.messageId))
            return
        }
        val myId = message.from!!.id
        val peopleId = message.replyToMessage!!.from!!.id
        val myName = message.getFullName()
        val peopleName = message.replyToMessage!!.getFullName()


        if (myName == peopleName) {
            update.message!!.replyToText(
                bot, update, withSelf.format(myId, myName),
                deleteButton(update.message!!.messageId), ParseMode.HTML
            )

        } else {
            val formatCount = withPeople.getCount("%s")
            if (formatCount == 4) {
                update.message!!.replyToText(
                    bot, update, withPeople.format(myId, myName, peopleId, peopleName),
                    deleteButton(update.message!!.messageId), ParseMode.HTML
                )
            } else {
                update.message!!.replyToText(
                    bot, update, withPeople.format(
                        myId, myName, peopleId, peopleName,
                        momo_seed_letter.random(), peopleId, peopleName, momo_seed_text.random()
                    ),
                    deleteButton(update.message!!.messageId), ParseMode.HTML
                )
            }
        }
    }
}