package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
  @PrimaryKey
  val id: String,
  val eventId: String,
  val userName: String,
  val userRole: String, // "Student", "Organizer", "Staff", "Admin"
  val text: String,
  val timeAgo: String,
  val timestamp: Long = System.currentTimeMillis(),
  val likes: Int = 0,
  val isLiked: Boolean = false,
  val isPinned: Boolean = false
)
