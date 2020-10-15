package ru.netology.exceptions

import java.lang.RuntimeException

class CommentNotDeletedException(message: String = "Comment is not deleted") : RuntimeException(message)