package ru.skillbranch.devintensive.models.data

import ru.skillbranch.devintensive.extensions.humanizeDiff
import ru.skillbranch.devintensive.utils.Utils
import java.util.*

data class User(
    val id : String,
    var firstName : String?,
    var lastName : String?,
    var avatar : String?,
    var rating : Int = 0,
    var respect : Int = 0,
    var lastVisit : Date? = Date(),
    var isOnline : Boolean = false
) {
    constructor (id: String, firstName: String?, lastName: String?) :this(
        id = id,
        firstName = firstName,
        lastName = lastName,
        avatar = null
    )
    constructor(id: String) : this (id, "John", "Doe")

    init {
        println("It's alive. His name: $id $firstName $lastName")
    }

    fun printMe() = println("""
        id : $id
        firstName: $firstName
        lastName: $lastName
        avatar : $avatar
        rating : $rating
        respect : $respect
        lastVisit: $lastVisit
        isOnline:  $isOnline
    """.trimIndent())

    fun toUserItem() : UserItem {
        val lastActivity = when {
            lastVisit == null -> "Ещу ни разу не заходил"
            isOnline -> "online"
            else -> "Последний раз был ${lastVisit!!.humanizeDiff()}"
        }

        return UserItem(
            id,
            "${firstName.orEmpty()} ${lastName.orEmpty()}",
            Utils.toInitials(firstName, lastName),
            avatar,
            lastActivity,
            false,
            isOnline
        )
    }

    class Builder{
        private lateinit var user: User

        fun id(id: String): Builder {
            user = User(id)
            return this
        }

        fun firstName(firstName: String?): Builder {
            user.firstName = firstName
            return this
        }
        fun lastName(lastName: String?): Builder {
            user.lastName = lastName
            return this
        }

        fun avatar(path: String?): Builder {
            user.avatar = path
            return this
        }

        fun rating(rating: Int): Builder {
            user.rating = rating
            return this
        }
        fun respect(respect: Int): Builder {
            user.respect = respect
            return this
        }
        fun lastVisit(lastVisit: Date): Builder {
            user.lastVisit = lastVisit
            return this
        }
        fun isOnline(isOnline: Boolean): Builder {
            user.isOnline = isOnline
            return this
        }

        fun build(): User {
            return user
        }
    }

    companion object Factory {
        private var lastId: Int = -1
        fun makeUser(fullname: String?) : User {
            lastId++
            val (firstName, lastName) = Utils.parseFullName(fullname)
            return User(id = "$lastId" ,  firstName = firstName, lastName = lastName)

        }
    }
}
class UserView (
        val id: String,
        val fullName: String,
        val nickName: String,
        val avatar:String?,
        val status:String = "offline",
        val initials:String?
) {
    fun printMe() {
        println("""
            id: $id
            fullName: $fullName
            nickName: $nickName
            avatar: $avatar
            status: $status
            initials: $initials
        """.trimIndent())
    }
}