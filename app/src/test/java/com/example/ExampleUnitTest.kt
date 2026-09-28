package com.example

import com.example.data.auth.AuthService
import com.example.location.LocationHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun distanceCalculation_isAccurateForCampus() {
    // NSUK Computing to Sports Complex (~460 meters)
    val distance = LocationHelper.calculateDistanceMeters(
      8.8480, 7.8780,
      8.8510, 7.8810
    )
    assertTrue("Distance should be around 400-500 meters", distance in 350.0..600.0)
  }

  @Test
  fun formatDistance_handlesMetersAndKilometers() {
    assertEquals("450m away", LocationHelper.formatDistance(450.0))
    assertEquals("1.5 km away", LocationHelper.formatDistance(1500.0))
  }

  @Test
  fun adminCredentials_areConfiguredCorrectly() {
    assertEquals("admin@nsuk.edu.ng", AuthService.ADMIN_EMAIL)
    assertEquals("Admin@NSUK2026!", AuthService.ADMIN_PASSWORD)
  }
}
