package edu.uc.intprog32.escarro.myapplication.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.uc.intprog32.escarro.myapplication.data.repository.UserRepository
import edu.uc.intprog32.escarro.myapplication.presentation.arcades.ArcadesScreen
import edu.uc.intprog32.escarro.myapplication.presentation.community.CommunityScreen
import edu.uc.intprog32.escarro.myapplication.presentation.home.HomeScreen
import edu.uc.intprog32.escarro.myapplication.presentation.home.HomeViewModel
import edu.uc.intprog32.escarro.myapplication.presentation.profile.ProfileScreen
import edu.uc.intprog32.escarro.myapplication.presentation.profile.ProfileViewModel
import androidx.compose.ui.unit.dp

/**
 * Navigation items for bottom navigation bar.
 */
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    object Arcades : BottomNavItem(
        route = "arcades",
        title = "Arcades",
        selectedIcon = Icons.Filled.LocationOn,
        unselectedIcon = Icons.Outlined.LocationOn
    )

    object Community : BottomNavItem(
        route = "community",
        title = "Community",
        selectedIcon = Icons.Filled.People,
        unselectedIcon = Icons.Outlined.People
    )

    object Profile : BottomNavItem(
        route = "profile",
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}

/**
 * Main container screen with bottom navigation.
 * Displays Home, Arcades, Community, and Profile tabs.
 *
 * @param onLogout Callback when user logs out
 * @param userRepository Repository for user data operations
 */
@Composable
fun MainScreen(
    onLogout: () -> Unit,
    userRepository: UserRepository
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val navItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Arcades,
        BottomNavItem.Community,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 8.dp
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == index) {
                                    item.selectedIcon
                                } else {
                                    item.unselectedIcon
                                },
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        // Content based on selected tab
        when (selectedTab) {
            0 -> {
                // Home Screen
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.Factory(userRepository)
                )
                HomeScreen(
                    onLogout = onLogout,
                    viewModel = homeViewModel
                )
            }
            1 -> {
                // Arcades Screen
                ArcadesScreen()
            }
            2 -> {
                // Community Screen
                CommunityScreen()
            }
            3 -> {
                // Profile Screen
                val profileViewModel: ProfileViewModel = viewModel(
                    factory = ProfileViewModel.Factory(userRepository)
                )
                ProfileScreen(
                    onLogout = onLogout,
                    viewModel = profileViewModel
                )
            }
        }
    }
}
