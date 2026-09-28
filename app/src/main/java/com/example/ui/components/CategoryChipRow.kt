package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CatAcademic
import com.example.ui.theme.CatCareer
import com.example.ui.theme.CatCultural
import com.example.ui.theme.CatReligious
import com.example.ui.theme.CatSocial
import com.example.ui.theme.CatSports
import com.example.ui.theme.CatTech
import com.example.ui.theme.CatWorkshop

data class CategoryItem(val name: String, val icon: ImageVector)

val CampusCategories = listOf(
  CategoryItem("All", Icons.Default.AllInclusive),
  CategoryItem("Technology", Icons.Default.Computer),
  CategoryItem("Academic", Icons.Default.School),
  CategoryItem("Sports", Icons.Default.SportsSoccer),
  CategoryItem("Social", Icons.Default.Groups),
  CategoryItem("Cultural", Icons.Default.Museum),
  CategoryItem("Workshop", Icons.Default.Build),
  CategoryItem("Career", Icons.Default.Work),
  CategoryItem("Religious", Icons.Default.Park)
)

fun getCategoryColor(category: String): androidx.compose.ui.graphics.Color {
  return when (category.lowercase()) {
    "technology", "tech" -> CatTech
    "academic" -> CatAcademic
    "sports" -> CatSports
    "social" -> CatSocial
    "cultural" -> CatCultural
    "workshop" -> CatWorkshop
    "career" -> CatCareer
    "religious" -> CatReligious
    else -> CatTech
  }
}

@Composable
fun CategoryChipRow(
  selectedCategory: String,
  onSelectCategory: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  Row(
    modifier = modifier
      .fillMaxWidth()
      .horizontalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    CampusCategories.forEach { cat ->
      val isSelected = selectedCategory.equals(cat.name, ignoreCase = true)
      FilterChip(
        selected = isSelected,
        onClick = { onSelectCategory(cat.name) },
        label = { Text(cat.name) },
        leadingIcon = {
          Icon(
            imageVector = cat.icon,
            contentDescription = cat.name,
            modifier = Modifier.size(16.dp)
          )
        },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
          selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
          selectedLeadingIconColor = MaterialTheme.colorScheme.primary
        )
      )
    }
  }
}
