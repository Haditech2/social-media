package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.EventEntity
import com.example.data.model.TicketEntity
import com.example.data.model.UserProfileEntity
import com.example.location.LocationHelper
import com.example.ui.theme.CampusBluePrimary
import com.example.ui.theme.CampusTealAccent

@Composable
fun EventRegistrationDialog(
  event: EventEntity,
  userProfile: UserProfileEntity?,
  onDismiss: () -> Unit,
  onRegister: (name: String, email: String, matric: String, dept: String, phone: String) -> Unit
) {
  var name by remember { mutableStateOf(userProfile?.fullName ?: "") }
  var email by remember { mutableStateOf(userProfile?.email ?: "") }
  var matric by remember { mutableStateOf(userProfile?.matricNumber ?: "") }
  var dept by remember { mutableStateOf(userProfile?.department ?: "") }
  var phone by remember { mutableStateOf(userProfile?.phone ?: "") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("registration_dialog")
    ) {
      Column(
        modifier = Modifier.padding(24.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Event Registration",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Text(
          text = event.title,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Full Name") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = matric,
          onValueChange = { matric = it },
          label = { Text("Matric Number") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = dept,
          onValueChange = { dept = it },
          label = { Text("Department") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Phone Number") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            if (name.isNotBlank() && matric.isNotBlank()) {
              onRegister(name, email, matric, dept, phone)
              onDismiss()
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("submit_registration_button"),
          colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary)
        ) {
          Text(
            text = "Confirm Registration",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
        }
      }
    }
  }
}

@Composable
fun TicketSuccessDialog(
  ticket: TicketEntity,
  onDismiss: () -> Unit,
  onAddToCalendar: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("ticket_success_dialog")
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(56.dp)
            .background(Color(0xFFDCFCE7), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = CampusTealAccent,
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "You're Registered! 🎉",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )

        Text(
          text = "Present this digital ticket at check-in",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        QrCodeView(code = ticket.ticketCode, sizeDp = 160)

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = ticket.ticketCode,
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.ExtraBold,
          color = CampusBluePrimary,
          letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = ticket.eventTitle,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
          text = "${ticket.eventDate} • ${ticket.eventTime}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
          text = ticket.eventVenue,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = onAddToCalendar,
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Calendar", fontSize = 12.sp)
          }

          Button(
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary)
          ) {
            Text("Done")
          }
        }
      }
    }
  }
}

@Composable
fun ReportContentDialog(
  targetType: String,
  targetTitle: String,
  onDismiss: () -> Unit,
  onSubmit: (reason: String, details: String) -> Unit
) {
  val reasons = listOf(
    "Spam or misleading",
    "Harassment or bullying",
    "Hate speech",
    "Scam or fraudulent event",
    "False campus information",
    "Inappropriate content",
    "Other"
  )
  var selectedReason by remember { mutableStateOf(reasons.first()) }
  var details by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    icon = {
      Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
    },
    title = {
      Text("Report $targetType")
    },
    text = {
      Column {
        Text(
          text = "Help maintain a safe campus community. Why are you reporting '$targetTitle'?",
          style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        reasons.forEach { reason ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { selectedReason = reason }
              .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            RadioButton(
              selected = selectedReason == reason,
              onClick = { selectedReason = reason }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = reason, style = MaterialTheme.typography.bodyMedium)
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onSubmit(selectedReason, details)
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
      ) {
        Text("Submit Report")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

@Composable
fun ChangeCampusLocationDialog(
  currentLat: Double,
  currentLng: Double,
  onDismiss: () -> Unit,
  onSelectLandmark: (LocationHelper.CampusLandmark) -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    icon = {
      Icon(Icons.Default.LocationOn, contentDescription = null, tint = CampusBluePrimary)
    },
    title = {
      Text("Simulate Campus Position")
    },
    text = {
      Column {
        Text(
          text = "Select a spot at NSUK Keffi to test real-time distance discovery:",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(modifier = Modifier.height(260.dp)) {
          items(LocationHelper.landmarks) { landmark ->
            val isCurrent = landmark.latitude == currentLat && landmark.longitude == currentLng
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                  onSelectLandmark(landmark)
                  onDismiss()
                }
                .background(if (isCurrent) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                .padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = if (isCurrent) CampusBluePrimary else Color.Gray,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = landmark.name,
                  fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                  fontSize = 14.sp
                )
                Text(
                  text = landmark.description,
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(onClick = onDismiss) {
        Text("Done")
      }
    }
  )
}
