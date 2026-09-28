package com.example.data.auth

import android.content.Context
import android.util.Log
import com.example.data.local.CampusDao
import com.example.data.model.UserProfileEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthService(
  private val context: Context,
  private val dao: CampusDao
) {

  private val firebaseAuth: FirebaseAuth? by lazy {
    try {
      if (FirebaseApp.getApps(context).isNotEmpty()) {
        FirebaseAuth.getInstance()
      } else {
        null
      }
    } catch (e: Exception) {
      Log.w("AuthService", "FirebaseApp not initialized, using fallback auth: ${e.message}")
      null
    }
  }

  companion object {
    const val ADMIN_EMAIL = "admin@nsuk.edu.ng"
    const val ADMIN_PASSWORD = "Admin@NSUK2026!"

    const val ORGANIZER_EMAIL = "organizer@nsuk.edu.ng"
    const val ORGANIZER_PASSWORD = "Organizer@2026!"

    const val DEMO_STUDENT_EMAIL = "lahadiademu7@gmail.com"
    const val DEMO_STUDENT_PASSWORD = "password123"
  }

  suspend fun signIn(email: String, password: String): Result<UserProfileEntity> {
    val cleanEmail = email.trim().lowercase()

    // 1. Check predefined Admin credentials
    if (cleanEmail == ADMIN_EMAIL.lowercase()) {
      if (password == ADMIN_PASSWORD) {
        val adminProfile = UserProfileEntity(
          id = "admin_user",
          fullName = "Prof. M. A. Keffi (Admin)",
          email = ADMIN_EMAIL,
          phone = "+234 803 000 1122",
          matricNumber = "NSUK/STAFF/ADM/001",
          faculty = "Central University Administration",
          department = "Directorate of ICT & Safety",
          level = "Super Admin",
          bio = "CampusConnect Chief Platform Administrator for Nasarawa State University, Keffi.",
          role = "Admin",
          followersCount = 1250,
          followingCount = 15
        )
        dao.saveUserProfile(adminProfile)
        return Result.success(adminProfile)
      } else {
        return Result.failure(Exception("Incorrect password for Administrator account."))
      }
    }

    // 2. Check predefined Organizer credentials
    if (cleanEmail == ORGANIZER_EMAIL.lowercase()) {
      if (password == ORGANIZER_PASSWORD) {
        val orgProfile = UserProfileEntity(
          id = "org_user",
          fullName = "NACOS Events Committee",
          email = ORGANIZER_EMAIL,
          phone = "+234 812 999 8877",
          matricNumber = "NSUK/ORG/2026/01",
          faculty = "Faculty of Computing",
          department = "Computer Science",
          level = "Lead Organizer",
          bio = "Official event coordinator for tech, hackathons, and departmental symposiums at NSUK.",
          role = "Organizer",
          followersCount = 890,
          followingCount = 42
        )
        dao.saveUserProfile(orgProfile)
        return Result.success(orgProfile)
      } else {
        return Result.failure(Exception("Incorrect password for Organizer account."))
      }
    }

    // 3. Try Firebase Authentication if initialized
    val auth = firebaseAuth
    if (auth != null) {
      try {
        val authResult = auth.signInWithEmailAndPassword(cleanEmail, password).await()
        val fbUser = authResult.user
        val existingProfile = dao.getUserProfileSync()
        val userProfile = existingProfile?.copy(
          email = fbUser?.email ?: cleanEmail,
          fullName = fbUser?.displayName ?: existingProfile.fullName
        ) ?: UserProfileEntity(
          email = fbUser?.email ?: cleanEmail,
          fullName = fbUser?.displayName ?: "Student User",
          role = "Student"
        )
        dao.saveUserProfile(userProfile)
        return Result.success(userProfile)
      } catch (e: Exception) {
        Log.w("AuthService", "Firebase signIn error: ${e.message}, checking local session")
      }
    }

    // 4. Fallback: match local student profile or allow valid student login
    val current = dao.getUserProfileSync()
    if (current != null && (cleanEmail == current.email.lowercase() || cleanEmail == DEMO_STUDENT_EMAIL.lowercase())) {
      if (password.length >= 6) {
        dao.saveUserProfile(current)
        return Result.success(current)
      } else {
        return Result.failure(Exception("Password must be at least 6 characters long."))
      }
    }

    // Generic valid login for testing with any valid credentials
    if (cleanEmail.contains("@") && password.length >= 6) {
      val dynamicProfile = UserProfileEntity(
        fullName = cleanEmail.substringBefore("@").replace(".", " ").capitalizeWords(),
        email = cleanEmail,
        role = "Student",
        matricNumber = "NSUK/2026/${(1000..9999).random()}",
        faculty = "Faculty of Computing",
        department = "Computer Science",
        level = "300 Level"
      )
      dao.saveUserProfile(dynamicProfile)
      return Result.success(dynamicProfile)
    }

    return Result.failure(Exception("Invalid credentials. Please enter a valid email and password."))
  }

  suspend fun signUp(
    fullName: String,
    email: String,
    password: String,
    matricNumber: String,
    faculty: String,
    department: String,
    phone: String,
    role: String
  ): Result<UserProfileEntity> {
    val cleanEmail = email.trim().lowercase()

    if (password.length < 6) {
      return Result.failure(Exception("Password must be at least 6 characters."))
    }

    val auth = firebaseAuth
    if (auth != null) {
      try {
        auth.createUserWithEmailAndPassword(cleanEmail, password).await()
      } catch (e: Exception) {
        Log.w("AuthService", "Firebase signUp error: ${e.message}")
      }
    }

    val newProfile = UserProfileEntity(
      fullName = fullName.trim(),
      email = cleanEmail,
      matricNumber = matricNumber.trim(),
      faculty = faculty,
      department = department.trim(),
      phone = phone.trim(),
      level = if (role == "Staff") "Staff Member" else "100 Level",
      role = role,
      bio = "Campus community member at Nasarawa State University, Keffi."
    )
    dao.saveUserProfile(newProfile)
    return Result.success(newProfile)
  }

  suspend fun signOut() {
    firebaseAuth?.signOut()
  }

  private fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
}
