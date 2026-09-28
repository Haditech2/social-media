package com.example.data.local

import com.example.data.model.AnnouncementEntity
import com.example.data.model.CommentEntity
import com.example.data.model.CommunityEntity
import com.example.data.model.EventEntity
import com.example.data.model.TicketEntity
import com.example.data.model.UserProfileEntity

object InitialData {
  val defaultProfile = UserProfileEntity(
    id = "current_user",
    fullName = "Abdul Abdullahadi",
    email = "lahadiademu7@gmail.com",
    phone = "+234 812 345 6789",
    matricNumber = "NSUK/CMP/2022/0481",
    faculty = "Faculty of Computing",
    department = "Computer Science",
    level = "400 Level",
    bio = "Tech enthusiast, aspiring software engineer & campus community builder at NSUK Keffi.",
    role = "Student",
    followersCount = 245,
    followingCount = 89
  )

  val events = listOf(
    EventEntity(
      id = "event_1",
      title = "NSUK Tech Innovation Summit 2026",
      description = "The premier university tech summit featuring keynote addresses from leading Nigerian tech founders, live robotics demonstrations, cloud computing workshops, and a $1,000 hackathon prize pool for student innovators.",
      category = "Technology",
      organizerName = "NACOS & Google Developer Student Club",
      organizerRole = "Department Association",
      organizerId = "org_nacos",
      faculty = "Faculty of Computing",
      department = "Computer Science",
      venue = "Faculty of Computing Lecture Theatre 1",
      room = "Auditorium A",
      latitude = 8.8480,
      longitude = 7.8780,
      startDateDisplay = "Tomorrow",
      timeDisplay = "10:00 AM - 3:30 PM",
      startDateEpochMs = System.currentTimeMillis() + 86400000L,
      bannerDrawable = "img_hackathon",
      isFree = true,
      maxAttendees = 450,
      interestedCount = 245,
      isLiked = true,
      isSaved = true,
      isRegistered = true,
      ticketCode = "NSUK-8924",
      isTrending = true,
      isFeatured = true
    ),
    EventEntity(
      id = "event_2",
      title = "Inter-Faculty Football Championship",
      description = "The grand clash between the Faculty of Computing and Faculty of Science in the annual NSUK Vice-Chancellor Cup. Free refreshments for attendees wearing their faculty jerseys!",
      category = "Sports",
      organizerName = "Directorate of Sports & SUG",
      organizerRole = "Student Union Government",
      organizerId = "org_sug",
      faculty = "University-Wide",
      department = "Sports Directorate",
      venue = "NSUK Main Sports Complex Stadium",
      room = "Pitch 1",
      latitude = 8.8510,
      longitude = 7.8810,
      startDateDisplay = "Friday",
      timeDisplay = "4:00 PM - 6:30 PM",
      startDateEpochMs = System.currentTimeMillis() + 172800000L,
      bannerDrawable = "img_sports",
      isFree = true,
      maxAttendees = 1200,
      interestedCount = 520,
      isLiked = false,
      isSaved = false,
      isRegistered = false,
      isTrending = true,
      isFeatured = true
    ),
    EventEntity(
      id = "event_3",
      title = "NSUK Grand Cultural Day & Carnival",
      description = "Celebrating the rich diversity and cultural heritage of Nasarawa State and Nigeria. Traditional dance troupes, indigenous delicacies, cultural attire exhibitions, and musical performances.",
      category = "Cultural",
      organizerName = "Indigenous Students Association",
      organizerRole = "Cultural Association",
      organizerId = "org_culture",
      faculty = "Faculty of Arts",
      department = "Theatre & Cultural Studies",
      venue = "Convocation Square & Open Grounds",
      room = "Main Pavilion",
      latitude = 8.8465,
      longitude = 7.8765,
      startDateDisplay = "Saturday",
      timeDisplay = "11:00 AM - 5:00 PM",
      startDateEpochMs = System.currentTimeMillis() + 259200000L,
      bannerDrawable = "img_cultural",
      isFree = true,
      maxAttendees = 800,
      interestedCount = 389,
      isLiked = true,
      isSaved = false,
      isRegistered = false,
      isTrending = true,
      isFeatured = false
    ),
    EventEntity(
      id = "event_4",
      title = "Freshers Orientation & Matriculation Welcome",
      description = "Official welcome ceremony for newly admitted undergraduate students of Nasarawa State University, Keffi. Meet faculty deans, department heads, student leaders, and campus clubs.",
      category = "Academic",
      organizerName = "NSUK Academic Affairs & Registry",
      organizerRole = "University Administration",
      organizerId = "org_admin",
      faculty = "University-Wide",
      department = "Student Affairs",
      venue = "Senate Building Main Auditorium",
      room = "Hall of Fame",
      latitude = 8.8472,
      longitude = 7.8768,
      startDateDisplay = "Monday, Next Week",
      timeDisplay = "9:00 AM - 1:00 PM",
      startDateEpochMs = System.currentTimeMillis() + 432000000L,
      bannerDrawable = "img_hero_campus",
      isFree = true,
      maxAttendees = 2000,
      interestedCount = 612,
      isLiked = false,
      isSaved = true,
      isRegistered = false,
      isTrending = false,
      isFeatured = true
    ),
    EventEntity(
      id = "event_5",
      title = "AI & Machine Learning Hands-on Workshop",
      description = "Learn how to build and deploy modern AI solutions using Gemini, Python, and TensorFlow. Bring your laptops. Certificates of completion will be awarded to all active participants.",
      category = "Workshop",
      organizerName = "NSUK AI & Robotics Society",
      organizerRole = "Student Society",
      organizerId = "org_ai",
      faculty = "Faculty of Computing",
      department = "Computer Science & Cyber Security",
      venue = "Computing Software Lab 3",
      room = "Floor 2, Room 204",
      latitude = 8.8482,
      longitude = 7.8784,
      startDateDisplay = "In 3 Days",
      timeDisplay = "2:00 PM - 5:00 PM",
      startDateEpochMs = System.currentTimeMillis() + 216000000L,
      bannerDrawable = "img_hackathon",
      isFree = true,
      maxAttendees = 150,
      interestedCount = 194,
      isLiked = false,
      isSaved = false,
      isRegistered = false,
      isTrending = false,
      isFeatured = false
    ),
    EventEntity(
      id = "event_6",
      title = "Campus Career & Internship Fair",
      description = "Connect directly with top employers, banking institutions, multinational tech companies, and NGOs actively recruiting NSUK students for graduate trainee roles and industrial attachment.",
      category = "Career",
      organizerName = "NSUK Alumni & Career Centre",
      organizerRole = "University Administration",
      organizerId = "org_admin",
      faculty = "Faculty of Administration",
      department = "Business Administration",
      venue = "Faculty of Administration Board Room & Grounds",
      room = "Exhibition Hall",
      latitude = 8.8485,
      longitude = 7.8760,
      startDateDisplay = "Next Thursday",
      timeDisplay = "10:00 AM - 4:00 PM",
      startDateEpochMs = System.currentTimeMillis() + 691200000L,
      bannerDrawable = "img_hero_campus",
      isFree = true,
      maxAttendees = 600,
      interestedCount = 310,
      isLiked = true,
      isSaved = true,
      isRegistered = false,
      isTrending = false,
      isFeatured = false
    )
  )

