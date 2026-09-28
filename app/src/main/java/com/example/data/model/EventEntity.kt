package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
  @PrimaryKey
  val id: String,
  val title: String,
  val description: String,
  val category: String, // Academic, Technology, Sports, Social, Cultural, Religious, Career, Workshop
  val organizerName: String,
  val organizerRole: String, // e.g., "NACOS NSUK", "Faculty of Computing", "Student Union Government"
  val organizerId: String = "org_nsuk",
  val faculty: String, // e.g., "Faculty of Computing"
  val department: String = "",
  val venue: String, // e.g., "Faculty of Computing Lecture Theatre 1"
  val room: String = "",
  val latitude: Double, // NSUK campus coords
  val longitude: Double,
  val startDateDisplay: String, // e.g., "Tomorrow", "12 Oct 2026"
  val timeDisplay: String, // e.g., "10:00 AM - 1:00 PM"
  val startDateEpochMs: Long,
  val bannerDrawable: String = "img_hackathon", // img_hackathon, img_sports, img_cultural, img_hero_campus
  val isFree: Boolean = true,
  val price: Double = 0.0,
  val maxAttendees: Int = 300,
  val interestedCount: Int = 120,
  val isLiked: Boolean = false,
  val isSaved: Boolean = false,
  val isRegistered: Boolean = false,
  val ticketCode: String? = null,
  val isTrending: Boolean = false,
  val isFeatured: Boolean = false,
  val allowComments: Boolean = true,
  val allowSharing: Boolean = true
)
