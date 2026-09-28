package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnnouncementEntity
import com.example.location.LocationHelper
import com.example.ui.components.CategoryChipRow
import com.example.ui.components.ChangeCampusLocationDialog
import com.example.ui.components.EventCard
import com.example.ui.theme.CampusAmberHighlight
import com.example.ui.theme.CampusBluePrimary
import com.example.ui.theme.CampusCoral
import com.example.ui.theme.CampusTealAccent
import com.example.ui.viewmodel.CampusViewModel

@Composable
fun HomeScreen(
  viewModel: CampusViewModel,
  onEventSelected: (String) -> Unit,
  onNavigateToExplore: () -> Unit,
  modifier: Modifier = Modifier
) {
  val userProfile by viewModel.userProfile.collectAsState()
  val eventsWithDist by viewModel.filteredEventsWithDistance.collectAsState()
  val trendingEvents by viewModel.trendingEvents.collectAsState()
  val announcements by viewModel.announcements.collectAsState()
  val selectedCat by viewModel.selectedCategory.collectAsState()
  val locationName by viewModel.currentLocationName.collectAsState()
  val userLat by viewModel.userLatitude.collectAsState()
  val userLng by viewModel.userLongitude.collectAsState()

  var showLocationDialog by remember { mutableStateOf(false) }

  if (showLocationDialog) {
    ChangeCampusLocationDialog(
      currentLat = userLat,
      currentLng = userLng,
      onDismiss = { showLocationDialog = false },
      onSelectLandmark = { landmark ->
        viewModel.setLocationToLandmark(landmark)
      }
    )
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("home_screen"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // 1. Header with greeting and location selector
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Good Morning, ${userProfile?.fullName?.split(" ")?.firstOrNull() ?: "Abdul"} 👋",
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "What's happening around you?",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // Search shortcut button
          IconButton(
            onClick = onNavigateToExplore,
            modifier = Modifier
              .size(44.dp)
              .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = MaterialTheme.colorScheme.primary
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Campus Location Pill
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable { showLocationDialog = true }
            .padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = CampusBluePrimary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = locationName,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
          )
          Spacer(modifier = Modifier.width(6.dp))
          Icon(
            imageVector = Icons.Default.EditLocation,
            contentDescription = "Change spot",
            tint = CampusBluePrimary,
            modifier = Modifier.size(14.dp)
          )
        }
      }
    }

    // 2. Official Campus Announcements Banner
    if (announcements.isNotEmpty()) {
      item {
        Column(modifier = Modifier.padding(top = 10.dp)) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Campaign,
              contentDescription = null,
              tint = CampusBluePrimary,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Official University Announcements",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
              color = CampusBluePrimary
            )
          }

          LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            items(announcements) { ann ->
              Card(
                modifier = Modifier
                  .width(300.dp)
                  .padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = ann.authorName,
                      style = MaterialTheme.typography.labelSmall,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary
                    )
                    if (ann.isVerified) {
                      Spacer(modifier = Modifier.width(4.dp))
                      Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = CampusTealAccent,
                        modifier = Modifier.size(12.dp)
                      )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                      text = ann.dateDisplay,
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = ann.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = ann.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }
            }
          }
        }
      }
    }

    // 3. Category Filter Chips
    item {
      Spacer(modifier = Modifier.height(10.dp))
      CategoryChipRow(
        selectedCategory = selectedCat,
        onSelectCategory = { cat -> viewModel.selectedCategory.value = cat }
      )
    }

    // 4. Trending Around Campus Section
    if (trendingEvents.isNotEmpty()) {
      item {
        Column(modifier = Modifier.padding(top = 12.dp)) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Whatshot,
                contentDescription = null,
                tint = CampusCoral,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "🔥 Trending Around Campus",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }
          }

          LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            items(trendingEvents) { event ->
              val distMeters = LocationHelper.calculateDistanceMeters(userLat, userLng, event.latitude, event.longitude)
              val distFormatted = LocationHelper.formatDistance(distMeters)
              Card(
                modifier = Modifier
                  .width(260.dp)
                  .clickable { onEventSelected(event.id) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = event.category,
                      color = CampusBluePrimary,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
                    )
                    Text(
                      text = distFormatted,
                      fontSize = 11.sp,
                      color = CampusAmberHighlight,
                      fontWeight = FontWeight.Bold
                    )
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = event.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "${event.startDateDisplay} • ${event.venue}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                  )
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "${event.interestedCount} students interested",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                  )
                }
              }
            }
          }
        }
      }
    }

    // 5. Events Near You Header
    item {
      Spacer(modifier = Modifier.height(16.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Events Near You",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "${eventsWithDist.size} found",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // 6. Event Card Feed
    if (eventsWithDist.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "No events found in this category near you",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Try switching category or expanding distance filter",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }
    } else {
      items(eventsWithDist, key = { it.event.id }) { item ->
        EventCard(
          event = item.event,
          distanceFormatted = item.distanceFormatted,
          onEventClick = { onEventSelected(item.event.id) },
          onLikeClick = { viewModel.toggleLike(item.event.id) },
          onSaveClick = { viewModel.toggleSave(item.event.id) },
          onCommentClick = { onEventSelected(item.event.id) }
        )
      }
    }
  }
}
