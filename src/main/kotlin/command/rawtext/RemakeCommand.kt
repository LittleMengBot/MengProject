package command.rawtext

import LANG
import callback.deleteButton
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Update
import dsl.getFullName
import dsl.replyToText

val P = listOf(
    0.00001, 0.00001, 0.00001, 0.00001, 0.00001, 0.00001, 0.00001, 0.00001, 0.00001, 0.00002, 0.00001,
    0.00002, 0.00003, 0.00003, 0.00003, 0.00003, 0.00003, 0.00003, 0.00003, 0.00004, 0.00004, 0.00004, 0.00004,
    0.00005, 0.00005, 0.00005, 0.00005, 0.00001, 0.00006, 0.00007, 0.00007, 0.00008, 0.00009, 0.00009, 0.00009,
    0.00009, 0.00009, 0.00009, 0.0001, 0.00013, 0.00013, 0.00014, 0.00015, 0.00016, 0.00016, 0.00016, 0.00023,
    0.00027, 0.0003, 0.0003, 0.00032, 0.00034, 0.00035, 0.00043, 0.00044, 0.00048, 0.00049, 0.00049, 0.0005,
    0.0005, 0.00062, 0.00064, 0.00067, 0.00073, 0.00074, 0.00074, 0.00096, 0.00101, 0.00103, 0.00104, 0.00108,
    0.0011, 0.00116, 0.00146, 0.00146, 0.00152, 0.00158, 0.00164, 0.00164, 0.00176, 0.00176, 0.00188, 0.00207,
    0.00207, 0.00219, 0.00225, 0.00231, 0.00243, 0.00255, 0.0028, 0.00292, 0.00304, 0.0031, 0.00322, 0.00328,
    0.00334, 0.00347, 0.00359, 0.00389, 0.00389, 0.00377, 0.00395, 0.00395, 0.00401, 0.00413, 0.00438, 0.00438,
    0.00444, 0.00444, 0.00444, 0.00456, 0.00462, 0.00474, 0.00492, 0.00499, 0.00505, 0.00505, 0.00547, 0.00553,
    0.00553, 0.00559, 0.00572, 0.00596, 0.00632, 0.00644, 0.00663, 0.00675, 0.00675, 0.00687, 0.00693, 0.00699,
    0.00724, 0.0076, 0.00778, 0.00784, 0.00803, 0.00809, 0.00821, 0.00845, 0.00869, 0.00869, 0.00888, 0.00894,
    0.00918, 0.00918, 0.00924, 0.0093, 0.00936, 0.00979, 0.00991, 0.01088, 0.01107, 0.00122, 0.00128, 0.00134,
    0.00134, 0.00134, 0.0014, 0.0014, 0.0014, 0.0014, 0.00146, 0.00146, 0.00146, 0.00152, 0.00152, 0.00158, 0.0017,
    0.00176, 0.00188, 0.00195, 0.00201, 0.00201, 0.00207, 0.00207, 0.00219, 0.00231, 0.00237, 0.00237, 0.00243,
    0.00249, 0.00261, 0.00261, 0.00261, 0.00268, 0.00286, 0.00286, 0.00298, 0.0031, 0.00322, 0.00334, 0.0034,
    0.0034, 0.0034, 0.00359, 0.00365, 0.00395, 0.00395, 0.00407, 0.00444, 0.0045, 0.00468, 0.0048, 0.00529, 0.00535,
    0.00547, 0.00651, 0.00657, 0.00663, 0.00675, 0.00772, 0.00772, 0.00845, 0.00857, 0.01015, 0.01058, 0.01173,
    0.01338, 0.01524, 0.01651, 0.01699, 0.0214, 0.02627, 0.10786, 0.11126
)

