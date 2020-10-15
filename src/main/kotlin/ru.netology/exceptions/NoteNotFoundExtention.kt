package ru.netology.exceptions

import java.lang.RuntimeException

class NoteNotFoundException(message: String = "Note not found") : RuntimeException(message)