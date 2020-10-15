package ru.netology.exceptions

import java.lang.RuntimeException

class CommentNotFoundException(message: String = "Comment not found") : RuntimeException(message)