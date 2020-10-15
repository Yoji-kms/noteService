package ru.netology.attachment

import ru.netology.attachment.types.*

sealed class Attachment{
    data class AttachmentAudio (val audio: Audio){
        val attachmentType = AttachmentType.AUDIO
    }

    data class AttachmentVideo (val video: Video){
        val attachmentType = AttachmentType.VIDEO
    }

    data class AttachmentPhoto (val photo: Photo){
        val attachmentType = AttachmentType.PHOTO
    }

    data class AttachmentDoc (val doc: Doc){
        val attachmentType = AttachmentType.DOC
    }

    data class AttachmentLink (val link: Link){
        val attachmentType = AttachmentType.LINK
    }
}