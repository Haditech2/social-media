package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.EventEntity
import com.example.ui.components.CampusInteractiveMap
import com.example.ui.components.CategoryChipRow
import com.example.ui.viewmodel.CampusViewModel

@Composable
fun CampusMapScreen(
  viewModel: CampusViewModel,
  onEventSelected: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val eventsWithDist by viewModel.filteredEventsWithDistance.collectAsState()
  val userLat by viewModel.userLatitude.collectAsState()
  val userLng by viewModel.userLongitude.collectAsState()
  val selectedCat by viewModel.selectedCategory.collectAsState()

  val events = eventsWithDist.map { it.event }

  Box(
    modifier = modifier
      .fillMaxSize()
      .testTag("campus_map_screen")
  ) {
    CampusInteractiveMap(
      events = events,
      userLat = userLat,
      userLng = userLng,
      onEventSelected = onEventSelected,
      onGetDirections = { event ->
        viewModel.showMessage("Route to ${event.venue} shown on campus map.")
      }
    )

    // Floating Category Chips atop map
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp)
    ) {
      CategoryChipRow(
        selectedCategory = selectedCat,
        onSelectCategory = { cat -> viewModel.selectedCategory.value = cat },
        modifier = Modifier.background(MaterialTheme.colorScheme.background.copy(alpha = 0.85f))
      )
    }
  }
}
