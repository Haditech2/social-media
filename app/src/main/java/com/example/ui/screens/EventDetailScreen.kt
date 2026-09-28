package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CommentEntity
import com.example.data.model.EventEntity
import com.example.location.LocationHelper
import com.example.ui.components.EventRegistrationDialog
import com.example.ui.components.ReportContentDialog
import com.example.ui.components.TicketSuccessDialog
import com.example.ui.components.getCategoryColor
import com.example.ui.components.getDrawableResByName
import com.example.ui.theme.CampusAmberHighlight
import com.example.ui.theme.CampusBluePrimary
import com.example.ui.theme.CampusCoral
import com.example.ui.theme.CampusTealAccent
import com.example.ui.viewmodel.CampusViewModel

@Composable
fun EventDetailScreen(
  eventId: String,
  viewModel: CampusViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val context = LocalContext.current
  val allEvents by viewModel.allEvents.collectAsState()
  val event = allEvents.find { it.id == eventId }
  val userProfile by viewModel.userProfile.collectAsState()
  val userLat by viewModel.userLatitude.collectAsState()
  val userLng by viewModel.userLongitude.collectAsState()
  val lastTicket by viewModel.lastIssuedTicket.collectAsState()

  // Comments for this event
  val commentsFlow = remember(eventId) { viewModel.getCommentsForEvent(eventId) }
  val comments: List<CommentEntity> by commentsFlow.collectAsState(initial = emptyList())

  var commentInput by remember { mutableStateOf("") }
  var showRegisterDialog by remember { mutableStateOf(false) }
  var showTicketDialog by remember { mutableStateOf(false) }
  var showReportDialog by remember { mutableStateOf(false) }

  if (event == null) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Event not found")
    }
    return
  }

  val distMeters = LocationHelper.calculateDistanceMeters(userLat, userLng, event.latitude, event.longitude)
  val distFormatted = LocationHelper.formatDistance(distMeters)
  val catColor = getCategoryColor(event.category)

  if (showRegisterDialog) {
    EventRegistrationDialog(
      event = event,
      userProfile = userProfile,
      onDismiss = { showRegisterDialog = false },
      onRegister = { name, email, matric, dept, phone ->
        viewModel.registerForEvent(
          eventId = event.id,
          name = name,
          email = email,
          matricNumber = matric,
          department = dept,
          phone = phone
        )
      }
    )
  }

  // Show ticket dialog when newly registered or user clicks "View Ticket"
  if (showTicketDialog && (lastTicket != null || event.ticketCode != null)) {
    val displayTicket = lastTicket ?: com.example.data.model.TicketEntity(
      id = "tkt_temp",
      eventId = event.id,
      eventTitle = event.title,
      eventDate = event.startDateDisplay,
      eventTime = event.timeDisplay,
      eventVenue = event.venue,
      attendeeName = userProfile?.fullName ?: "Student",
      matricNumber = userProfile?.matricNumber ?: "NSUK",
      department = userProfile?.department ?: "NSUK",
      phone = userProfile?.phone ?: "",
      ticketCode = event.ticketCode ?: "NSUK-0000"
    )
    TicketSuccessDialog(
      ticket = displayTicket,
      onDismiss = {
        showTicketDialog = false
        viewModel.clearLastIssuedTicket()
      },
      onAddToCalendar = {
        viewModel.showMessage("Added event reminder to device calendar!")
      }
    )
  }

  if (showReportDialog) {
    ReportContentDialog(
      targetType = "Event",
      targetTitle = event.title,
      onDismiss = { showReportDialog = false },
      onSubmit = { reason, details ->
        viewModel.submitReport("Event", event.id, event.title, reason, details)
      }
    )
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("event_detail_screen"),
    contentPadding = PaddingValues(bottom = 80.dp)
  ) {
    // 1. Top Poster with Back & Share buttons
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(240.dp)
      ) {
        Image(
          painter = painterResource(id = getDrawableResByName(event.bannerDrawable)),
          contentDescription = event.title,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )

        // Top Navigation buttons overlay
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onBack,
            modifier = Modifier
              .size(42.dp)
              .background(Color(0x99000000), CircleShape)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White
            )
          }

          Row {
            IconButton(
              onClick = {
                val sendIntent = Intent().apply {
                  action = Intent.ACTION_SEND
                  putExtra(
                    Intent.EXTRA_TEXT,
                    "Join me at '${event.title}' on CampusConnect! Venue: ${event.venue} (${event.startDateDisplay})."
                  )
                  type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Share Event via"))
              },
              modifier = Modifier
                .size(42.dp)
                .background(Color(0x99000000), CircleShape)
            ) {
              Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                tint = Color.White
              )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
              onClick = { viewModel.toggleSave(event.id) },
              modifier = Modifier
                .size(42.dp)
                .background(Color(0x99000000), CircleShape)
            ) {
              Icon(
                imageVector = if (event.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = "Save",
                tint = if (event.isSaved) CampusAmberHighlight else Color.White
              )
            }
          }
        }

        // Category & Distance Pill overlay at bottom of poster
        Row(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(16.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .background(catColor, RoundedCornerShape(12.dp))
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = event.category,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }

          Box(
            modifier = Modifier
              .background(Color(0xCC0F172A), RoundedCornerShape(12.dp))
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.NearMe,
                contentDescription = null,
                tint = CampusAmberHighlight,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = distFormatted,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }

    // 2. Title & Organizer
    item {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = event.title,
          style = MaterialTheme.typography.headlineLarge,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Organizer card
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .background(CampusBluePrimary, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = event.organizerName.take(2).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = event.organizerName,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = "Verified Organizer",
                  tint = CampusTealAccent,
                  modifier = Modifier.size(14.dp)
                )
              }
              Text(
                text = "${event.organizerRole} • ${event.faculty}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Date & Time Card
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.CalendarMonth,
              contentDescription = null,
              tint = CampusBluePrimary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
              Text(
                text = "${event.startDateDisplay} • ${event.timeDisplay}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "Add to your personal study schedule",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Location & Directions Card
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = CampusCoral,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(14.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = event.venue,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.SemiBold
                )
                if (event.room.isNotBlank()) {
                  Text(
                    text = "Room/Hall: ${event.room}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
              onClick = {
                viewModel.showMessage("Navigating to ${event.venue} on campus (${distFormatted})")
              },
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Get Campus Directions ($distFormatted)")
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Registration & Interested status
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Favorite,
              contentDescription = null,
              tint = CampusCoral,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "${event.interestedCount} students interested",
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.sp
            )
          }

          Text(
            text = if (event.isFree) "Free Admission" else "₦${event.price.toInt()}",
            fontWeight = FontWeight.Bold,
            color = CampusTealAccent,
            fontSize = 14.sp
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Registration / Ticket Primary CTA
        if (event.isRegistered || lastTicket != null) {
          Button(
            onClick = { showTicketDialog = true },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("view_ticket_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CampusTealAccent),
            shape = RoundedCornerShape(16.dp)
          ) {
            Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("View Digital QR Ticket", fontWeight = FontWeight.Bold, fontSize = 16.sp)
          }
        } else {
          Button(
            onClick = { showRegisterDialog = true },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("register_event_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary),
            shape = RoundedCornerShape(16.dp)
          ) {
            Text("Register for Event", fontWeight = FontWeight.Bold, fontSize = 16.sp)
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Event Description
        Text(
          text = "About this Event",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = event.description,
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Report Button
        TextButton(
          onClick = { showReportDialog = true },
          modifier = Modifier.align(Alignment.End)
        ) {
          Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Report Event", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        // Discussion Header
        Text(
          text = "Discussion & Questions (${comments.size})",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // 3. Discussion Comments List
    if (comments.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No questions or comments yet. Be the first to ask!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    } else {
      items(comments, key = { it.id }) { comment ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (comment.isPinned) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
          )
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = comment.userName,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .background(
                      if (comment.userRole == "Organizer") CampusBluePrimary else Color(0x3364748B),
                      RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = comment.userRole,
                    color = if (comment.userRole == "Organizer") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              if (comment.isPinned) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.PushPin,
                    contentDescription = "Pinned",
                    tint = CampusAmberHighlight,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Pinned",
                    fontSize = 11.sp,
                    color = CampusAmberHighlight,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = comment.text,
              style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = comment.timeAgo,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )

              Row(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { viewModel.toggleLikeComment(comment) }
                  .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = if (comment.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                  contentDescription = "Like",
                  tint = if (comment.isLiked) CampusCoral else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "${comment.likes}",
                  fontSize = 12.sp,
                  color = if (comment.isLiked) CampusCoral else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }

    // 4. Comment Input Field
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = commentInput,
          onValueChange = { commentInput = it },
          placeholder = { Text("Ask organizer or discuss...") },
          modifier = Modifier
            .weight(1f)
            .testTag("comment_input_field"),
          shape = RoundedCornerShape(20.dp),
          singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
          onClick = {
            if (commentInput.isNotBlank()) {
              viewModel.addComment(event.id, commentInput)
              commentInput = ""
            }
          },
          modifier = Modifier
            .size(48.dp)
            .background(CampusBluePrimary, CircleShape)
            .testTag("send_comment_button")
        ) {
          Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
        }
      }
    }
  }
}
