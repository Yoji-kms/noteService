package ru.netology.notes

import ru.netology.Comment
import ru.netology.exceptions.*

object NoteService {
    private val notes = mutableListOf<Note>()
    private val comments = mutableListOf<Comment>()
    private val deletedComments = mutableListOf<Int>()

    fun clear() {
        notes.clear()
        comments.clear()
        deletedComments.clear()
    }

    fun add(note: Note): Note {
        val newNote = note.copy(id = notes.lastIndex + 1)
        notes += newNote
        return newNote
    }

    fun createComment(comment: Comment, noteId: Int): Comment {
        val note: Note = getById(noteId)

        val noteComments = if (note.comments.isEmpty()) mutableListOf() else note.comments as MutableList<Int>
        val newComment = comment.copy(id = comments.lastIndex + 1)

        comments += newComment
        noteComments += newComment.id
        update(note.copy(comments = noteComments))
        return newComment
    }

    fun delete(noteId: Int): Boolean {
        val note = getById(noteId = noteId)

        for (commentId in note.comments) {
            for (deletedCommentId in deletedComments)
                if (deletedCommentId == commentId) deletedComments.remove(commentId)
            for (comment in comments) if (comment.id == commentId) comments.remove(comment)
        }

        notes.remove(note)
        return true
    }

    fun deleteComment(commentId: Int, ownerId: Int = 0): Boolean {
        val userComments = userComments(ownerId)

        for (comment in userComments) if (comment.id == commentId) {
            deletedComments += commentId
            return true
        }
        throw CommentNotFoundException()
    }

    fun update(note: Note): Boolean {
        for ((index, updatingNote) in notes.withIndex()) {
            if (index == note.id) {
                notes[index] = note.copy(
                    id = updatingNote.id,
                    ownerId = updatingNote.ownerId,
                    date = updatingNote.date
                )
                return true
            }
        }
        throw NoteNotFoundException()
    }

    fun updateComment(comment: Comment): Boolean {
        val newComments = mutableListOf<Comment>()
        for ((index, updatingComment) in comments.withIndex())
            newComments += if (index == comment.id) {
                comment.copy(
                    id = updatingComment.id,
                    fromId = updatingComment.fromId,
                    date = updatingComment.date
                )
            } else comments[index]

        if (newComments == comments) throw CommentNotFoundException()

        val userComments = userComments(comment.fromId)

        for (userComment in userComments)
            if (userComment.id == comment.id) {
                comments.clear()
                comments += newComments
                return true
            }
        throw CommentDoesNotBelongToThisOwnerException()
    }

    fun get(
        noteIds: List<Int> = emptyList(),
        userId: Int = 0,
        offset: Int = 0,
        count: Int = 20,
        sortDec: Boolean = true
    ): List<Note> {
        val userNotes = mutableListOf<Note>()
        for (note in notes) {
            if (note.ownerId == userId) userNotes += note
        }

        if (sortDec) userNotes.sortByDescending { it.date } else userNotes.sortBy { it.date }

        val userNotesByIds = if (noteIds.isNotEmpty()) {
            getNotesByIds(noteIds = noteIds, noteList = userNotes)
        } else userNotes

        if (userNotesByIds.isEmpty()) throw NoteNotFoundException()

        val size = userNotesByIds.size
        val rangedOffset = subtractionWithMaxValue(int = offset, max = size)

        return if (userNotesByIds.size < count) userNotesByIds else {
            if (rangedOffset <= size - count) {
                userNotesByIds.subList(fromIndex = rangedOffset, toIndex = count + rangedOffset)
            } else {
                userNotesByIds.subList(0, rangedOffset + count - size) + userNotesByIds.subList(rangedOffset, size)
            }
        }
    }

    private fun subtractionWithMaxValue(int: Int, max: Int): Int {
        return if (int > max) {
            subtractionWithMaxValue(int = int - max, max = max)
        } else int
    }

    private fun getNotesByIds(noteIds: List<Int>, noteList: MutableList<Note>): MutableList<Note> {
        val temp = mutableListOf<Note>()
        for (note in noteList) {
            for (id in noteIds) {
                if (note.id == id) temp += note
            }
        }
        if (temp.isNotEmpty()) return temp
        throw NoteNotFoundException()
    }

    fun getById(
        noteId: Int,
        ownerId: Int = 0,
//        needWiki: Boolean = false
    ): Note {
        for (note: Note in notes)
            if (note.id == noteId && note.ownerId == ownerId) return note
        throw NoteNotFoundException()
    }

    fun getComments(
        noteId: Int,
        ownerId: Int = 0,
        sortDec: Boolean = true,
        offset: Int = 0,
        count: Int = 20
    ): List<Comment> {
        val note = getById(noteId)

        val userComments = userComments(ownerId)

        val noteComments = mutableListOf<Comment>()
        for (comment in userComments)
            for (id in note.comments)
                if (comment.id == id) noteComments += comment

        if (sortDec) noteComments.sortByDescending { it.date } else noteComments.sortBy { it.date }

        val deletedNoteComments = mutableListOf<Comment>()

        for (comment in noteComments)
            for (deletedCommentId in deletedComments)
                if (comment.id == deletedCommentId) deletedNoteComments += comment

        noteComments.removeAll(deletedNoteComments)

        if (noteComments.isEmpty()) throw CommentNotFoundException()

        val size = noteComments.size
        val rangedOffset = subtractionWithMaxValue(int = offset, max = size)

        return listOffset(
            count = count,
            offset = rangedOffset,
            list = noteComments
        )
    }

    private fun <E> listOffset(count: Int, offset: Int, list: List<E>): List<E> {
        return if (list.size < count) list else {
            if (offset <= list.size - count) {
                list.subList(fromIndex = offset, toIndex = count + offset)
            } else {
                list.subList(0, offset + count - list.size) +
                        list.subList(offset, list.size)
            }
        }
    }

    private fun userComments(userId: Int): MutableList<Comment> {
        val userComments = mutableListOf<Comment>()
        for (comment in comments) if (comment.fromId == userId) userComments += comment
        if (userComments.isNotEmpty()) return userComments
        throw CommentOwnerNotFoundException()
    }

    fun restoreComment(commentId: Int, ownerId: Int = 0): Boolean {
        val userComments = userComments(ownerId)

        for (comment in comments) {
            if (comment.id == commentId) {
                for (userComment in userComments) {
                    if (userComment.id == commentId) {
                        for (id in deletedComments) {
                            if (id == commentId) {
                                deletedComments.remove(commentId)
                                return true
                            }
                        }
                        throw CommentNotDeletedException()
                    }
                }
                throw CommentDoesNotBelongToThisOwnerException()
            }
        }
        throw CommentNotFoundException()
    }
}