package ru.netology

import ru.netology.exceptions.CommentNotFoundException
import ru.netology.exceptions.CommentOwnerNotFoundException
import ru.netology.exceptions.PostNotFoundException

object WallService {
    private val posts = mutableListOf<Post>()
    private val comments = mutableListOf<Comment>()
    private val reports = mutableListOf<Report>()

    fun clear() {
        posts.clear()
        comments.clear()
    }

    fun createComment(comment: Comment, postId: Int): Comment {
        val post: Post = findPostById(postId)

        val postComments = post.comments?.comments ?: mutableListOf()
        val newComment = comment.copy(id = comments.lastIndex + 1)

        comments += newComment
        postComments += newComment
        update(post.copy(comments = Comments(comments = postComments)))
        return newComment
    }

    private fun findPostById(postId: Int): Post {
        for (post: Post in posts) {
            if (post.id == postId) return post
        }
        throw PostNotFoundException()
    }

    fun add(post: Post): Post {
        val newPost = post.copy(id = posts.lastIndex + 1)
        posts += newPost
        return newPost
    }

    fun update(post: Post): Boolean {
        for ((index, updatingPost) in posts.withIndex()) {
            if (index == post.id) {
                posts[index] = post.copy(
                    ownerId = updatingPost.ownerId,
                    date = updatingPost.date
                )
                return true
            }
        }
        return false
    }

    fun reportComment(report: Report): Boolean {
        val ownerComments = mutableListOf<Comment>()

        for (comment: Comment in comments)
            if (comment.fromId == report.ownerId) {
                ownerComments += comment
            }

        if (ownerComments.isNotEmpty()) {
            for (comment: Comment in ownerComments)
                if (comment.id == report.commentId) {
                    reports += report
                    return true
                }
            throw CommentNotFoundException()
        }
        throw CommentOwnerNotFoundException()
    }
}