  val communities = listOf(
    CommunityEntity(
      id = "comm_1",
      name = "Nigeria Association of Computer Science Students (NACOS)",
      tag = "NACOS NSUK",
      description = "The official student body for Computer Science, Information Tech, Cyber Security, and Software Engineering scholars at NSUK Keffi.",
      faculty = "Faculty of Computing",
      memberCount = 1420,
      isJoined = true,
      category = "Technology"
    ),
    CommunityEntity(
      id = "comm_2",
      name = "NSUK Student Union Government (SUG)",
      tag = "SUG Press & Events",
      description = "The apex governing student union championing student welfare, campus events, sports, and university-wide dialogue.",
      faculty = "University-Wide",
      memberCount = 8900,
      isJoined = true,
      category = "Social"
    ),
    CommunityEntity(
      id = "comm_3",
      name = "Faculty of Science Innovators",
      tag = "Science Hub",
      description = "A thriving hub of Biochemistry, Microbiology, Physics, Chemistry, and Mathematics students advancing scientific research and exhibitions.",
      faculty = "Faculty of Science",
      memberCount = 980,
      isJoined = false,
      category = "Academic"
    ),
    CommunityEntity(
      id = "comm_4",
      name = "Campus Athletes & Football Club",
      tag = "NSUK Sports",
      description = "The central home for intramural leagues, track and field, basketball, and university tournament updates.",
      faculty = "Sports Directorate",
      memberCount = 2300,
      isJoined = false,
      category = "Sports"
    ),
    CommunityEntity(
      id = "comm_5",
      name = "Law Students Society (LSS NSUK)",
      tag = "LSS Moot Court",
      description = "Advancing legal advocacy, moot court championships, parliamentary debates, and professional networking.",
      faculty = "Faculty of Law",
      memberCount = 760,
      isJoined = false,
      category = "Academic"
    )
  )

