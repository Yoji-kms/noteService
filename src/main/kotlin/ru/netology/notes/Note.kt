package ru.netology.notes

data class Note(
    val id: Int = 0,
    val ownerId: Int = 0,
    val title: String? = null,
    val text: String = "text",
    val date: Int = 0,
    val comments: List<Int> = emptyList(),
    val readComments: Int = 0,
    val viewUrl: String = ""
)