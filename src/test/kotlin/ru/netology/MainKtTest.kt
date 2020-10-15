package ru.netology

import org.junit.Test

import org.junit.Assert.*
import ru.netology.exceptions.*
import ru.netology.notes.*

class MainKtTest {

    //Wall service tests

    @Test
    fun main_add() {
        val wall = WallService
        val post = Post()

        wall.clear()
        wall.add(post)
        wall.add(post.copy(date = 1))
        val lastPost = wall.add(post.copy(date = 2))

        assertNotEquals(0, lastPost.id)
    }

    @Test
    fun main_update_existing() {
        val wall = WallService
        val post = Post()

        wall.clear()
        wall.add(post)
        wall.add(post.copy(date = 1))
        wall.add(post.copy(date = 2))

        val updated = wall.update(post.copy(id = 1, text = "new text"))

        assertTrue(updated)
    }

    @Test
    fun main_update_notExisting() {
        val wall = WallService
        val post = Post()

        wall.clear()
        wall.add(post)
        wall.add(post.copy(date = 1))
        wall.add(post.copy(date = 2))

        val updated = wall.update(post.copy(id = 3, text = "new text"))

        assertFalse(updated)
    }

    @Test
    fun main_equalPosts() {
        val post = Post()
        val clonePost = post.copy()

        assertEquals(post, clonePost)
    }

    @Test
    fun main_hashPost() {
        val hash = Post().hashCode()

        assertNotEquals(0, hash)
    }

    @Test(expected = PostNotFoundException::class)
    fun main_createCommentThrowsException() {
        WallService.createComment(Comment(), Int.MIN_VALUE)
    }

    @Test
    fun main_createComment() {
        val wall = WallService

        wall.clear()
        val post = wall.add(Post())

        val result = wall.createComment(Comment(), post.id)

        assertNotNull(result)
    }

    @Test
    fun main_report_success() {
        val wall = WallService

        wall.clear()
        val post = wall.add(Post())
        val comment = Comment()

        wall.createComment(comment, post.id)
        wall.createComment(comment, post.id)
        val lastComment = wall.createComment(comment, post.id)

        val result = wall.reportComment(
            Report(
                ownerId = lastComment.fromId,
                commentId = lastComment.id,
                reportReason = ReportReason.SPAM
            )
        )

        assertTrue(result)
    }

    @Test(expected = CommentNotFoundException::class)
    fun main_report_commentNotFoundException() {
        val wall = WallService

        wall.clear()
        val post = wall.add(Post())
        val comment = wall.createComment(Comment(), post.id)

        wall.reportComment(
            Report(
                ownerId = comment.fromId,
                commentId = comment.id + 1,
                reportReason = ReportReason.ABUSE
            )
        )
    }

    @Test(expected = CommentOwnerNotFoundException::class)
    fun main_report_commentOwnerNotFoundException() {
        val wall = WallService

        wall.clear()
        val post = wall.add(Post())
        val comment = wall.createComment(Comment(), post.id)

        wall.reportComment(
            Report(
                ownerId = comment.fromId + 1,
                commentId = comment.id,
                reportReason = ReportReason.VIOLENCE
            )
        )
    }

    //Note service tests

    @Test
    fun main_note_getById_success() {
        val notes = NoteService
        notes.clear()

        notes.add(Note())
        val gettingNote = notes.add(Note(text = "Some text"))
        notes.add(Note())

        assertEquals(gettingNote, notes.getById(gettingNote.id))
    }

    @Test(expected = NoteNotFoundException::class)
    fun main_noteNotFoundException() {
        val notes = NoteService
        notes.clear()

        notes.getById(Int.MIN_VALUE)
    }

    @Test
    fun main_note_update_success() {
        val notes = NoteService
        notes.clear()

        notes.add(Note())
        val updatingNote = notes.add(Note(text = "Some text"))
        val newNote = updatingNote.copy(text = "New note text")

        assertTrue(notes.update(newNote))
    }

    @Test(expected = NoteNotFoundException::class)
    fun main_note_update_NotFoundException() {
        val notes = NoteService
        notes.clear()

        notes.update(Note())
    }

    @Test
    fun main_note_delete() {
        val notes = NoteService
        notes.clear()

        notes.add(Note())
        val deletingNoteId = notes.add(Note()).id
        notes.add(Note())

        assertTrue(notes.delete(deletingNoteId))
    }

    @Test
    fun main_noteComment_getComments_success(){
        val notes = NoteService
        notes.clear()

        val noteId = notes.add(Note()).id

        val comments = mutableListOf<Comment>()

        val comment1 = notes.createComment(comment = Comment(text = "Comment 1"), noteId = noteId)
        val comment2 = notes.createComment(comment = Comment(text = "Comment 2"), noteId = noteId)

        comments+= comment1
        comments += comment2

        assertEquals(comments, notes.getComments(noteId = noteId))
    }

