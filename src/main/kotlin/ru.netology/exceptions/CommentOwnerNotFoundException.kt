package ru.netology.exceptions

import java.lang.RuntimeException

class CommentOwnerNotFoundException(message: String = "Comment owner not found") : RuntimeException(message)