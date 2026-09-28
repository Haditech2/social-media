package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventEntity
import com.example.location.LocationHelper
import com.example.ui.theme.CampusAmberHighlight
import com.example.ui.theme.CampusBluePrimary
import com.example.ui.theme.CampusTealAccent
import kotlin.math.roundToInt

@Composable
fun CampusInteractiveMap(
  events: List<EventEntity>,
  userLat: Double,
  userLng: Double,
  onEventSelected: (String) -> Unit,
  onGetDirections: (EventEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedMarkerEvent by remember { mutableStateOf<EventEntity?>(events.firstOrNull()) }
  var scale by remember { mutableFloatStateOf(1.0f) }
  var offsetX by remember { mutableFloatStateOf(0f) }
  var offsetY by remember { mutableFloatStateOf(0f) }

  // Reference bounding box for NSUK campus area
  // Lat: 8.8420 to 8.8530 (height ~ 0.011)
  // Lng: 7.8720 to 7.8830 (width ~ 0.011)
  val minLat = 8.8420
  val maxLat = 8.8530
  val minLng = 7.8720
  val maxLng = 7.8830

  BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val canvasWidth = constraints.maxWidth.toFloat()
    val canvasHeight = constraints.maxHeight.toFloat()

    fun latLngToScreen(lat: Double, lng: Double): Offset {
      val normX = ((lng - minLng) / (maxLng - minLng)).toFloat()
      val normY = 1.0f - ((lat - minLat) / (maxLat - minLat)).toFloat() // invert Y for screen coords
      val rawX = normX * canvasWidth
      val rawY = normY * canvasHeight
      val centerX = canvasWidth / 2f
      val centerY = canvasHeight / 2f
      val transformedX = centerX + (rawX - centerX) * scale + offsetX
      val transformedY = centerY + (rawY - centerY) * scale + offsetY
      return Offset(transformedX, transformedY)
    }

    // 1. Gesture detector & Map Canvas
    Box(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
          detectTransformGestures { _, pan, zoom, _ ->
            scale = (scale * zoom).coerceIn(0.7f, 3.5f)
            offsetX += pan.x
            offsetY += pan.y
          }
        }
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        // Background Campus Grounds
        drawRect(color = Color(0xFFF1F5F9))

        // Campus boundary & green park areas
        drawRoundRect(
          color = Color(0xFFDCFCE7),
          topLeft = Offset(canvasWidth * 0.1f * scale + offsetX, canvasHeight * 0.15f * scale + offsetY),
          size = Size(canvasWidth * 0.8f * scale, canvasHeight * 0.7f * scale),
          cornerRadius = CornerRadius(24f * scale)
        )

        // Academic zone quads (subtle campus building layouts)
        val buildingColor = Color(0xFFE2E8F0)
        drawRoundRect(
          color = buildingColor,
          topLeft = Offset(canvasWidth * 0.35f * scale + offsetX, canvasHeight * 0.3f * scale + offsetY),
          size = Size(180f * scale, 120f * scale),
          cornerRadius = CornerRadius(12f)
        )
        drawRoundRect(
          color = buildingColor,
          topLeft = Offset(canvasWidth * 0.55f * scale + offsetX, canvasHeight * 0.55f * scale + offsetY),
          size = Size(200f * scale, 140f * scale),
          cornerRadius = CornerRadius(12f)
        )

        // Campus main avenue roads (Senate Drive & Computing Way)
        val roadColor = Color(0xFFCBD5E1)
        val roadPath = Path().apply {
          val start = latLngToScreen(8.8430, 7.8730)
          val senate = latLngToScreen(8.8465, 7.8765)
          val comp = latLngToScreen(8.8480, 7.8780)
          val sports = latLngToScreen(8.8515, 7.8815)
          moveTo(start.x, start.y)
          lineTo(senate.x, senate.y)
          lineTo(comp.x, comp.y)
          lineTo(sports.x, sports.y)
        }
        drawPath(
          path = roadPath,
          color = roadColor,
          style = Stroke(width = 18f * scale, cap = StrokeCap.Round)
        )

        // Sports stadium pitch oval
        val stadiumCenter = latLngToScreen(8.8510, 7.8810)
        drawOval(
          color = Color(0xFF86EFAC),
          topLeft = Offset(stadiumCenter.x - 45f * scale, stadiumCenter.y - 30f * scale),
          size = Size(90f * scale, 60f * scale)
        )
        drawOval(
          color = Color.White,
          topLeft = Offset(stadiumCenter.x - 45f * scale, stadiumCenter.y - 30f * scale),
          size = Size(90f * scale, 60f * scale),
          style = Stroke(width = 3f * scale)
        )

        // User Location Radar Pulse & Pin
        val userScreenPos = latLngToScreen(userLat, userLng)
        drawCircle(
          color = CampusBluePrimary.copy(alpha = 0.2f),
          radius = 35f * scale,
          center = userScreenPos
        )
        drawCircle(
          color = CampusBluePrimary,
          radius = 12f * scale,
          center = userScreenPos
        )
        drawCircle(
          color = Color.White,
          radius = 5f * scale,
          center = userScreenPos
        )
      }

      // Event Markers layer (interactive overlay)
      events.forEach { event ->
        val pos = latLngToScreen(event.latitude, event.longitude)
        val isSelected = selectedMarkerEvent?.id == event.id
        val markerColor = getCategoryColor(event.category)

        Box(
          modifier = Modifier
            .offset { IntOffset(pos.x.roundToInt() - 24, pos.y.roundToInt() - 48) }
            .size(48.dp)
            .clickable { selectedMarkerEvent = event },
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(if (isSelected) 36.dp else 28.dp)
                .background(markerColor, CircleShape)
                .border(2.dp, Color.White, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Place,
                contentDescription = event.title,
                tint = Color.White,
                modifier = Modifier.size(if (isSelected) 22.dp else 16.dp)
              )
            }
            // Small pointer triangle / shadow
            Box(
              modifier = Modifier
                .size(6.dp)
                .background(Color(0x55000000), CircleShape)
            )
          }
        }
      }
    }

    // Top Controls: "Search this area" pill and campus legend
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.TopCenter)
        .padding(top = 16.dp),
      contentAlignment = Alignment.Center
    ) {
      Row(
        modifier = Modifier
          .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(24.dp))
          .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(24.dp))
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.LocationSearching,
          contentDescription = null,
          tint = CampusBluePrimary,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "NSUK Keffi Campus Map",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = Color(0xFF0F172A)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "• ${events.size} Active Events",
          fontSize = 12.sp,
          color = CampusTealAccent,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    // Recenter & Reset Buttons
    Column(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(top = 70.dp, end = 16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      FloatingActionButton(
        onClick = {
          scale = 1.0f
          offsetX = 0f
          offsetY = 0f
        },
        modifier = Modifier.size(44.dp),
        shape = CircleShape,
        containerColor = Color.White,
        contentColor = CampusBluePrimary
      ) {
        Icon(Icons.Default.Refresh, contentDescription = "Reset view", modifier = Modifier.size(20.dp))
      }

      FloatingActionButton(
        onClick = {
          scale = 1.6f
          offsetX = 0f
          offsetY = 0f
        },
        modifier = Modifier.size(44.dp),
        shape = CircleShape,
        containerColor = CampusBluePrimary,
        contentColor = Color.White
      ) {
        Icon(Icons.Default.MyLocation, contentDescription = "My location", modifier = Modifier.size(20.dp))
      }
    }

    // Bottom Sheet Event Preview
    selectedMarkerEvent?.let { event ->
      val distMeters = LocationHelper.calculateDistanceMeters(userLat, userLng, event.latitude, event.longitude)
      val distFormatted = LocationHelper.formatDistance(distMeters)
      val catColor = getCategoryColor(event.category)

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.BottomCenter)
          .padding(16.dp)
          .testTag("map_event_sheet"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .background(catColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = event.category,
                color = catColor,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.NearMe,
                contentDescription = null,
                tint = CampusAmberHighlight,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = distFormatted,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = event.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "${event.startDateDisplay} • ${event.timeDisplay}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
          )

          Text(
            text = event.venue,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = { onGetDirections(event) },
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Directions")
            }

            Button(
              onClick = { onEventSelected(event.id) },
              modifier = Modifier.weight(1.2f),
              colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary)
            ) {
              Text("View Event")
            }
          }
        }
      }
    }
  }
}
