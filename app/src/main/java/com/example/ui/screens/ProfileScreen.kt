package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.TicketEntity
import com.example.data.model.UserProfileEntity
import com.example.location.LocationHelper
import com.example.ui.components.QrCodeView
import com.example.ui.components.TicketSuccessDialog
import com.example.ui.theme.CampusAmberHighlight
import com.example.ui.theme.CampusBluePrimary
import com.example.ui.theme.CampusTealAccent
import com.example.ui.viewmodel.CampusViewModel

@Composable
fun ProfileScreen(
  viewModel: CampusViewModel,
  onEventSelected: (String) -> Unit,
  onNavigateToCheckIn: () -> Unit,
  onNavigateToAdmin: () -> Unit,
  onNavigateToAuth: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val userProfile by viewModel.userProfile.collectAsState()
  val savedEvents by viewModel.savedEvents.collectAsState()
  val myTickets by viewModel.myTickets.collectAsState()
  val communities by viewModel.communities.collectAsState()
  val allEvents by viewModel.allEvents.collectAsState()
  val userLat by viewModel.userLatitude.collectAsState()
  val userLng by viewModel.userLongitude.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Tickets, 1: Saved, 2: Communities, 3: Created
  var showEditProfileDialog by remember { mutableStateOf(false) }
  var showRoleSwitcherDialog by remember { mutableStateOf(false) }
  var selectedTicketModal by remember { mutableStateOf<TicketEntity?>(null) }

  val joinedCommunities = communities.filter { it.isJoined }
  val createdEvents = allEvents.filter { it.organizerId == "current_user" || it.organizerId == "org_user" }

  selectedTicketModal?.let { ticket ->
    TicketSuccessDialog(
      ticket = ticket,
      onDismiss = { selectedTicketModal = null },
      onAddToCalendar = { viewModel.showMessage("Saved to device calendar!") }
    )
  }

  if (showEditProfileDialog) {
    var editName by remember { mutableStateOf(userProfile?.fullName ?: "") }
    var editBio by remember { mutableStateOf(userProfile?.bio ?: "") }
    var editFaculty by remember { mutableStateOf(userProfile?.faculty ?: "") }
    var editDept by remember { mutableStateOf(userProfile?.department ?: "") }
    var editLevel by remember { mutableStateOf(userProfile?.level ?: "") }

    AlertDialog(
      onDismissRequest = { showEditProfileDialog = false },
      title = { Text("Edit Student Profile") },
      text = {
        Column {
          OutlinedTextField(
            value = editName,
            onValueChange = { editName = it },
            label = { Text("Full Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = editBio,
            onValueChange = { editBio = it },
            label = { Text("Bio") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = editFaculty,
            onValueChange = { editFaculty = it },
            label = { Text("Faculty") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = editDept,
            onValueChange = { editDept = it },
            label = { Text("Department") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.updateProfile(editName, editBio, editFaculty, editDept, editLevel)
            showEditProfileDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary)
        ) {
          Text("Save Changes")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showEditProfileDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  if (showRoleSwitcherDialog) {
    val roles = listOf("Student", "Staff", "Organizer", "Admin")
    AlertDialog(
      onDismissRequest = { showRoleSwitcherDialog = false },
      title = { Text("Switch User Role Persona") },
      text = {
        Column {
          Text(
            text = "Experience CampusConnect as different user types:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(12.dp))
          roles.forEach { role ->
            val isCurrent = userProfile?.role == role
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (isCurrent) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                .clickable {
                  viewModel.switchUserRole(role)
                  showRoleSwitcherDialog = false
                }
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = role,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                fontSize = 15.sp,
                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
              )
              if (isCurrent) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CampusTealAccent)
              }
            }
          }
        }
      },
      confirmButton = {
        Button(onClick = { showRoleSwitcherDialog = false }) {
          Text("Close")
        }
      }
    )
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("profile_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Profile Header Card
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(80.dp)
              .clip(CircleShape)
              .background(CampusBluePrimary),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = userProfile?.fullName?.split(" ")?.mapNotNull { it.firstOrNull()?.toString() }?.take(2)?.joinToString("") ?: "AA",
              fontSize = 28.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = userProfile?.fullName ?: "Abdul Abdullahadi",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "${userProfile?.department} • ${userProfile?.level}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
          )

          Text(
            text = "${userProfile?.faculty} • NSUK Keffi",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Text(
            text = "Matric: ${userProfile?.matricNumber}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Role Badge with Persona Switcher trigger
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(MaterialTheme.colorScheme.primaryContainer)
              .clickable { showRoleSwitcherDialog = true }
              .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.SwitchAccount, contentDescription = null, tint = CampusBluePrimary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Role: ${userProfile?.role ?: "Student"}",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("(tap to change)", fontSize = 10.sp, color = CampusBluePrimary)
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = userProfile?.bio ?: "",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Followers & Following Stats
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("${userProfile?.followersCount ?: 245}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
              Text("Followers", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("${userProfile?.followingCount ?: 89}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
              Text("Following", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("${myTickets.size}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
              Text("Attending", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Edit Profile & Account Actions Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = { showEditProfileDialog = true },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Edit Profile", fontSize = 12.sp)
            }

            OutlinedButton(
              onClick = {
                viewModel.signOut()
                onNavigateToAuth()
              },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Default.SwitchAccount, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Switch User", fontSize = 12.sp)
            }
          }
        }
      }
    }

    // Role-specific shortcut banner for Organizer or Admin
    if (userProfile?.role == "Organizer" || userProfile?.role == "Staff") {
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = CampusTealAccent.copy(alpha = 0.15f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Organizer Attendance Scanner", fontWeight = FontWeight.Bold)
              Text("Scan QR passes & record event check-ins", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(
              onClick = onNavigateToCheckIn,
              colors = ButtonDefaults.buttonColors(containerColor = CampusTealAccent)
            ) {
              Text("Scanner", fontSize = 12.sp)
            }
          }
        }
      }
    }

    if (userProfile?.role == "Admin") {
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = CampusBluePrimary.copy(alpha = 0.15f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Campus Safety & Moderation Hub", fontWeight = FontWeight.Bold)
              Text("Review reports & publish verified university notices", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(
              onClick = onNavigateToAdmin,
              colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary)
            ) {
              Text("Admin Hub", fontSize = 12.sp)
            }
          }
        }
      }
    }

    // 2. Profile Tabs: Attending Tickets, Saved, Communities, Created
    item {
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier.clip(RoundedCornerShape(14.dp))
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("Tickets (${myTickets.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("Saved (${savedEvents.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = { Text("Clubs (${joinedCommunities.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        )
      }
    }

    // Tab 0: Tickets
    if (selectedTab == 0) {
      if (myTickets.isEmpty()) {
        item {
          Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
            Text("You haven't registered for any events yet.")
          }
        }
      } else {
        items(myTickets, key = { it.id }) { ticket ->
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { selectedTicketModal = ticket }
          ) {
            Row(
              modifier = Modifier.padding(16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              QrCodeView(code = ticket.ticketCode, sizeDp = 70)

              Spacer(modifier = Modifier.width(14.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(ticket.eventTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
                Spacer(modifier = Modifier.height(2.dp))
                Text("${ticket.eventDate} • ${ticket.eventTime}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                Text(ticket.eventVenue, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text("Code: ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  Text(ticket.ticketCode, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = CampusBluePrimary)
                }
              }

              if (ticket.isCheckedIn) {
                Box(
                  modifier = Modifier
                    .background(Color(0xFFDCFCE7), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text("Checked In", color = CampusTealAccent, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
              }
            }
          }
        }
      }
    }

    // Tab 1: Saved Events
    if (selectedTab == 1) {
      if (savedEvents.isEmpty()) {
        item {
          Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
            Text("No saved bookmarks yet. Tap the bookmark icon on any event!")
          }
        }
      } else {
        items(savedEvents, key = { it.id }) { event ->
          val distMeters = LocationHelper.calculateDistanceMeters(userLat, userLng, event.latitude, event.longitude)
          val distFormatted = LocationHelper.formatDistance(distMeters)
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onEventSelected(event.id) }
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(event.category, color = CampusBluePrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text(distFormatted, color = CampusAmberHighlight, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(event.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text("${event.startDateDisplay} • ${event.venue}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
        }
      }
    }

    // Tab 2: Joined Communities
    if (selectedTab == 2) {
      if (joinedCommunities.isEmpty()) {
        item {
          Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
            Text("You haven't joined any campus communities yet.")
          }
        }
      } else {
        items(joinedCommunities, key = { it.id }) { comm ->
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
                  .background(CampusBluePrimary, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Groups, contentDescription = null, tint = Color.White)
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(comm.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("${comm.memberCount} members • ${comm.faculty}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
          }
        }
      }
    }
  }
}
