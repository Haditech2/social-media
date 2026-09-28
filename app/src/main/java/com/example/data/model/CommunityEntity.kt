package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "communities")
data class CommunityEntity(
  @PrimaryKey
  val id: String,
  val name: String,
  val tag: String, // e.g. "NACOS", "SUG", "Tech", "Sports"
  val description: String,
  val faculty: String,
  val memberCount: Int,
  val isJoined: Boolean = false,
  val category: String,
  val leaderName: String = "President / Lead"
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
  @PrimaryKey
  val id: String,
  val title: String,
  val content: String,
  val authorName: String,
  val authorRole: String, // "NSUK Directorate of Information", "Faculty Dean", etc.
  val isVerified: Boolean = true,
  val dateDisplay: String,
  val timestamp: Long = System.currentTimeMillis(),
  val faculty: String? = null
)

@Entity(tableName = "tickets")
data class TicketEntity(
  @PrimaryKey
  val id: String,
  val eventId: String,
  val eventTitle: String,
  val eventDate: String,
  val eventTime: String,
  val eventVenue: String,
  val attendeeName: String,
  val matricNumber: String,
  val department: String,
  val phone: String,
  val ticketCode: String, // e.g. "NSUK-8924"
  val isCheckedIn: Boolean = false,
  val checkInTime: String? = null,
  val registeredAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
  @PrimaryKey
  val id: String = "current_user",
  val fullName: String = "Abdul Abdullahadi",
  val email: String = "lahadiademu7@gmail.com",
  val phone: String = "+234 812 345 6789",
  val matricNumber: String = "NSUK/CMP/2022/0481",
  val faculty: String = "Faculty of Computing",
  val department: String = "Computer Science",
  val level: String = "400 Level",
  val bio: String = "Tech enthusiast, aspiring software engineer & campus community builder at NSUK Keffi.",
  val role: String = "Student", // "Student", "Staff", "Organizer", "Admin"
  val followersCount: Int = 245,
  val followingCount: Int = 89
)

@Entity(tableName = "reports")
data class ReportEntity(
  @PrimaryKey
  val id: String,
  val targetType: String, // "Event", "Comment", "User"
  val targetId: String,
  val targetTitle: String,
  val reason: String,
  val details: String = "",
  val reporterName: String = "Student",
  val timestamp: Long = System.currentTimeMillis(),
  val isResolved: Boolean = false
)
