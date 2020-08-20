package ru.skillbranch.devintensive.utils

import java.util.*

object Utils {
    fun parseFullName(fullName: String?): Pair<String?, String?> {
        if (fullName.isNullOrEmpty() || fullName.trim().isEmpty()) return Pair(null, null)

        val parts: List<String>? = fullName.split(" ")
        val firstName = parts?.getOrNull(0)
        val lastName = parts?.getOrNull(1)
        return firstName to lastName
    }

    fun transliteration(payload:String, divider:String = " ") :String {
        TODO()
    }

    fun toInitials(firstName: String?, lastName: String?): String? {

        if (firstName.isNullOrEmpty() || firstName.trim().isEmpty())
            return null

        var result:String = firstName.take(1).capitalize()
        lastName?.let {
            result += it.take(1).capitalize()
        }
        return result
    }
}