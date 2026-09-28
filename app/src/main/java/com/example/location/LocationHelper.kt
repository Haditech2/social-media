package com.example.location

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object LocationHelper {
  // Default NSUK Main Campus Center (Keffi, Nasarawa State, Nigeria)
  const val NSUK_CAMPUS_LAT = 8.8475
  const val NSUK_CAMPUS_LNG = 7.8776

  /**
   * Calculates distance in meters using Haversine formula
   */
  fun calculateDistanceMeters(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double
  ): Double {
    val earthRadius = 6371000.0 // meters
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
        sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
  }

  fun formatDistance(meters: Double): String {
    return if (meters < 1000) {
      "${meters.toInt()}m away"
    } else {
      val km = meters / 1000.0
      String.format("%.1f km away", km)
    }
  }

  data class CampusLandmark(
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
  )

  val landmarks = listOf(
    CampusLandmark("Faculty of Computing", "Dept of Computer Science & Software Labs", 8.8480, 7.8780),
    CampusLandmark("Senate Building", "Vice-Chancellor's Office & Registry", 8.8465, 7.8765),
    CampusLandmark("Main Sports Complex", "University Stadium & Pavilion", 8.8510, 7.8810),
    CampusLandmark("Faculty of Science", "Science Complex & Laboratories", 8.8490, 7.8775),
    CampusLandmark("Main Auditorium", "Convocation Hall & Events", 8.8472, 7.8768),
    CampusLandmark("Faculty of Law", "Moot Court & Law Library", 8.8440, 7.8740),
    CampusLandmark("Faculty of Administration", "Business & Public Admin", 8.8485, 7.8760),
    CampusLandmark("NSUK Main Library", "Central Academic Library", 8.8470, 7.8770)
  )
}
