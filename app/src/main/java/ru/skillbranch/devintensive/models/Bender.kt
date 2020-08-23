package ru.skillbranch.devintensive.models

import androidx.core.text.isDigitsOnly
import java.util.*

class Bender(
        var status: Status = Status.NORMAL,
        var question: Question = Question.NAME
) {
    fun askQuestion(): String = when (question) {
        Question.NAME -> Question.NAME.question
        Question.PROFESSION -> Question.PROFESSION.question
        Question.MATERIAL -> Question.MATERIAL.question
        Question.BDAY -> Question.BDAY.question
        Question.SERIAL -> Question.SERIAL.question
        Question.IDLE -> Question.IDLE.question
    }

    fun listenAnswer(answer: String): Pair<String, Triple<Int, Int, Int>> {
        val getCritics = question.validation(answer)
        val isValid = getCritics == null
        if (!isValid)
            return "$getCritics\n${question.question}" to status.color

        return if (question.answers.contains(answer.toLowerCase(Locale.ROOT))) {
            question = question.nextQuestion()
            "Отлично - ты справился\n${question.question}" to status.color
        } else {
            return if (status == Status.CRITICAL) {
                status = Status.NORMAL
                question = Question.NAME
                "Это неправильный ответ. Давай все по новой\n${question.question}" to status.color
            } else {
                if (isValid) {
                    if(question != Question.IDLE) {
                        status = status.nextStatus()
                    } else {
                        return question.question to status.color
                    }
                }
                return "Это неправильный ответ\n${question.question}" to status.color
            }
        }
    }

    enum class Question(val question: String, val answers: List<String>) {
        NAME("Как меня зовут?", listOf("Бендер", "bender")) {
            override fun nextQuestion(): Question = PROFESSION
            override fun validation(answer: String): String? {
                if (answer.isNotEmpty() && answer[0].isLowerCase())
                    return "Имя должно начинаться с заглавной буквы"
                return null
            }
        },
        PROFESSION("Назови мою профессию?", listOf("сгибальщик", "bender")) {
            override fun nextQuestion(): Question = MATERIAL
            override fun validation(answer: String): String? {
                if (answer.isNotEmpty() && answer[0].isUpperCase())
                    return "Профессия должна начинаться со строчной буквы"
                return null
            }
        },
        MATERIAL("Из чего я сделан?", listOf("металл", "дерево", "metal", "iron", "wood", "bender")) {
            override fun nextQuestion(): Question = BDAY
            override fun validation(answer: String): String? {
                if (answer.isNotEmpty() && answer.any { it.isDigit() })
                    return "Материал не должен содержать цифр"
                return null
            }
        },
        BDAY("Когда меня сделали?", listOf("2993")) {
            override fun nextQuestion(): Question = SERIAL
            override fun validation(answer: String): String? {
                if (answer.isNotEmpty() && answer.all { it.isDigit() })
                    return "Год моего рождения должен содержать только цифры"
                return null
            }
        },
        SERIAL("Мой серийны номер?", listOf("2716057")) {
            override fun nextQuestion(): Question = IDLE
            override fun validation(answer: String): String? {
                if (answer.isNotEmpty() && answer.all { it.isDigit() }
                        && answer.length != 7 )
                    return "Серийный номер содержит только цифры, и их 7"
                return null
            }
        },
        IDLE("На этом все, вопросов больше нет", emptyList()) {
            override fun nextQuestion(): Question = IDLE
            override fun validation(answer: String): String? {
                return null
            }
        };

        abstract fun nextQuestion(): Question
        abstract fun validation(answer: String): String?
    }

    enum class Status(val color: Triple<Int, Int, Int>) {
        NORMAL(Triple(255, 255, 255)),
        WARNING(Triple(255, 120, 0)),
        DANGER(Triple(255, 60, 60)),
        CRITICAL(Triple(255, 255, 0));

        fun nextStatus(): Status {
            return if (this.ordinal < values().lastIndex) {
                values()[this.ordinal + 1]
            } else {
                values()[0]
            }
        }
    }
}
