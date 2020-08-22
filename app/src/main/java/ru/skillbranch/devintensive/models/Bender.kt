package ru.skillbranch.devintensive.models

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
        return if (question.answers.contains(answer)) {
            question = question.nextQuestion()
            "Отлично - это правильный ответ!\n ${question.question}" to status.color
        } else {
            status = status.nextStatus()
            "Это не правильный ответ!\n ${question.question}" to status.color
        }
    }

    enum class Question(val question: String, val answers: List<String>) {
        NAME("Как меня зовут?", listOf("бендер", "bender")) {
            override fun nextQuestion():Question=PROFESSION
        },
        PROFESSION("Назови мою профессию?", listOf("сгибальщик", "bender")){
            override fun nextQuestion():Question=PROFESSION
        },
        MATERIAL("Из чего я сделан?", listOf("металл", "дерево", "metal", "iron", "wood", "bender")){
            override fun nextQuestion():Question=MATERIAL
        },
        BDAY("Когда меня сделали?", listOf("2993")){
            override fun nextQuestion():Question=BDAY
        },
        SERIAL("Мой серийны номер?", listOf("271564168")){
            override fun nextQuestion():Question=SERIAL
        },
        IDLE("На этом все, вопросов больше нет", emptyList()){
            override fun nextQuestion():Question=NAME
        };

        abstract fun nextQuestion():Question
    }

    enum class Status(val color: Triple<Int, Int, Int>) {
        NORMAL(Triple(255, 255, 255)),
        WARNING(Triple(255, 120, 0)),
        DANGER(Triple(255, 60, 60)),
        CRITICAL(Triple(255, 255, 0));

        fun nextStatus():Status {
            return if (this.ordinal < values().lastIndex) {
                values()[this.ordinal + 1]
            } else {
                values()[0]
            }
        }
    }
}
