package com.example.data.repository

import com.example.data.auth.AuthService
import com.example.data.local.CampusDao
import com.example.data.local.InitialData
import com.example.data.model.AnnouncementEntity
import com.example.data.model.CommentEntity
import com.example.data.model.CommunityEntity
import com.example.data.model.EventEntity
import com.example.data.model.ReportEntity
import com.example.data.model.TicketEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

sealed class CheckInResult {
  data class Success(val ticket: TicketEntity) : CheckInResult()
  data class AlreadyCheckedIn(val ticket: TicketEntity, val time: String) : CheckInResult()
  data object NotFound : CheckInResult()
}

class CampusRepository(
  private val dao: CampusDao,
  val authService: AuthService? = null
) {

  val allEvents: Flow<List<EventEntity>> = dao.getAllEvents()
  val trendingEvents: Flow<List<EventEntity>> = dao.getTrendingEvents()
  val savedEvents: Flow<List<EventEntity>> = dao.getSavedEvents()
  val allCommunities: Flow<List<CommunityEntity>> = dao.getAllCommunities()
  val announcements: Flow<List<AnnouncementEntity>> = dao.getAnnouncements()
  val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
  val myTickets: Flow<List<TicketEntity>> = dao.getAllTickets()
  val allReports: Flow<List<ReportEntity>> = dao.getAllReports()

  suspend fun signIn(email: String, password: String): Result<UserProfileEntity> {
    return authService?.signIn(email, password)
      ?: Result.failure(Exception("Auth service not initialized"))
  }

  suspend fun signUp(
    fullName: String,
    email: String,
    password: String,
    matricNumber: String,
    faculty: String,
    department: String,
    phone: String,
    role: String
  ): Result<UserProfileEntity> {
    return authService?.signUp(
      fullName = fullName,
      email = email,
      password = password,
      matricNumber = matricNumber,
      faculty = faculty,
      department = department,
      phone = phone,
      role = role
    ) ?: Result.failure(Exception("Auth service not initialized"))
  }

  suspend fun signOut() {
    authService?.signOut()
  }

  suspend fun ensureDatabaseSeeded() {
    val existingProfile = dao.getUserProfileSync()
    if (existingProfile == null) {
      dao.insertEvents(InitialData.events)
      dao.insertCommunities(InitialData.communities)
      dao.insertAnnouncements(InitialData.announcements)
      dao.insertComments(InitialData.comments)
      dao.saveUserProfile(InitialData.defaultProfile)
      dao.insertTickets(InitialData.initialTickets)
    }
  }

  suspend fun getEventById(id: String): EventEntity? = dao.getEventById(id)

  fun observeEventById(id: String): Flow<EventEntity?> = dao.observeEventById(id)

  fun getCommentsForEvent(eventId: String): Flow<List<CommentEntity>> = dao.getCommentsForEvent(eventId)

  suspend fun toggleLike(eventId: String) {
    val event = dao.getEventById(eventId) ?: return
    val newLiked = !event.isLiked
    val newCount = if (newLiked) event.interestedCount + 1 else (event.interestedCount - 1).coerceAtLeast(0)
    dao.updateEvent(event.copy(isLiked = newLiked, interestedCount = newCount))
  }

  suspend fun toggleSave(eventId: String) {
    val event = dao.getEventById(eventId) ?: return
    dao.updateEvent(event.copy(isSaved = !event.isSaved))
  }

  suspend fun registerForEvent(
    eventId: String,
    userName: String,
    email: String,
    matricNumber: String,
    department: String,
    phone: String
  ): TicketEntity? {
    val event = dao.getEventById(eventId) ?: return null
    val ticketCode = "NSUK-${(1000..9999).random()}"
    val ticket = TicketEntity(
      id = UUID.randomUUID().toString(),
      eventId = event.id,
      eventTitle = event.title,
      eventDate = event.startDateDisplay,
      eventTime = event.timeDisplay,
      eventVenue = event.venue,
      attendeeName = userName,
      matricNumber = matricNumber,
      department = department,
      phone = phone,
      ticketCode = ticketCode,
      isCheckedIn = false
    )
    dao.insertTicket(ticket)
    dao.updateEvent(
      event.copy(
        isRegistered = true,
        ticketCode = ticketCode,
        interestedCount = event.interestedCount + 1
      )
    )
    return ticket
  }

  suspend fun addComment(
    eventId: String,
    text: String,
    userName: String,
    userRole: String
  ) {
    val comment = CommentEntity(
      id = UUID.randomUUID().toString(),
      eventId = eventId,
      userName = userName,
      userRole = userRole,
      text = text,
      timeAgo = "Just now",
      timestamp = System.currentTimeMillis(),
      likes = 0,
      isLiked = false,
      isPinned = false
    )
    dao.insertComment(comment)
  }

  suspend fun toggleLikeComment(comment: CommentEntity) {
    val newLiked = !comment.isLiked
    val newLikes = if (newLiked) comment.likes + 1 else (comment.likes - 1).coerceAtLeast(0)
    dao.updateComment(comment.copy(isLiked = newLiked, likes = newLikes))
  }

  suspend fun togglePinComment(comment: CommentEntity) {
    dao.updateComment(comment.copy(isPinned = !comment.isPinned))
  }

  suspend fun createEvent(event: EventEntity) {
    dao.insertEvent(event)
  }

  suspend fun deleteEvent(eventId: String) {
    dao.deleteEvent(eventId)
  }

  suspend fun toggleJoinCommunity(community: CommunityEntity) {
    val newJoined = !community.isJoined
    val newCount = if (newJoined) community.memberCount + 1 else (community.memberCount - 1).coerceAtLeast(0)
    dao.updateCommunity(community.copy(isJoined = newJoined, memberCount = newCount))
  }

  suspend fun checkInTicket(ticketCode: String): CheckInResult {
    val cleanCode = ticketCode.trim().uppercase()
    val ticket = dao.getTicketByCode(cleanCode) ?: return CheckInResult.NotFound
    if (ticket.isCheckedIn) {
      return CheckInResult.AlreadyCheckedIn(ticket, ticket.checkInTime ?: "Earlier")
    }
    val updated = ticket.copy(
      isCheckedIn = true,
      checkInTime = "Today, " + java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
    )
    dao.updateTicket(updated)
    return CheckInResult.Success(updated)
  }

  suspend fun updateProfile(profile: UserProfileEntity) {
    dao.saveUserProfile(profile)
  }

  suspend fun submitReport(
    targetType: String,
    targetId: String,
    targetTitle: String,
    reason: String,
    details: String
  ) {
    val report = ReportEntity(
      id = UUID.randomUUID().toString(),
      targetType = targetType,
      targetId = targetId,
      targetTitle = targetTitle,
      reason = reason,
      details = details,
      reporterName = "Student"
    )
    dao.insertReport(report)
  }

  suspend fun postAnnouncement(
    title: String,
    content: String,
    authorName: String,
    authorRole: String,
    faculty: String? = null
  ) {
    val ann = AnnouncementEntity(
      id = UUID.randomUUID().toString(),
      title = title,
      content = content,
      authorName = authorName,
      authorRole = authorRole,
      isVerified = true,
      dateDisplay = "Just now",
      timestamp = System.currentTimeMillis(),
      faculty = faculty
    )
    dao.insertAnnouncement(ann)
  }

  suspend fun resolveReport(reportId: String, deleteTarget: Boolean) {
    dao.deleteReport(reportId)
  }
}
