package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.components.CategoryChipRow
import com.example.ui.components.EventCard
import com.example.ui.theme.CampusBluePrimary
import com.example.ui.viewmodel.CampusViewModel
import com.example.ui.viewmodel.DistanceFilter
import com.example.ui.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
  viewModel: CampusViewModel,
  onEventSelected: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val searchQuery by viewModel.searchQuery.collectAsState()
  val selectedCat by viewModel.selectedCategory.collectAsState()
  val selectedDist by viewModel.selectedDistanceFilter.collectAsState()
  val selectedFaculty by viewModel.selectedFacultyFilter.collectAsState()
  val selectedSort by viewModel.selectedSortOption.collectAsState()
  val filteredEvents by viewModel.filteredEventsWithDistance.collectAsState()

  val distanceScroll = rememberScrollState()
  val facultyList = listOf(
    "All Faculties",
    "Faculty of Computing",
    "Faculty of Science",
    "Faculty of Arts",
    "Faculty of Social Sciences",
    "Faculty of Law",
    "Faculty of Administration",
    "University-Wide"
  )
  var facultyMenuExpanded by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("explore_screen"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // 1. Search Bar
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
      ) {
        Text(
          text = "Explore Campus",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = searchQuery,
          onValueChange = { viewModel.searchQuery.value = it },
          placeholder = { Text("Search events, venues, organizers...") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear")
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CampusBluePrimary,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("explore_search_input")
        )
      }
    }

    // 2. Distance Filter Chips
    item {
      Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.NearMe,
            contentDescription = null,
            tint = CampusBluePrimary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Distance Radius:",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
          )
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(distanceScroll)
            .padding(horizontal = 16.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          DistanceFilter.entries.forEach { dist ->
            val isSelected = selectedDist == dist
            FilterChip(
              selected = isSelected,
              onClick = { viewModel.selectedDistanceFilter.value = dist },
              label = { Text(dist.label, fontSize = 12.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
              )
            )
          }
        }
      }
    }

    // 3. Category Filter Chips
    item {
      CategoryChipRow(
        selectedCategory = selectedCat,
        onSelectCategory = { cat -> viewModel.selectedCategory.value = cat }
      )
    }

    // 4. Faculty & Sort Options Row
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Faculty Selector
        ExposedDropdownMenuBox(
          expanded = facultyMenuExpanded,
          onExpandedChange = { facultyMenuExpanded = !facultyMenuExpanded },
          modifier = Modifier.weight(1f)
        ) {
          OutlinedTextField(
            value = selectedFaculty,
            onValueChange = {},
            readOnly = true,
            label = { Text("Faculty", fontSize = 11.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = facultyMenuExpanded) },
            textStyle = MaterialTheme.typography.bodySmall,
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
          )
          ExposedDropdownMenu(
            expanded = facultyMenuExpanded,
            onDismissRequest = { facultyMenuExpanded = false }
          ) {
            facultyList.forEach { fac ->
              DropdownMenuItem(
                text = { Text(fac, fontSize = 13.sp) },
                onClick = {
                  viewModel.selectedFacultyFilter.value = fac
                  facultyMenuExpanded = false
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Sort By Toggle
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          FilterChip(
            selected = true,
            onClick = {
              // Cycle sort
              val nextSort = when (selectedSort) {
                SortOption.DISTANCE -> SortOption.POPULARITY
                SortOption.POPULARITY -> SortOption.DATE
                SortOption.DATE -> SortOption.DISTANCE
              }
              viewModel.selectedSortOption.value = nextSort
            },
            label = { Text(selectedSort.label, fontSize = 12.sp) }
          )
        }
      }
    }

    // 5. Results Counter
    item {
      Text(
        text = "Found ${filteredEvents.size} events matching criteria",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
      )
    }

    // 6. Results List
    if (filteredEvents.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "No events match your search",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Try clearing filters or changing distance radius",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    } else {
      items(filteredEvents, key = { it.event.id }) { item ->
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
