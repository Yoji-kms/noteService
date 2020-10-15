package ru.netology.attachment.types

data class Photo(
    val id: Int,
    val albumId: Int,
    val ownerId: Int,
    val userId: Int,
    val text: String?,
    val date: Int,
    val width: Int,
    val height: Int,
)