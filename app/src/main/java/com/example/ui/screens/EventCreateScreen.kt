package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.location.LocationHelper
import com.example.ui.components.getDrawableResByName
import com.example.ui.theme.CampusAmberHighlight
import com.example.ui.theme.CampusBluePrimary
import com.example.ui.theme.CampusTealAccent
import com.example.ui.viewmodel.CampusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCreateScreen(
  viewModel: CampusViewModel,
  onEventCreated: () -> Unit,
  modifier: Modifier = Modifier
) {
  var currentStep by remember { mutableIntStateOf(1) } // 1 to 5
  val scrollState = rememberScrollState()

  // Step 1: Info
  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Technology") }
  var bannerDrawable by remember { mutableStateOf("img_hackathon") }

  // Step 2: Date & Time
  var dateDisplay by remember { mutableStateOf("Next Friday") }
  var timeDisplay by remember { mutableStateOf("10:00 AM - 1:00 PM") }

  // Step 3: Location
  var venue by remember { mutableStateOf("Faculty of Computing Lecture Theatre 1") }
  var room by remember { mutableStateOf("Hall A") }
  var selectedLandmarkIndex by remember { mutableIntStateOf(0) }
  var faculty by remember { mutableStateOf("Faculty of Computing") }
  var department by remember { mutableStateOf("Computer Science") }

  // Step 4: Settings
  var isPublic by remember { mutableStateOf(true) }
  var isFree by remember { mutableStateOf(true) }
  var maxAttendees by remember { mutableStateOf("300") }
  var registrationRequired by remember { mutableStateOf(true) }

  val categories = listOf("Technology", "Academic", "Sports", "Social", "Cultural", "Workshop", "Career", "Religious")
  val posterOptions = listOf("img_hackathon", "img_sports", "img_cultural", "img_hero_campus")

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(scrollState)
      .padding(16.dp)
      .testTag("create_event_screen")
  ) {
    Text(
      text = "Create Campus Event",
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Step Indicator Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      for (step in 1..5) {
        val isCompleted = step < currentStep
        val isCurrent = step == currentStep

        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(
              when {
                isCompleted -> CampusTealAccent
                isCurrent -> CampusBluePrimary
                else -> MaterialTheme.colorScheme.surfaceVariant
              }
            ),
          contentAlignment = Alignment.Center
        ) {
          if (isCompleted) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
          } else {
            Text(
              text = "$step",
              color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // STEP 1: EVENT INFORMATION
    if (currentStep == 1) {
      Text(
        text = "Step 1: Event Information",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        label = { Text("Event Title *") },
        placeholder = { Text("e.g. NSUK Robotics & AI Exhibition") },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("create_title_input"),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("Description *") },
        placeholder = { Text("Describe event agenda, speakers, objectives...") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 3
      )

      Spacer(modifier = Modifier.height(14.dp))

      Text("Select Category", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        categories.take(4).forEach { cat ->
          FilterChip(
            selected = category == cat,
            onClick = { category = cat },
            label = { Text(cat, fontSize = 11.sp) }
          )
        }
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        categories.drop(4).forEach { cat ->
          FilterChip(
            selected = category == cat,
            onClick = { category = cat },
            label = { Text(cat, fontSize = 11.sp) }
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text("Choose Event Poster Poster", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        posterOptions.forEach { posterKey ->
          val isSelected = bannerDrawable == posterKey
          Box(
            modifier = Modifier
              .weight(1f)
              .height(60.dp)
              .clip(RoundedCornerShape(8.dp))
              .border(if (isSelected) 3.dp else 1.dp, if (isSelected) CampusBluePrimary else Color.Gray, RoundedCornerShape(8.dp))
              .clickable { bannerDrawable = posterKey }
          ) {
            Image(
              painter = painterResource(id = getDrawableResByName(posterKey)),
              contentDescription = null,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }
        }
      }
    }

    // STEP 2: DATE & TIME
    if (currentStep == 2) {
      Text(
        text = "Step 2: Date & Time",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(14.dp))

      OutlinedTextField(
        value = dateDisplay,
        onValueChange = { dateDisplay = it },
        label = { Text("Event Date") },
        placeholder = { Text("e.g. Next Saturday, 18 Oct 2026") },
        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = timeDisplay,
        onValueChange = { timeDisplay = it },
        label = { Text("Event Time Duration") },
        placeholder = { Text("e.g. 10:00 AM - 2:00 PM") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )
    }

    // STEP 3: LOCATION & COORDINATES
    if (currentStep == 3) {
      Text(
        text = "Step 3: Campus Location",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "Select an NSUK Campus Landmark:",
        style = MaterialTheme.typography.labelMedium
      )
      Spacer(modifier = Modifier.height(6.dp))

      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        LocationHelper.landmarks.forEachIndexed { index, landmark ->
          val isSelected = selectedLandmarkIndex == index
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                selectedLandmarkIndex = index
                venue = landmark.name
              },
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
            )
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = if (isSelected) CampusBluePrimary else Color.Gray,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(text = landmark.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = landmark.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = venue,
        onValueChange = { venue = it },
        label = { Text("Venue / Building Name") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(8.dp))

      OutlinedTextField(
        value = room,
        onValueChange = { room = it },
        label = { Text("Hall / Room Number") },
        placeholder = { Text("e.g. Lab 2, Hall of Fame") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )
    }

    // STEP 4: SETTINGS & POLICIES
    if (currentStep == 4) {
      Text(
        text = "Step 4: Event Settings",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(16.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("Public Campus Event", fontWeight = FontWeight.Bold)
          Text("Visible to all NSUK students & staff", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = isPublic, onCheckedChange = { isPublic = it })
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("Free Admission", fontWeight = FontWeight.Bold)
          Text("No ticket fee required for entrance", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = isFree, onCheckedChange = { isFree = it })
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("Registration Required", fontWeight = FontWeight.Bold)
          Text("Issue digital QR pass to attendees", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = registrationRequired, onCheckedChange = { registrationRequired = it })
      }

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = maxAttendees,
        onValueChange = { maxAttendees = it },
        label = { Text("Maximum Attendee Capacity") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )
    }

    // STEP 5: PREVIEW & PUBLISH
    if (currentStep == 5) {
      Text(
        text = "Step 5: Preview & Publish",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(14.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Column {
          Image(
            painter = painterResource(id = getDrawableResByName(bannerDrawable)),
            contentDescription = null,
            modifier = Modifier
              .fillMaxWidth()
              .height(150.dp),
            contentScale = ContentScale.Crop
          )
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = category,
              color = CampusBluePrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = if (title.isBlank()) "Untitled Campus Event" else title,
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "📅 $dateDisplay • $timeDisplay",
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "📍 $venue (${room.ifBlank { "Main Area" }})",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = if (description.isBlank()) "No description provided." else description,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(30.dp))

    // Navigation buttons (Back / Next / Publish)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      if (currentStep > 1) {
        OutlinedButton(
          onClick = { currentStep -= 1 },
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Back")
        }
        Spacer(modifier = Modifier.width(12.dp))
      }

      if (currentStep < 5) {
        Button(
          onClick = {
            if (currentStep == 1 && title.isBlank()) {
              viewModel.showMessage("Please enter an event title")
              return@Button
            }
            currentStep += 1
          },
          modifier = Modifier
            .weight(1f)
            .testTag("next_step_button"),
          colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary)
        ) {
          Text("Next")
          Spacer(modifier = Modifier.width(6.dp))
          Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
      } else {
        Button(
          onClick = {
            val landmark = LocationHelper.landmarks[selectedLandmarkIndex]
            viewModel.createEvent(
              title = title.ifBlank { "Campus Community Meetup" },
              description = description.ifBlank { "Exciting event on NSUK campus." },
              category = category,
              faculty = faculty,
              department = department,
              venue = venue,
              room = room,
              latitude = landmark.latitude,
              longitude = landmark.longitude,
              dateDisplay = dateDisplay,
              timeDisplay = timeDisplay,
              isFree = isFree,
              maxAttendees = maxAttendees.toIntOrNull() ?: 300,
              banner = bannerDrawable
            )
            onEventCreated()
          },
          modifier = Modifier
            .weight(1f)
            .testTag("publish_event_button"),
          colors = ButtonDefaults.buttonColors(containerColor = CampusTealAccent)
        ) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Publish Event", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
