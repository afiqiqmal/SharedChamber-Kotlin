package com.chamber.kotlin.sample

/**
 * @author : hafiq on 22/09/2017.
 */

class User {

    var id: Int = 0
    var name: String? = null
    var email: String? = null

    override fun toString(): String {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\''.toString() +
                ", email='" + email + '\''.toString() +
                '}'.toString()
    }
}
