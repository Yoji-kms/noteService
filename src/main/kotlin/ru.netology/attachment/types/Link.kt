package ru.netology.attachment.types

data class Link(
    val url: String,
    val title: String,
    val caption: String?,
    val description: String?,
    val photo: Photo?,
    val previewPage: String,
    val previewUrl: String
)