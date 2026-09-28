package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.EventEntity
import com.example.ui.theme.CampusAmberHighlight
import com.example.ui.theme.CampusCoral

fun getDrawableResByName(name: String): Int {
  return when (name) {
    "img_hackathon" -> R.drawable.img_hackathon
    "img_sports" -> R.drawable.img_sports
    "img_cultural" -> R.drawable.img_cultural
    "img_hero_campus" -> R.drawable.img_hero_campus
    else -> R.drawable.img_hero_campus
  }
}

@Composable
fun EventCard(
  event: EventEntity,
  distanceFormatted: String,
  onEventClick: () -> Unit,
  onLikeClick: () -> Unit,
  onSaveClick: () -> Unit,
  onCommentClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val catColor = getCategoryColor(event.category)
  val heartColor by animateColorAsState(
    targetValue = if (event.isLiked) CampusCoral else MaterialTheme.colorScheme.onSurfaceVariant,
    label = "heartColor"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("event_card_${event.id}")
      .clickable { onEventClick() },
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column {
      // 1. Poster Banner with Category Tag & Distance Badge
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
      ) {
        Image(
          painter = painterResource(id = getDrawableResByName(event.bannerDrawable)),
          contentDescription = event.title,
          modifier = Modifier.fillMaxWidth(),
          contentScale = ContentScale.Crop
        )

        // Gradient overlay for readability
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .align(Alignment.BottomCenter)
            .background(
              Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color(0xCC000000))
              )
            )
        )

        // Top badges: Category and Distance
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Category Chip
          Box(
            modifier = Modifier
              .background(catColor.copy(alpha = 0.95f), RoundedCornerShape(12.dp))
              .padding(horizontal = 10.dp, vertical = 5.dp)
          ) {
            Text(
              text = event.category,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }

          // Distance Badge
          Box(
            modifier = Modifier
              .background(Color(0xCC0F172A), RoundedCornerShape(12.dp))
              .padding(horizontal = 10.dp, vertical = 5.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.NearMe,
                contentDescription = null,
                tint = CampusAmberHighlight,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = distanceFormatted,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
              )
            }
          }
        }

        // Bottom Banner Overlay: Registration badge or Free/Paid
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomStart)
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (event.isRegistered) {
            Box(
              modifier = Modifier
                .background(Color(0xFF059669), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Registered",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                )
              }
            }
            Spacer(modifier = Modifier.width(8.dp))
          }

          Text(
            text = "${event.interestedCount} interested",
            color = Color.White,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp
          )
        }
      }

      // 2. Card Content
      Column(
        modifier = Modifier.padding(16.dp)
      ) {
        // Date & Time Line
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${event.startDateDisplay} • ${event.timeDisplay}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Title
        Text(
          text = event.title,
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Location & Organizer
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = event.venue,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "By ${event.organizerName}",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Social Interaction Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            // Like Button
            Row(
              modifier = Modifier
                .clip(CircleShape)
                .clickable { onLikeClick() }
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .testTag("like_button_${event.id}"),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (event.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Like",
                tint = heartColor,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "${event.interestedCount}",
                style = MaterialTheme.typography.labelLarge,
                color = heartColor,
                fontWeight = FontWeight.SemiBold
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Comment Button
            Row(
              modifier = Modifier
                .clip(CircleShape)
                .clickable { onCommentClick() }
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .testTag("comment_button_${event.id}"),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Chat,
                contentDescription = "Comments",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(19.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Discuss",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            // Share Button
            IconButton(
              onClick = {
                val sendIntent = Intent().apply {
                  action = Intent.ACTION_SEND
                  putExtra(
                    Intent.EXTRA_TEXT,
                    "Check out '${event.title}' happening at NSUK Keffi on CampusConnect! Venue: ${event.venue}."
                  )
                  type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Share Event via"))
              },
              modifier = Modifier.testTag("share_button_${event.id}")
            ) {
              Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            // Save / Bookmark Button
            IconButton(
              onClick = { onSaveClick() },
              modifier = Modifier.testTag("save_button_${event.id}")
            ) {
              Icon(
                imageVector = if (event.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = "Save",
                tint = if (event.isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }
  }
}
