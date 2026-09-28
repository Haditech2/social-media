package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.CheckInResult
import com.example.ui.theme.CampusAmberHighlight
import com.example.ui.theme.CampusBluePrimary
import com.example.ui.theme.CampusCoral
import com.example.ui.theme.CampusTealAccent
import com.example.ui.viewmodel.CampusViewModel

@Composable
fun CheckInScannerScreen(
  viewModel: CampusViewModel,
  modifier: Modifier = Modifier
) {
  val checkInResult by viewModel.checkInResult.collectAsState()
  val tickets by viewModel.myTickets.collectAsState()
  var inputCode by remember { mutableStateOf("") }

  val totalRegistered = tickets.size
  val checkedInCount = tickets.count { it.isCheckedIn }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("checkin_scanner_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Header & Live Stats
    item {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.QrCodeScanner,
            contentDescription = null,
            tint = CampusBluePrimary,
            modifier = Modifier.size(28.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Organizer Check-In Terminal",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Validate digital attendee tickets & monitor live attendance",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Cards Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text("Total Registered", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
              Text("$totalRegistered", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
          }

          Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
            shape = RoundedCornerShape(16.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text("Checked In", fontSize = 12.sp, color = Color(0xFF166534))
              Text("$checkedInCount", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = CampusTealAccent)
            }
          }
        }
      }
    }

    // 2. Scanner / Code Input
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Enter or Scan Ticket Code",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "e.g. NSUK-8924 (or tap any demo ticket below to test)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
              value = inputCode,
              onValueChange = { inputCode = it.uppercase() },
              placeholder = { Text("NSUK-XXXX") },
              singleLine = true,
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("ticket_code_input")
            )

            Spacer(modifier = Modifier.width(10.dp))

            Button(
              onClick = {
                if (inputCode.isNotBlank()) {
                  viewModel.processCheckIn(inputCode)
                }
              },
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary),
              modifier = Modifier.height(52.dp)
            ) {
              Text("Verify", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // 3. Check-In Result Banner
    checkInResult?.let { result ->
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = when (result) {
              is CheckInResult.Success -> Color(0xFFDCFCE7)
              is CheckInResult.AlreadyCheckedIn -> Color(0xFFFEF3C7)
              is CheckInResult.NotFound -> Color(0xFFFEE2E2)
            }
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = when (result) {
                is CheckInResult.Success -> Icons.Default.CheckCircle
                is CheckInResult.AlreadyCheckedIn -> Icons.Default.Warning
                is CheckInResult.NotFound -> Icons.Default.Error
              },
              contentDescription = null,
              tint = when (result) {
                is CheckInResult.Success -> CampusTealAccent
                is CheckInResult.AlreadyCheckedIn -> CampusAmberHighlight
                is CheckInResult.NotFound -> CampusCoral
              },
              modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
              when (result) {
                is CheckInResult.Success -> {
                  Text("Check-In Successful! ✅", fontWeight = FontWeight.Bold, color = Color(0xFF14532D))
                  Text("${result.ticket.attendeeName} (${result.ticket.matricNumber})", fontSize = 13.sp, color = Color(0xFF14532D))
                  Text(result.ticket.eventTitle, fontSize = 12.sp, color = Color(0xFF166534))
                }
                is CheckInResult.AlreadyCheckedIn -> {
                  Text("Duplicate Check-In ⚠️", fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
                  Text("${result.ticket.attendeeName} was already checked in at ${result.time}", fontSize = 13.sp, color = Color(0xFF78350F))
                }
                is CheckInResult.NotFound -> {
                  Text("Ticket Not Found ❌", fontWeight = FontWeight.Bold, color = Color(0xFF7F1D1D))
                  Text("No registered ticket matches code '$inputCode'", fontSize = 13.sp, color = Color(0xFF7F1D1D))
                }
              }
            }

            IconButton(onClick = { viewModel.clearCheckInResult() }) {
              Icon(Icons.Default.Close, contentDescription = "Dismiss")
            }
          }
        }
      }
    }

    // 4. Attendee Roster List
    item {
      Text(
        text = "Registered Attendee List (${tickets.size})",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
    }

    items(tickets, key = { it.id }) { ticket ->
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .background(CampusBluePrimary.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = CampusBluePrimary)
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(ticket.attendeeName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("${ticket.matricNumber} • ${ticket.department}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Code: ${ticket.ticketCode}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = CampusBluePrimary)
          }

          if (ticket.isCheckedIn) {
            Box(
              modifier = Modifier
                .background(Color(0xFFDCFCE7), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text("Checked In", color = CampusTealAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
          } else {
            Button(
              onClick = {
                inputCode = ticket.ticketCode
                viewModel.processCheckIn(ticket.ticketCode)
              },
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary)
            ) {
              Text("Check In", fontSize = 11.sp)
            }
          }
        }
      }
    }
  }
}
