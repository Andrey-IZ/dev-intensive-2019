package ru.skillbranch.devintensive.utils

import android.content.Context
import java.util.*
import kotlin.collections.HashSet
import kotlin.math.roundToInt

object Utils {
    fun parseFullName(fullName: String?): Pair<String?, String?> {
        if (fullName.isNullOrEmpty() || fullName.trim().isEmpty()) return Pair(null, null)

        val parts: List<String>? = fullName.split(" ")
        val firstName = parts?.getOrNull(0)
        val lastName = parts?.getOrNull(1)
        return firstName to lastName
    }

    fun transliteration(payload:String, divider:String = " "): String {
        val dict = mapOf(
            "а" to "a",
            "б" to "b",
            "в" to "v",
            "г" to "g",
            "д" to "d",
            "е" to "e",
            "ё" to "e",
            "ж" to "zh",
            "з" to "z",
            "и" to "i",
            "й" to "i",
            "к" to "k",
            "л" to "l",
            "м" to "m",
            "н" to "n",
            "о" to "o",
            "п" to "p",
            "р" to "r",
            "с" to "s",
            "т" to "t",
            "у" to "u",
            "ф" to "f",
            "х" to "h",
            "ц" to "c",
            "ч" to "ch",
            "ш" to "sh",
            "щ" to "sh'",
            "ъ" to "",
            "ы" to "i",
            "ь" to "",
            "э" to "e",
            "ю" to "yu",
            "я" to "ya"
        )

        return payload.map {
            val char = it.toString()
                if (char == " ")
                    return@map divider
                var newChar = dict[char.toLowerCase(Locale.getDefault())]
                if (newChar.isNullOrEmpty()) return@map char

                if (it.isUpperCase())
                    newChar = newChar.capitalize()
                return@map newChar
            }.joinToString("")
    }

    fun toInitials(firstName: String?, lastName: String?): String? {
        var result = ""
        if (!(firstName.isNullOrEmpty() || firstName.trim().isEmpty())) {
            result += firstName.take(1).capitalize()
        }
        if (!(lastName.isNullOrEmpty() || lastName.trim().isEmpty())) {
            result += lastName.take(1).capitalize()
        }

        if (result.isNotEmpty())
            return result
        return null
    }

    fun convertPxToDp(context: Context, px: Int): Int {
        return (px / context.resources.displayMetrics.density).roundToInt()
    }

    fun convertDpToPx(context: Context, dp: Float): Int {
        return (dp * context.resources.displayMetrics.density).roundToInt()
    }

    fun convertSpToPx(context: Context, sp: Int): Int {
        return sp * context.resources.displayMetrics.scaledDensity.roundToInt()
    }
}