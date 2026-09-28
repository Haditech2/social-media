package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CampusDatabase
import com.example.data.local.InitialData
import com.example.data.model.AnnouncementEntity
import com.example.data.model.CommentEntity
import com.example.data.model.CommunityEntity
import com.example.data.model.EventEntity
import com.example.data.model.ReportEntity
import com.example.data.model.TicketEntity
import com.example.data.model.UserProfileEntity
import com.example.data.repository.CampusRepository
import com.example.data.repository.CheckInResult
import com.example.location.LocationHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class DistanceFilter(val label: String, val maxMeters: Double) {
  WITHIN_500M("500m", 500.0),
  WITHIN_1KM("1 km", 1000.0),
  WITHIN_5KM("5 km", 5000.0),
  WITHIN_10KM("10 km", 10000.0),
  CAMPUS_WIDE("Anywhere", Double.MAX_VALUE)
}

enum class SortOption(val label: String) {
  DISTANCE("Nearest"),
  POPULARITY("Trending"),
  DATE("Date")
}

data class EventWithDistance(
  val event: EventEntity,
  val distanceMeters: Double,
  val distanceFormatted: String
)

class CampusViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: CampusRepository

  init {
    val db = CampusDatabase.getDatabase(application)
    val authService = com.example.data.auth.AuthService(application, db.campusDao())
    repository = CampusRepository(db.campusDao(), authService)
    viewModelScope.launch {
      repository.ensureDatabaseSeeded()
    }
  }

  // Authentication State
  val isAuthLoading = MutableStateFlow(false)
  val authErrorMessage = MutableStateFlow<String?>(null)

  fun signIn(
    email: String,
    password: String,
    onSuccess: (UserProfileEntity) -> Unit
  ) {
    viewModelScope.launch {
      isAuthLoading.value = true
      authErrorMessage.value = null
      val result = repository.signIn(email, password)
      isAuthLoading.value = false
      result.onSuccess { profile ->
        _snackbarMessage.value = "Welcome back, ${profile.fullName} (${profile.role})!"
        onSuccess(profile)
      }.onFailure { error ->
        val msg = error.message ?: "Authentication failed"
        authErrorMessage.value = msg
        _snackbarMessage.value = msg
      }
    }
  }

  fun signUp(
    fullName: String,
    email: String,
    password: String,
    matricNumber: String,
    faculty: String,
    department: String,
    phone: String,
    role: String,
    onSuccess: (UserProfileEntity) -> Unit
  ) {
    viewModelScope.launch {
      isAuthLoading.value = true
      authErrorMessage.value = null
      val result = repository.signUp(
        fullName = fullName,
        email = email,
        password = password,
        matricNumber = matricNumber,
        faculty = faculty,
        department = department,
        phone = phone,
        role = role
      )
      isAuthLoading.value = false
      result.onSuccess { profile ->
        _snackbarMessage.value = "Account created successfully for ${profile.fullName}!"
        onSuccess(profile)
      }.onFailure { error ->
        val msg = error.message ?: "Registration failed"
        authErrorMessage.value = msg
        _snackbarMessage.value = msg
      }
    }
  }

  fun signOut() {
    viewModelScope.launch {
      repository.signOut()
      _snackbarMessage.value = "Signed out"
    }
  }

  // User location state (lat, lng) - default to NSUK campus center
  val userLatitude = MutableStateFlow(LocationHelper.NSUK_CAMPUS_LAT)
  val userLongitude = MutableStateFlow(LocationHelper.NSUK_CAMPUS_LNG)
  val currentLocationName = MutableStateFlow("NSUK Keffi Main Campus")

  // Search & Filter state
  val searchQuery = MutableStateFlow("")
  val selectedCategory = MutableStateFlow("All")
  val selectedDistanceFilter = MutableStateFlow(DistanceFilter.CAMPUS_WIDE)
  val selectedFacultyFilter = MutableStateFlow("All Faculties")
  val selectedSortOption = MutableStateFlow(SortOption.DISTANCE)

  // Message / Notification Toast
  private val _snackbarMessage = MutableStateFlow<String?>(null)
  val snackbarMessage = _snackbarMessage.asStateFlow()

  // Selected event for detail view
  private val _selectedEventId = MutableStateFlow<String?>("event_1")
  val selectedEventId = _selectedEventId.asStateFlow()

  // Last registered ticket to show modal
  private val _lastIssuedTicket = MutableStateFlow<TicketEntity?>(null)
  val lastIssuedTicket = _lastIssuedTicket.asStateFlow()

  // Base Data Flows from Repo
  val allEvents: StateFlow<List<EventEntity>> = repository.allEvents
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val trendingEvents: StateFlow<List<EventEntity>> = repository.trendingEvents
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val savedEvents: StateFlow<List<EventEntity>> = repository.savedEvents
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val communities: StateFlow<List<CommunityEntity>> = repository.allCommunities
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val announcements: StateFlow<List<AnnouncementEntity>> = repository.announcements
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val myTickets: StateFlow<List<TicketEntity>> = repository.myTickets
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val reports: StateFlow<List<ReportEntity>> = repository.allReports
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Computed: Events with distance & filtered
  val filteredEventsWithDistance: StateFlow<List<EventWithDistance>> = combine(
    allEvents,
    userLatitude,
    userLongitude,
    searchQuery,
    selectedCategory,
    selectedDistanceFilter,
    selectedFacultyFilter,
    selectedSortOption
  ) { args ->
    @Suppress("UNCHECKED_CAST")
    val events = args[0] as List<EventEntity>
    val lat = args[1] as Double
    val lon = args[2] as Double
    val query = (args[3] as String).trim().lowercase()
    val category = args[4] as String
    val distanceFilter = args[5] as DistanceFilter
    val faculty = args[6] as String
    val sort = args[7] as SortOption

    events.map { event ->
      val dist = LocationHelper.calculateDistanceMeters(lat, lon, event.latitude, event.longitude)
      EventWithDistance(
        event = event,
        distanceMeters = dist,
        distanceFormatted = LocationHelper.formatDistance(dist)
      )
    }.filter { item ->
      val matchQuery = query.isEmpty() ||
          item.event.title.lowercase().contains(query) ||
          item.event.description.lowercase().contains(query) ||
          item.event.venue.lowercase().contains(query) ||
          item.event.organizerName.lowercase().contains(query)

      val matchCategory = category == "All" || item.event.category.equals(category, ignoreCase = true)
      val matchDistance = item.distanceMeters <= distanceFilter.maxMeters
      val matchFaculty = faculty == "All Faculties" || item.event.faculty.equals(faculty, ignoreCase = true)

      matchQuery && matchCategory && matchDistance && matchFaculty
    }.let { list ->
      when (sort) {
        SortOption.DISTANCE -> list.sortedBy { it.distanceMeters }
        SortOption.POPULARITY -> list.sortedByDescending { it.event.interestedCount }
        SortOption.DATE -> list.sortedBy { it.event.startDateEpochMs }
      }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun selectEvent(id: String) {
    _selectedEventId.value = id
  }

  fun getCommentsForEvent(eventId: String): Flow<List<CommentEntity>> = repository.getCommentsForEvent(eventId)

  fun clearSnackbar() {
    _snackbarMessage.value = null
  }

  fun showMessage(msg: String) {
    _snackbarMessage.value = msg
  }

  fun clearLastIssuedTicket() {
    _lastIssuedTicket.value = null
  }

  fun toggleLike(eventId: String) {
    viewModelScope.launch {
      repository.toggleLike(eventId)
    }
  }

  fun toggleSave(eventId: String) {
    viewModelScope.launch {
      repository.toggleSave(eventId)
      val event = repository.getEventById(eventId)
      if (event?.isSaved == true) {
        _snackbarMessage.value = "Saved to your bookmarks"
      } else {
        _snackbarMessage.value = "Removed from bookmarks"
      }
    }
  }

  fun registerForEvent(
    eventId: String,
    name: String,
    email: String,
    matricNumber: String,
    department: String,
    phone: String
  ) {
    viewModelScope.launch {
      val ticket = repository.registerForEvent(
        eventId = eventId,
        userName = name,
        email = email,
        matricNumber = matricNumber,
        department = department,
        phone = phone
      )
      if (ticket != null) {
        _lastIssuedTicket.value = ticket
        _snackbarMessage.value = "Registered! Your QR Ticket is ready."
      }
    }
  }

  fun addComment(eventId: String, text: String) {
    if (text.isBlank()) return
    viewModelScope.launch {
      val profile = userProfile.value
      repository.addComment(
        eventId = eventId,
        text = text,
        userName = profile?.fullName ?: "Student",
        userRole = profile?.role ?: "Student"
      )
      _snackbarMessage.value = "Comment posted"
    }
  }

  fun toggleLikeComment(comment: CommentEntity) {
    viewModelScope.launch {
      repository.toggleLikeComment(comment)
    }
  }

  fun togglePinComment(comment: CommentEntity) {
    viewModelScope.launch {
      repository.togglePinComment(comment)
    }
  }

  fun toggleJoinCommunity(community: CommunityEntity) {
    viewModelScope.launch {
      repository.toggleJoinCommunity(community)
      if (!community.isJoined) {
        _snackbarMessage.value = "Joined ${community.name}"
      } else {
        _snackbarMessage.value = "Left ${community.name}"
      }
    }
  }

  fun setLocationToLandmark(landmark: LocationHelper.CampusLandmark) {
    userLatitude.value = landmark.latitude
    userLongitude.value = landmark.longitude
    currentLocationName.value = landmark.name
    _snackbarMessage.value = "Location set to ${landmark.name}"
  }

  fun createEvent(
    title: String,
    description: String,
    category: String,
    faculty: String,
    department: String,
    venue: String,
    room: String,
    latitude: Double,
    longitude: Double,
    dateDisplay: String,
    timeDisplay: String,
    isFree: Boolean,
    maxAttendees: Int,
    banner: String
  ) {
    viewModelScope.launch {
      val profile = userProfile.value
      val event = EventEntity(
        id = UUID.randomUUID().toString(),
        title = title,
        description = description,
        category = category,
        organizerName = profile?.fullName ?: "NSUK Organizer",
        organizerRole = if (profile?.role == "Staff") "Staff Member" else "Campus Organizer",
        organizerId = profile?.id ?: "org_user",
        faculty = faculty,
        department = department,
        venue = venue,
        room = room,
        latitude = latitude,
        longitude = longitude,
        startDateDisplay = dateDisplay,
        timeDisplay = timeDisplay,
        startDateEpochMs = System.currentTimeMillis() + 86400000L,
        bannerDrawable = banner,
        isFree = isFree,
        maxAttendees = maxAttendees,
        interestedCount = 1,
        isTrending = true
      )
      repository.createEvent(event)
      _snackbarMessage.value = "Event '$title' published to campus!"
    }
  }

  // Check-In scanner operation
  private val _checkInResult = MutableStateFlow<CheckInResult?>(null)
  val checkInResult = _checkInResult.asStateFlow()

  fun processCheckIn(ticketCode: String) {
    viewModelScope.launch {
      val result = repository.checkInTicket(ticketCode)
      _checkInResult.value = result
    }
  }

  fun clearCheckInResult() {
    _checkInResult.value = null
  }

  // Switch role between Student, Staff, Organizer, Admin
  fun switchUserRole(newRole: String) {
    viewModelScope.launch {
      val current = userProfile.value ?: InitialData.defaultProfile
      val updated = current.copy(role = newRole)
      repository.updateProfile(updated)
      _snackbarMessage.value = "Switched active view to: $newRole"
    }
  }

  fun updateProfile(fullName: String, bio: String, faculty: String, department: String, level: String) {
    viewModelScope.launch {
      val current = userProfile.value ?: InitialData.defaultProfile
      val updated = current.copy(
        fullName = fullName,
        bio = bio,
        faculty = faculty,
        department = department,
        level = level
      )
      repository.updateProfile(updated)
      _snackbarMessage.value = "Profile updated"
    }
  }

  fun submitReport(targetType: String, targetId: String, title: String, reason: String, details: String) {
    viewModelScope.launch {
      repository.submitReport(targetType, targetId, title, reason, details)
      _snackbarMessage.value = "Report submitted. Campus moderators will review."
    }
  }

  fun resolveReport(reportId: String, deleteTarget: Boolean) {
    viewModelScope.launch {
      repository.resolveReport(reportId, deleteTarget)
      _snackbarMessage.value = "Report resolved."
    }
  }

  fun postAnnouncement(title: String, content: String, faculty: String? = null) {
    viewModelScope.launch {
      val profile = userProfile.value
      repository.postAnnouncement(
        title = title,
        content = content,
        authorName = profile?.fullName ?: "University Admin",
        authorRole = "Verified Official Notice",
        faculty = faculty
      )
      _snackbarMessage.value = "Announcement posted to entire campus!"
    }
  }
}
