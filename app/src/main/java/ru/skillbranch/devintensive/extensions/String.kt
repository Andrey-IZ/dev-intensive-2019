package ru.skillbranch.devintensive.extensions

fun String.truncate(count: Int = 16): String {
    val string = this.trimEnd()
    if (count > 3) {
        return this.slice(0 until count).trimEnd() + "..."
    }
    return string
}

fun String.stripHtml(): String {
    return this
        .replace("&", "")
        .replace(Regex("<[\\w\\s=\"/\\\\]+?>"), "")
        .replace(Regex("\\s+"), " ")
}