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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.getCategoryColor
import com.example.ui.theme.CampusBluePrimary
import com.example.ui.theme.CampusTealAccent
import com.example.ui.viewmodel.CampusViewModel

@Composable
fun CommunitiesScreen(
  viewModel: CampusViewModel,
  modifier: Modifier = Modifier
) {
  val communities by viewModel.communities.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("communities_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Column {
        Text(
          text = "Campus Communities",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Join faculty clubs, student associations, and interest hubs at NSUK Keffi.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    items(communities, key = { it.id }) { comm ->
      val catColor = getCategoryColor(comm.category)

      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .background(catColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = comm.category,
                color = catColor,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }

            Text(
              text = comm.faculty,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .background(catColor, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Groups, contentDescription = null, tint = Color.White)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = comm.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "${comm.memberCount} active campus members",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = comm.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            if (comm.isJoined) {
              OutlinedButton(
                onClick = { viewModel.toggleJoinCommunity(comm) },
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = CampusTealAccent, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Joined", color = CampusTealAccent, fontWeight = FontWeight.Bold)
              }
            } else {
              Button(
                onClick = { viewModel.toggleJoinCommunity(comm) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary)
              ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Join Community")
              }
            }
          }
        }
      }
    }
  }
}
