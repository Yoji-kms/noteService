package ru.netology

data class Report (
    val ownerId: Int,
    val commentId: Int,
    val reportReason: ReportReason
)