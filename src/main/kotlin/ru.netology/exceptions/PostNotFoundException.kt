package ru.netology.exceptions

import java.lang.RuntimeException

class PostNotFoundException(message: String = "Post not found") : RuntimeException(message)