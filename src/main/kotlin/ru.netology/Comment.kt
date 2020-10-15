package ru.netology

import ru.netology.attachment.Attachment

data class Comment(
    val id: Int = 0,
    val fromId: Int = 0,
    val date: Int = 0,
    val text: String = "text",
    val replyToUser: Int? = null,
    val replyToComment: Int? = null,
    val attachment: Attachment? = null,
    val parentsStack: List<Int> = emptyList(),
    val thread: Thread = Thread()
)