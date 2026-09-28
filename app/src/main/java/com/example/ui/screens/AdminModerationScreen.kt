package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CampusAmberHighlight
import com.example.ui.theme.CampusBluePrimary
import com.example.ui.theme.CampusTealAccent
import com.example.ui.viewmodel.CampusViewModel

@Composable
fun AdminModerationScreen(
  viewModel: CampusViewModel,
  modifier: Modifier = Modifier
) {
  val reports by viewModel.reports.collectAsState()
  val allEvents by viewModel.allEvents.collectAsState()
  val communities by viewModel.communities.collectAsState()

  var annTitle by remember { mutableStateOf("") }
  var annContent by remember { mutableStateOf("") }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("admin_moderation_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Admin Header
    item {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.AdminPanelSettings,
          contentDescription = null,
          tint = CampusBluePrimary,
          modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "Campus Administration & Safety",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "NSUK Content Moderation & Official University Broadcasts",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // 2. Platform Metrics Overview
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Card(
          modifier = Modifier.weight(1f),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          shape = RoundedCornerShape(14.dp)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text("Active Events", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${allEvents.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
          }
        }
        Card(
          modifier = Modifier.weight(1f),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          shape = RoundedCornerShape(14.dp)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text("Communities", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${communities.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
          }
        }
        Card(
          modifier = Modifier.weight(1f),
          colors = CardDefaults.cardColors(containerColor = if (reports.isNotEmpty()) Color(0xFFFEE2E2) else MaterialTheme.colorScheme.surfaceVariant),
          shape = RoundedCornerShape(14.dp)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text("Open Reports", fontSize = 11.sp, color = if (reports.isNotEmpty()) Color(0xFF991B1B) else MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${reports.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (reports.isNotEmpty()) Color(0xFF991B1B) else MaterialTheme.colorScheme.onSurface)
          }
        }
      }
    }

    // 3. Post Official University Announcement
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Campaign, contentDescription = null, tint = CampusBluePrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Broadcast Official Announcement",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = annTitle,
            onValueChange = { annTitle = it },
            label = { Text("Announcement Headline") },
            placeholder = { Text("e.g. Schedule for 2026 Convocation") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = annContent,
            onValueChange = { annContent = it },
            label = { Text("Announcement Content") },
            placeholder = { Text("Details for all university students & staff...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
          )

          Spacer(modifier = Modifier.height(12.dp))

          Button(
            onClick = {
              if (annTitle.isNotBlank() && annContent.isNotBlank()) {
                viewModel.postAnnouncement(annTitle, annContent)
                annTitle = ""
                annContent = ""
              }
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary),
            modifier = Modifier.align(Alignment.End)
          ) {
            Text("Publish Verified Broadcast", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // 4. Reports & Flagged Content
    item {
      Text(
        text = "Reported Content Queue (${reports.size})",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
    }

    if (reports.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
            Text(
              text = "No open reports. Campus safety feed is clean! 🎉",
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 14.sp
            )
          }
        }
      }
    } else {
      items(reports, key = { it.id }) { report ->
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .background(Color(0xFFFEE2E2), RoundedCornerShape(6.dp))
                  .padding(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Text(
                  text = report.reason,
                  color = Color(0xFF991B1B),
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                )
              }
              Text("Target: ${report.targetType}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "Item: ${report.targetTitle}",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )

            if (report.details.isNotBlank()) {
              Text(
                text = "Note: ${report.details}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End,
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedButton(
                onClick = { viewModel.resolveReport(report.id, deleteTarget = false) },
                shape = RoundedCornerShape(8.dp)
              ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Keep & Dismiss", fontSize = 12.sp)
              }

              Spacer(modifier = Modifier.width(8.dp))

              Button(
                onClick = { viewModel.resolveReport(report.id, deleteTarget = true) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
              ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Remove Content", fontSize = 12.sp)
              }
            }
          }
        }
      }
    }
  }
}
