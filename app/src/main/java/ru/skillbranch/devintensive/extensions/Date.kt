package ru.skillbranch.devintensive.extensions

import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.ceil

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.format(pattern: String? = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}


fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time
    time += when (units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }

    this.time = time
    return this
}

fun Date.humanizeDiff(date:Date = Date()): String {
    fun timeConvertStr(time:Long, message:String) = if (time >= 0) "через $message" else "$message назад"

    val time = this.time - date.time

    return when(val timeAbs = abs(time)) {
        in 0..SECOND ->             "только что"
        in SECOND..45*SECOND ->     timeConvertStr(time, "несколько секунд")
        in 45*SECOND..75*SECOND ->  timeConvertStr(time,"минуту")
        in 75*SECOND..45*MINUTE ->  {
            val value = ceil((timeAbs / MINUTE.toDouble())).toInt()
            timeConvertStr(time,"$value ${TimeUnits.MINUTE.plural(value)}")
        }
        in 45*MINUTE..75*MINUTE ->  timeConvertStr(time,"час")
        in 75* MINUTE..22*HOUR ->   {
            val value = ceil((timeAbs / HOUR.toDouble())).toInt()
            timeConvertStr(time,"$value ${TimeUnits.HOUR.plural(value)}")
        }
        in 22* HOUR..26*HOUR ->     timeConvertStr(time,"день")
        in 26* HOUR..360*DAY ->     {
            val value = ceil((timeAbs / DAY.toDouble())).toInt()
            timeConvertStr(time,"$value ${TimeUnits.DAY.plural(value)}")
        }
        else ->                     if (time >= 0) "более чем через год" else "более года назад"

    }
}

enum class TimeUnits {
    SECOND {
        override fun plural(value:Int):String {
            return when(getNumber(value)) {
                1 -> "секунду"
                in 2..4 -> "секунды"
                else -> "секунд"
            }
    }},
    MINUTE {
        override fun plural(value:Int):String {
            return when(getNumber(value)) {
                1 -> "минуту"
                in 2..4 -> "минуты"
                else -> "минут"
            }
        }},
    HOUR {
        override fun plural(value:Int):String {
            return when(getNumber(value)) {
                1 -> "час"
                in 2..4 -> "часа"
                else -> "часов"
            }
        }},
    DAY {
        override fun plural(value:Int):String {
            return when(getNumber(value)) {
                1 -> "день"
                in 2..4 -> "дня"
                else -> "дней"
            }
        }};
    abstract fun plural(value:Int):String

    protected fun getNumber(value:Int): Int {
        return if (value >= 10) ((value / 10.0) % 1).toInt() else value
    }
}