    @Test(expected = CommentOwnerNotFoundException::class)
    fun main_noteComment_getComments_commentOwnerNotFound(){
        val notes = NoteService
        notes.clear()

        val noteId = notes.add(Note()).id

        notes.createComment(comment = Comment(), noteId = noteId)

        notes.getComments(noteId = noteId, ownerId = Int.MIN_VALUE)
    }

    @Test(expected = CommentNotFoundException::class)
    fun main_noteComment_getComments_commentNotFound(){
        val notes = NoteService
        notes.clear()

        val noteId = notes.add(Note()).id

        val commentId = notes.createComment(comment = Comment(), noteId = noteId).id
        notes.deleteComment(commentId = commentId)

        notes.getComments(noteId = noteId)
    }

    @Test
    fun main_noteComment_updateComment_success(){
        val notes = NoteService
        notes.clear()

        val noteId = notes.add(Note()).id

        notes.createComment(comment = Comment(), noteId = noteId)
        val updatingComment = notes.createComment(comment = Comment(), noteId = noteId)
        notes.createComment(comment = Comment(), noteId = noteId)

        val newComment = updatingComment.copy(text = "New text")

        assertTrue(notes.updateComment(newComment))
    }

    @Test(expected = CommentNotFoundException::class)
    fun main_noteComment_updateComment_commentNotFound(){
        val notes = NoteService
        notes.clear()

        val noteId = notes.add(Note()).id

        notes.createComment(comment = Comment(), noteId = noteId)
        notes.createComment(comment = Comment(), noteId = noteId)

        notes.updateComment(Comment(id = Int.MIN_VALUE))
    }

    @Test(expected = CommentDoesNotBelongToThisOwnerException::class)
    fun main_noteComment_updateComment_commentNotBelongToOwner(){
        val notes = NoteService
        notes.clear()

        val noteId = notes.add(Note()).id

        notes.createComment(comment = Comment(), noteId = noteId)
        val updatingComment = notes.createComment(comment = Comment(fromId = 1), noteId = noteId)
        notes.createComment(comment = Comment(), noteId = noteId)

        val newComment = updatingComment.copy(text = "New text", fromId = 0)

        notes.updateComment(newComment)
    }

    @Test
    fun main_noteComment_deleteComment_success(){
        val notes = NoteService
        notes.clear()

        val noteId = notes.add(Note()).id

        val commentId = notes.createComment(comment = Comment(), noteId = noteId).id
        assertTrue(notes.deleteComment(commentId = commentId))
    }

    @Test(expected = CommentNotFoundException::class)
    fun main_noteComment_deleteComment_commentNotFound(){
        val notes = NoteService
        notes.clear()

        val noteId = notes.add(Note()).id
        notes.createComment(comment = Comment(), noteId = noteId)

        notes.deleteComment(commentId = Int.MIN_VALUE)
    }

    @Test
    fun main_noteComment_restoreComment_success(){
        val notes = NoteService
        notes.clear()

        val noteId = notes.add(Note()).id

        val commentId = notes.createComment(comment = Comment(), noteId = noteId).id
        notes.deleteComment(commentId = commentId)

        assertTrue(notes.restoreComment(commentId = commentId))
    }

    @Test(expected = CommentNotFoundException::class)
    fun main_noteComment_restoreComment_commentNotFound(){
        val notes = NoteService
        notes.clear()

        val noteId = notes.add(Note()).id

        notes.createComment(comment = Comment(), noteId = noteId)
        notes.restoreComment(commentId = Int.MIN_VALUE)
    }

    @Test(expected = CommentDoesNotBelongToThisOwnerException::class)
    fun main_noteComment_restoreComment_commentNotBelongToOwner(){
        val notes = NoteService
        notes.clear()

        val noteId = notes.add(Note()).id

        val ownerId = notes.createComment(comment = Comment(), noteId = noteId).fromId
        val restoringCommentId = notes.createComment(comment = Comment(fromId = ownerId + 1), noteId = noteId).id
        notes.restoreComment(commentId = restoringCommentId, ownerId = ownerId)
    }

    @Test(expected = CommentNotDeletedException::class)
    fun main_noteComment_restoreComment_commentNotDeleted(){
        val notes = NoteService
        notes.clear()

        val noteId = notes.add(Note()).id

        val commentId = notes.createComment(comment = Comment(), noteId = noteId).id
        notes.restoreComment(commentId = commentId)
    }
}