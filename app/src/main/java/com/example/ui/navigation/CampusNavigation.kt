package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AdminModerationScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CampusMapScreen
import com.example.ui.screens.CheckInScannerScreen
import com.example.ui.screens.CommunitiesScreen
import com.example.ui.screens.EventCreateScreen
import com.example.ui.screens.EventDetailScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.CampusAmberHighlight
import com.example.ui.theme.CampusBluePrimary
import com.example.ui.theme.CampusTealAccent
import com.example.ui.viewmodel.CampusViewModel

enum class MainTab(
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector
) {
  HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
  EXPLORE("Explore", Icons.Filled.Explore, Icons.Outlined.Explore),
  CREATE("Create", Icons.Filled.Add, Icons.Filled.Add),
  MAP("Map", Icons.Filled.Map, Icons.Outlined.Map),
  PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

sealed class Screen {
  data object Splash : Screen()
  data object Auth : Screen()
  data class Main(val activeTab: MainTab) : Screen()
  data class EventDetail(val eventId: String) : Screen()
  data object Communities : Screen()
  data object CheckInScanner : Screen()
  data object AdminModeration : Screen()
}

@Composable
fun CampusConnectApp(
  viewModel: CampusViewModel
) {
  var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
  val snackbarHostState = remember { SnackbarHostState() }
  val snackbarMsg by viewModel.snackbarMessage.collectAsState()

  LaunchedEffect(snackbarMsg) {
    snackbarMsg?.let {
      snackbarHostState.showSnackbar(it)
      viewModel.clearSnackbar()
    }
  }

  when (val screen = currentScreen) {
    is Screen.Splash -> {
      SplashScreen(
        onGetStarted = { currentScreen = Screen.Auth },
        onContinueGoogle = { currentScreen = Screen.Main(MainTab.HOME) }
      )
    }

    is Screen.Auth -> {
      AuthScreen(
        viewModel = viewModel,
        onAuthSuccess = { profile ->
          currentScreen = Screen.Main(MainTab.HOME)
        }
      )
    }

    is Screen.EventDetail -> {
      EventDetailScreen(
        eventId = screen.eventId,
        viewModel = viewModel,
        onBack = { currentScreen = Screen.Main(MainTab.HOME) }
      )
    }

    is Screen.CheckInScanner -> {
      BackHandler { currentScreen = Screen.Main(MainTab.PROFILE) }
      Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
      ) { innerPadding ->
        CheckInScannerScreen(
          viewModel = viewModel,
          modifier = Modifier.padding(innerPadding)
        )
      }
    }

    is Screen.AdminModeration -> {
      BackHandler { currentScreen = Screen.Main(MainTab.PROFILE) }
      Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
      ) { innerPadding ->
        AdminModerationScreen(
          viewModel = viewModel,
          modifier = Modifier.padding(innerPadding)
        )
      }
    }

    is Screen.Communities -> {
      BackHandler { currentScreen = Screen.Main(MainTab.HOME) }
      Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
      ) { innerPadding ->
        CommunitiesScreen(
          viewModel = viewModel,
          modifier = Modifier.padding(innerPadding)
        )
      }
    }

    is Screen.Main -> {
      val activeTab = screen.activeTab

      Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
          NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.navigationBarsPadding()
          ) {
            MainTab.entries.forEach { tab ->
              val isSelected = activeTab == tab
              if (tab == MainTab.CREATE) {
                // Visually prominent Create action
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 6.dp),
                  contentAlignment = Alignment.Center
                ) {
                  FloatingActionButton(
                    onClick = { currentScreen = Screen.Main(MainTab.CREATE) },
                    containerColor = CampusBluePrimary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                    modifier = Modifier
                      .size(50.dp)
                      .testTag("nav_tab_create")
                  ) {
                    Icon(
                      imageVector = Icons.Default.Add,
                      contentDescription = "Create Event",
                      modifier = Modifier.size(28.dp)
                    )
                  }
                }
              } else {
                NavigationBarItem(
                  selected = isSelected,
                  onClick = { currentScreen = Screen.Main(tab) },
                  icon = {
                    Icon(
                      imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                      contentDescription = tab.label
                    )
                  },
                  label = {
                    Text(
                      text = tab.label,
                      fontSize = 11.sp
                    )
                  },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CampusBluePrimary,
                    selectedTextColor = CampusBluePrimary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                  ),
                  modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                )
              }
            }
          }
        },
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
      ) { innerPadding ->
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
          when (activeTab) {
            MainTab.HOME -> {
              HomeScreen(
                viewModel = viewModel,
                onEventSelected = { eventId -> currentScreen = Screen.EventDetail(eventId) },
                onNavigateToExplore = { currentScreen = Screen.Main(MainTab.EXPLORE) }
              )
            }
            MainTab.EXPLORE -> {
              ExploreScreen(
                viewModel = viewModel,
                onEventSelected = { eventId -> currentScreen = Screen.EventDetail(eventId) }
              )
            }
            MainTab.CREATE -> {
              EventCreateScreen(
                viewModel = viewModel,
                onEventCreated = { currentScreen = Screen.Main(MainTab.HOME) }
              )
            }
            MainTab.MAP -> {
              CampusMapScreen(
                viewModel = viewModel,
                onEventSelected = { eventId -> currentScreen = Screen.EventDetail(eventId) }
              )
            }
            MainTab.PROFILE -> {
              ProfileScreen(
                viewModel = viewModel,
                onEventSelected = { eventId -> currentScreen = Screen.EventDetail(eventId) },
                onNavigateToCheckIn = { currentScreen = Screen.CheckInScanner },
                onNavigateToAdmin = { currentScreen = Screen.AdminModeration },
                onNavigateToAuth = { currentScreen = Screen.Auth }
              )
            }
          }
        }
      }
    }
  }
}