  val announcements = listOf(
    AnnouncementEntity(
      id = "ann_1",
      title = "Official Welcome to the 2025/2026 Academic Session",
      content = "The Vice-Chancellor and Senate of Nasarawa State University, Keffi warmly welcome all returning and newly matriculating students. Course registrations and faculty clearance are now open on the university portal.",
      authorName = "Directorate of Information & Protocol",
      authorRole = "NSUK Central Administration",
      isVerified = true,
      dateDisplay = "Today, 8:30 AM",
      timestamp = System.currentTimeMillis() - 7200000L
    ),
    AnnouncementEntity(
      id = "ann_2",
      title = "Campus Security Advisory for Evening Events",
      content = "All departmental and student association events extending past 8:00 PM must obtain security clearance from the Chief Security Officer (CSO) at the Senate Annex.",
      authorName = "Campus Security Department",
      authorRole = "NSUK Security Division",
      isVerified = true,
      dateDisplay = "Yesterday",
      timestamp = System.currentTimeMillis() - 86400000L
    )
  )

  val comments = listOf(
    CommentEntity(
      id = "comm_evt_1",
      eventId = "event_1",
      userName = "Dr. B. K. Ibrahim",
      userRole = "Organizer",
      text = "Important Note: Please arrive by 9:45 AM for registration verification and to receive your summit badge and workshop materials.",
      timeAgo = "2 hours ago",
      timestamp = System.currentTimeMillis() - 7200000L,
      likes = 42,
      isLiked = true,
      isPinned = true
    ),
    CommentEntity(
      id = "comm_evt_2",
      eventId = "event_1",
      userName = "Fatima Usman",
      userRole = "Student",
      text = "Will there be certificates issued for the cloud computing hands-on breakout session?",
      timeAgo = "1 hour ago",
      timestamp = System.currentTimeMillis() - 3600000L,
      likes = 12,
      isLiked = false,
      isPinned = false
    ),
    CommentEntity(
      id = "comm_evt_3",
      eventId = "event_1",
      userName = "Michael Okon",
      userRole = "Organizer",
      text = "Yes Fatima! Digital verified certificates will be sent to all registered attendees through the app.",
      timeAgo = "30 mins ago",
      timestamp = System.currentTimeMillis() - 1800000L,
      likes = 19,
      isLiked = true,
      isPinned = false
    )
  )

  val initialTickets = listOf(
    TicketEntity(
      id = "tkt_1",
      eventId = "event_1",
      eventTitle = "NSUK Tech Innovation Summit 2026",
      eventDate = "Tomorrow • 10:00 AM",
      eventTime = "10:00 AM - 3:30 PM",
      eventVenue = "Faculty of Computing Lecture Theatre 1",
      attendeeName = "Abdul Abdullahadi",
      matricNumber = "NSUK/CMP/2022/0481",
      department = "Computer Science",
      phone = "+234 812 345 6789",
      ticketCode = "NSUK-8924",
      isCheckedIn = false
    )
  )
}
