package ru.netology.exceptions

import java.lang.RuntimeException

class CommentDoesNotBelongToThisOwnerException(
    message: String = "Comment doesn`t belong to this user"
) : RuntimeException(message)