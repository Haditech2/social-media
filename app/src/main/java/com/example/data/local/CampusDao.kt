package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AnnouncementEntity
import com.example.data.model.CommentEntity
import com.example.data.model.CommunityEntity
import com.example.data.model.EventEntity
import com.example.data.model.ReportEntity
import com.example.data.model.TicketEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CampusDao {
  // Events
  @Query("SELECT * FROM events ORDER BY startDateEpochMs ASC")
  fun getAllEvents(): Flow<List<EventEntity>>

  @Query("SELECT * FROM events WHERE id = :id")
  suspend fun getEventById(id: String): EventEntity?

  @Query("SELECT * FROM events WHERE id = :id")
  fun observeEventById(id: String): Flow<EventEntity?>

  @Query("SELECT * FROM events WHERE isTrending = 1 ORDER BY interestedCount DESC")
  fun getTrendingEvents(): Flow<List<EventEntity>>

  @Query("SELECT * FROM events WHERE isSaved = 1")
  fun getSavedEvents(): Flow<List<EventEntity>>

  @Query("SELECT * FROM events WHERE organizerId = :organizerId")
  fun getEventsByOrganizer(organizerId: String): Flow<List<EventEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEvent(event: EventEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEvents(events: List<EventEntity>)

  @Update
  suspend fun updateEvent(event: EventEntity)

  @Query("DELETE FROM events WHERE id = :id")
  suspend fun deleteEvent(id: String)

  // Comments
  @Query("SELECT * FROM comments WHERE eventId = :eventId ORDER BY isPinned DESC, timestamp ASC")
  fun getCommentsForEvent(eventId: String): Flow<List<CommentEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertComment(comment: CommentEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertComments(comments: List<CommentEntity>)

  @Update
  suspend fun updateComment(comment: CommentEntity)

  @Query("DELETE FROM comments WHERE id = :id")
  suspend fun deleteComment(id: String)

  // Communities
  @Query("SELECT * FROM communities ORDER BY memberCount DESC")
  fun getAllCommunities(): Flow<List<CommunityEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCommunities(communities: List<CommunityEntity>)

  @Update
  suspend fun updateCommunity(community: CommunityEntity)

  // Announcements
  @Query("SELECT * FROM announcements ORDER BY timestamp DESC")
  fun getAnnouncements(): Flow<List<AnnouncementEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAnnouncement(announcement: AnnouncementEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAnnouncements(announcements: List<AnnouncementEntity>)

  @Query("DELETE FROM announcements WHERE id = :id")
  suspend fun deleteAnnouncement(id: String)

  // Tickets & Check-in
  @Query("SELECT * FROM tickets ORDER BY registeredAt DESC")
  fun getAllTickets(): Flow<List<TicketEntity>>

  @Query("SELECT * FROM tickets WHERE eventId = :eventId")
  fun getTicketsForEvent(eventId: String): Flow<List<TicketEntity>>

  @Query("SELECT * FROM tickets WHERE ticketCode = :code LIMIT 1")
  suspend fun getTicketByCode(code: String): TicketEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTicket(ticket: TicketEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTickets(tickets: List<TicketEntity>)

  @Update
  suspend fun updateTicket(ticket: TicketEntity)

  // User Profile
  @Query("SELECT * FROM user_profile WHERE id = 'current_user' LIMIT 1")
  fun getUserProfile(): Flow<UserProfileEntity?>

  @Query("SELECT * FROM user_profile WHERE id = 'current_user' LIMIT 1")
  suspend fun getUserProfileSync(): UserProfileEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveUserProfile(profile: UserProfileEntity)

  // Moderation & Reports
  @Query("SELECT * FROM reports ORDER BY timestamp DESC")
  fun getAllReports(): Flow<List<ReportEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertReport(report: ReportEntity)

  @Update
  suspend fun updateReport(report: ReportEntity)

  @Query("DELETE FROM reports WHERE id = :id")
  suspend fun deleteReport(id: String)
}
