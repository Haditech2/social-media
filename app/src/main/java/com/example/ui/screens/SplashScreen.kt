package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CampusAmberHighlight
import com.example.ui.theme.CampusBlueDark
import com.example.ui.theme.CampusBluePrimary
import com.example.ui.theme.CampusTealAccent

@Composable
fun SplashScreen(
  onGetStarted: () -> Unit,
  onContinueGoogle: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            CampusBlueDark,
            CampusBluePrimary,
            Color(0xFF0F172A)
          )
        )
      )
      .padding(24.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // App Icon / Logo
      Box(
        modifier = Modifier
          .size(100.dp)
          .clip(RoundedCornerShape(26.dp))
          .background(Color.White)
          .padding(8.dp),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.img_app_icon),
          contentDescription = "CampusConnect Logo",
          modifier = Modifier.fillMaxSize()
        )
      }

      Spacer(modifier = Modifier.height(28.dp))

      Text(
        text = "CampusConnect",
        style = MaterialTheme.typography.displayLarge,
        fontWeight = FontWeight.ExtraBold,
        color = Color.White,
        letterSpacing = (-0.5).sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Discover what's happening around campus.",
        style = MaterialTheme.typography.titleMedium,
        color = Color(0xFF93C5FD),
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Campus badge
      Box(
        modifier = Modifier
          .background(Color(0x33FFFFFF), RoundedCornerShape(20.dp))
          .padding(horizontal = 14.dp, vertical = 6.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.School,
            contentDescription = null,
            tint = CampusAmberHighlight,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Nasarawa State University, Keffi",
            color = Color.White,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(56.dp))

      // Action buttons
      Button(
        onClick = onGetStarted,
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("get_started_button"),
        colors = ButtonDefaults.buttonColors(
          containerColor = CampusTealAccent,
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp)
      ) {
        Text("Get Started", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(8.dp))
        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
      }

      Spacer(modifier = Modifier.height(14.dp))

      OutlinedButton(
        onClick = onContinueGoogle,
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("continue_google_button"),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(Color.White, Color(0xFF93C5FD)))),
        shape = RoundedCornerShape(16.dp)
      ) {
        Text("Continue with Google / Student SSO", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
  viewModel: com.example.ui.viewmodel.CampusViewModel,
  onAuthSuccess: (com.example.data.model.UserProfileEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Register
  val scrollState = rememberScrollState()

  val isAuthLoading by viewModel.isAuthLoading.collectAsState()
  val authError by viewModel.authErrorMessage.collectAsState()

  // Form states
  var email by remember { mutableStateOf("admin@nsuk.edu.ng") }
  var password by remember { mutableStateOf("Admin@NSUK2026!") }
  var fullName by remember { mutableStateOf("Abdul Abdullahadi") }
  var matricNumber by remember { mutableStateOf("NSUK/CMP/2022/0481") }
  var phone by remember { mutableStateOf("+234 812 345 6789") }
  var department by remember { mutableStateOf("Computer Science") }
  var selectedFaculty by remember { mutableStateOf("Faculty of Computing") }
  var selectedRole by remember { mutableStateOf("Student") }

  val faculties = listOf(
    "Faculty of Computing",
    "Faculty of Science",
    "Faculty of Arts",
    "Faculty of Social Sciences",
    "Faculty of Law",
    "Faculty of Administration",
    "Faculty of Education"
  )
  val roles = listOf("Student", "Staff", "Organizer", "Admin")

  var facultyExpanded by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(scrollState)
      .padding(20.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(16.dp))

    // Header Branding
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(CampusBluePrimary),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.School, contentDescription = null, tint = Color.White)
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = "CampusConnect",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "NSUK Keffi Secure Authentication",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Dedicated Credentials Preset Card for Easy Evaluation
    Card(
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = CampusBluePrimary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Demo & Admin Credentials",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Admin: admin@nsuk.edu.ng | Pass: Admin@NSUK2026!",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onPrimaryContainer,
          fontWeight = FontWeight.SemiBold
        )
        Text(
          text = "Student: lahadiademu7@gmail.com | Pass: password123",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Button(
            onClick = {
              email = "admin@nsuk.edu.ng"
              password = "Admin@NSUK2026!"
              selectedTab = 0
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
          ) {
            Text("Fill Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = {
              email = "lahadiademu7@gmail.com"
              password = "password123"
              selectedTab = 0
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = CampusTealAccent),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
          ) {
            Text("Fill Student", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = {
              email = "organizer@nsuk.edu.ng"
              password = "Organizer@2026!"
              selectedTab = 0
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
          ) {
            Text("Organizer", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Login / Register Tabs
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = MaterialTheme.colorScheme.surfaceVariant,
      contentColor = MaterialTheme.colorScheme.primary,
      modifier = Modifier.clip(RoundedCornerShape(14.dp))
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("Log In", fontWeight = FontWeight.Bold) }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("Register", fontWeight = FontWeight.Bold) }
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Display Error Message if any
    val currentError = authError
    if (currentError != null) {
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = currentError,
          color = Color(0xFF991B1B),
          fontSize = 12.sp,
          modifier = Modifier.padding(10.dp)
        )
      }
      Spacer(modifier = Modifier.height(12.dp))
    }

    if (selectedTab == 0) {
      // LOGIN FORM
      OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("University Email") },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("auth_email_input"),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Password") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("auth_password_input"),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(20.dp))

      Button(
        onClick = {
          viewModel.signIn(email, password) { profile ->
            onAuthSuccess(profile)
          }
        },
        enabled = !isAuthLoading && email.isNotBlank() && password.isNotBlank(),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("login_button"),
        colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary),
        shape = RoundedCornerShape(14.dp)
      ) {
        if (isAuthLoading) {
          Text("Signing In...", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        } else {
          Text("Sign In with Firebase / SSO", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      TextButton(onClick = {
        viewModel.showMessage("Password recovery link sent to $email")
      }) {
        Text("Forgot password?", color = MaterialTheme.colorScheme.primary)
      }
    } else {
      // REGISTRATION FORM
      OutlinedTextField(
        value = fullName,
        onValueChange = { fullName = it },
        label = { Text("Full Name") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("University Email (@nsuk.edu.ng)") },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = matricNumber,
        onValueChange = { matricNumber = it },
        label = { Text("Matric Number") },
        leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = phone,
        onValueChange = { phone = it },
        label = { Text("Phone Number") },
        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Faculty Dropdown
      ExposedDropdownMenuBox(
        expanded = facultyExpanded,
        onExpandedChange = { facultyExpanded = !facultyExpanded }
      ) {
        OutlinedTextField(
          value = selectedFaculty,
          onValueChange = {},
          readOnly = true,
          label = { Text("Faculty") },
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = facultyExpanded) },
          modifier = Modifier
            .menuAnchor()
            .fillMaxWidth()
        )
        ExposedDropdownMenu(
          expanded = facultyExpanded,
          onDismissRequest = { facultyExpanded = false }
        ) {
          faculties.forEach { faculty ->
            DropdownMenuItem(
              text = { Text(faculty) },
              onClick = {
                selectedFaculty = faculty
                facultyExpanded = false
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = department,
        onValueChange = { department = it },
        label = { Text("Department") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Role Selector
      Text(
        text = "Register as:",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.align(Alignment.Start)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        roles.forEach { role ->
          val isSelected = selectedRole == role
          Button(
            onClick = { selectedRole = role },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isSelected) CampusBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
              contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            ),
            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 2.dp),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text(role, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Password (at least 6 characters)") },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(20.dp))

      Button(
        onClick = {
          viewModel.signUp(
            fullName = fullName,
            email = email,
            password = password,
            matricNumber = matricNumber,
            faculty = selectedFaculty,
            department = department,
            phone = phone,
            role = selectedRole
          ) { profile ->
            onAuthSuccess(profile)
          }
        },
        enabled = !isAuthLoading && fullName.isNotBlank() && email.isNotBlank() && password.length >= 6,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("register_button"),
        colors = ButtonDefaults.buttonColors(containerColor = CampusTealAccent),
        shape = RoundedCornerShape(14.dp)
      ) {
        if (isAuthLoading) {
          Text("Creating Account...", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        } else {
          Text("Create Account (Firebase & NSUK)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
      }
    }
  }
}
