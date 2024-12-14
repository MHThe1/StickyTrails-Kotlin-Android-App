package com.work.stickytrails.models

data class NoteModel(
    val id: String,
    val title: String,
    val content: String,
    val color: String,
    val priority: Int,
    val user_id: String,
    val createdAt: String,
    val updatedAt: String
)
