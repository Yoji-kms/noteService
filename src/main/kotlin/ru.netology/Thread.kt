package ru.netology

data class Thread (
    val count: Int = 0,
    val items: Array<Comment>? = emptyArray(),
    val canPost: Boolean = false,
    val showReplyButton: Boolean = false,
    val groupsCanPost: Boolean = false
)