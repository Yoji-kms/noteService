package ru.netology.attachment.types

data class Video(
    val id: Int,
    val ownerId: Int,
    val title: String,
    val description: String?,
    val duration: Int,
    val link: String,
    val image: String,
    val imageMedium: String,
    val date: Int,
    val player: String
)