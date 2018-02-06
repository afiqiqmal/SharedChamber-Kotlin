package com.chamber.kotlin.sample

/**
 * @author : hafiq on 22/09/2017.
 */

class Task {

    var id: Int = 0
    var project_id: Int = 0
    var user_id: Int = 0
    var title: String? = null
    var description: String? = null

    override fun toString(): String {
        return "Task{" +
                "id=" + id +
                ", project_id=" + project_id +
                ", user_id=" + user_id +
                ", title='" + title + '\''.toString() +
                ", description='" + description + '\''.toString() +
                '}'.toString()
    }
}