val country = listOf(
    "科科斯（基林）群島",
    "梵蒂岡",
    "托克勞",
    "紐埃",
    "福克蘭群島",
    "聖赫勒拿",
    "蒙哲臘",
    "吐瓦魯",
    "諾魯",
    "庫克群島",
    "安圭拉",
    "帛琉",
    "英屬維京群島",
    "直布羅陀",
    "聖馬利諾",
    "摩納哥",
    "特克斯與凱科斯群島",
    "列支敦斯登",
    "荷屬聖馬丁",
    "北馬利亞納群島",
    "法羅群島",
    "格陵蘭",
    "馬紹爾群島",
    "聖克里斯多福與尼維斯",
    "美屬薩摩亞",
    "開曼群島",
    "百慕達",
    "安道爾",
    "多米尼克",
    "曼島",
    "安地卡及巴布達",
    "塞席爾",
    "密克羅尼西亞聯邦",
    "阿魯巴",
    "美屬維京群島",
    "東加",
    "格瑞那達",
    "聖文森及格瑞那丁",
    "吉里巴斯",
    "古拉索",
    "澤西/耿西",
    "關島",
    "聖露西亞",
    "薩摩亞",
    "聖多美普林西比",
    "萬那杜",
    "巴貝多",
    "冰島",
    "貝里斯",
    "馬爾地夫",
    "巴哈馬",
    "馬爾他",
    "汶萊",
    "維德角",
    "蘇利南",
    "盧森堡",
    "撒拉威阿拉伯民主共和國",
    "索羅門群島",
    "澳門",
    "蒙特內哥羅",
    "蓋亞那",
    "不丹",
    "葛摩",
    "斐濟",
    "赤道幾內亞",
    "吉布地",
    "賽普勒斯",
    "東帝汶",
    "模里西斯",
    "愛沙尼亞",
    "史瓦濟蘭",
    "千里達及托巴哥",
    "巴林",
    "科索沃",
    "加彭",
    "拉脫維亞",
    "幾內亞比索",
    "斯洛維尼亞",
    "馬其頓",
    "甘比亞",
    "賴索托",
    "波札那",
    "卡達",
    "納米比亞",
    "立陶宛",
    "牙買加",
    "阿爾巴尼亞",
    "亞美尼亞",
    "蒙古",
    "烏拉圭",
    "波多黎各",
    "波赫",
    "喬治亞",
    "摩爾多瓦",
    "巴拿馬",
    "克羅埃西亞",
    "茅利塔尼亞",
    "科威特",
    "賴比瑞亞",
    "紐西蘭",
    "愛爾蘭",
    "剛果共和國",
    "哥斯大黎加",
    "巴勒斯坦",
    "中非",
    "挪威",
    "斯洛伐克",
    "芬蘭",
    "厄利垂亞",
    "土庫曼",
    "阿曼",
    "丹麥",
    "新加坡",
    "薩爾瓦多",
    "吉爾吉斯",
    "利比亞",
    "尼加拉瓜",
    "獅子山",
    "巴拉圭",
    "黎巴嫩",
    "保加利亞",
    "寮國",
    "香港",
    "多哥",
    "巴布亞紐幾內亞",
    "約旦",
    "宏都拉斯",
    "以色列",
    "瑞士",
    "奧地利",
    "塞爾維亞",
    "塔吉克",
    "白俄羅斯",
    "阿聯",
    "匈牙利",
    "瑞典",
    "亞塞拜然",
    "葡萄牙",
    "捷克",
    "希臘",
    "多明尼加",
    "海地",
    "玻利維亞",
    "古巴",
    "索馬利亞",
    "比利時",
    "突尼西亞",
    "貝南",
    "蒲隆地",
    "盧安達",
    "幾內亞",
    "南蘇丹",
    "查德",
    "柬埔寨",
    "塞內加爾",
    "辛巴威",
    "厄瓜多",
    "荷蘭",
    "敘利亞",
    "瓜地馬拉",
    "尚比亞",
    "哈薩克",
    "智利",
    "馬拉威",
    "羅馬尼亞",
    "馬利",
    "布吉納法索",
    "斯里蘭卡",
    "尼日",
    "台灣",
    "象牙海岸",
    "澳洲",
    "喀麥隆",
    "北韓",
    "馬達加斯加",
    "安哥拉",
    "葉門",
    "迦納",
    "尼泊爾",
    "莫三比克",
    "烏茲別克",
    "委內瑞拉",
    "馬來西亞",
    "祕魯",
    "沙烏地阿拉伯",
    "阿富汗",
    "摩洛哥",
    "加拿大",
    "波蘭",
    "伊拉克",
    "阿爾及利亞",
    "烏克蘭",
    "烏干達",
    "蘇丹",
    "阿根廷",
    "西班牙",
    "肯亞",
    "哥倫比亞",
    "韓國",
    "緬甸",
    "南非",
    "坦尚尼亞",
    "義大利",
    "英國",
    "法國",
    "泰國",
    "德國",
    "伊朗",
    "土耳其",
    "剛果民主共和國",
    "越南",
    "埃及",
    "菲律賓",
    "衣索比亞",
    "日本",
    "墨西哥",
    "俄羅斯",
    "孟加拉",
    "奈及利亞",
    "巴基斯坦",
    "巴西",
    "印度尼西亞",
    "米國",
    "印度",
    "支那"
)

fun generateCountry(): String {
    val tempCountry = mutableListOf<String>()
    for (pIndex in (P.indices)) {
        val p = (P[pIndex] * 100000).toInt()
        tempCountry.addAll(Array(p) { country[pIndex] }.toList())
    }
    return tempCountry.random()
}

fun remakeCommand(bot: Bot, update: Update) {
    val message = update.message!!
    when (val country = generateCountry()) {
        "米國", "加拿大", "德國", "英國", "法國",
        "義大利", "比利時", "盧森堡", "愛爾蘭", "希臘",
        "西班牙", "葡萄牙", "奧地利", "澳門",
        "台灣", "新加坡", "韓國", "瑞士",
        "阿聯", "澳州", "紐西蘭" -> {
            message.replyToText(bot, update, LANG["remake_congratulation"]!!.format(message.getFullName(), country))
        }

        "丹麥", "芬蘭", "冰島", "挪威", "瑞典" -> {
            message.replyToText(bot, update, LANG["remake_nEurope"]!!.format(message.getFullName(), country))
        }

        "北韓", "緬甸", "敘利亞", "古巴",
        "越南", "支那", "阿富汗" -> {
            message.replyToText(bot, update, LANG["remake_sad"]!!.format(message.getFullName(), country))
        }

        "日本" -> {
            message.replyToText(bot, update, LANG["remake_japan"]!!.format(message.getFullName()))
        }

        "荷蘭" -> {
            message.replyToText(bot, update, LANG["remake_Netherlands"]!!.format(message.getFullName()))
        }

        "香港" -> {
            message.replyToText(bot, update, LANG["remake_HongKong"]!!.format(message.getFullName()))
        }

        else -> {
            message.replyToText(
                bot, update, LANG["remake"]!!.format(message.getFullName(), country),
                replyMarkup = deleteButton(messageId = message.messageId)
            )
        }
    }


}