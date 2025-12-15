package edu.uc.intprog32.escarro.myapplication.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import edu.uc.intprog32.escarro.myapplication.data.model.User
import edu.uc.intprog32.escarro.myapplication.data.repository.UserRepository
import edu.uc.intprog32.escarro.myapplication.presentation.components.GradientBackground

/**
 * Admin Dashboard screen for managing users and viewing system statistics. Only accessible to users
 * with isAdmin = true.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
        userRepository: UserRepository,
        onNavigateBack: () -> Unit,
        viewModel: AdminViewModel =
                viewModel<AdminViewModel>(factory = AdminViewModel.Factory(userRepository))
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show messages
    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Admin Dashboard") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        },
                        actions = {
                            IconButton(onClick = { viewModel.loadAdminData() }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                            }
                        },
                        colors =
                                TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                                        navigationIconContentColor =
                                                MaterialTheme.colorScheme.onPrimary,
                                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                                )
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        GradientBackground {
            if (uiState.isLoading) {
                Box(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            } else {
                LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Environment Badge
                    item { EnvironmentBadge(environment = uiState.environment) }

                    // Stats Cards
                    item {
                        StatsSection(
                                totalUsers = uiState.totalUsers,
                                checkInsToday = uiState.totalCheckInsToday
                        )
                    }

                    // Users Section Header
                    item {
                        Text(
                                text = "Users (${uiState.users.size})",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // User List
                    items(uiState.users) { user ->
                        UserCard(user = user, onToggleAdmin = { viewModel.toggleUserAdmin(user) })
                    }
                }
            }
        }
    }
}

@Composable
fun EnvironmentBadge(environment: String) {
    val (backgroundColor, textColor) =
            if (environment == "DEV") {
                MaterialTheme.colorScheme.tertiaryContainer to
                        MaterialTheme.colorScheme.onTertiaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer to
                        MaterialTheme.colorScheme.onErrorContainer
            }

    Card(
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            shape = RoundedCornerShape(8.dp)
    ) {
        Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                    imageVector =
                            if (environment == "DEV") Icons.Default.Build
                            else Icons.Default.Verified,
                    contentDescription = null,
                    tint = textColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                    text = "Environment: $environment",
                    color = textColor,
                    fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StatsSection(totalUsers: Int, checkInsToday: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.People,
                label = "Total Users",
                value = totalUsers.toString()
        )
        StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.LocationOn,
                label = "Check-ins Today",
                value = checkInsToday.toString()
        )
    }
}

@Composable
fun StatCard(
        modifier: Modifier = Modifier,
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        label: String,
        value: String
) {
    Card(
            modifier = modifier,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
            )
            Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun UserCard(user: User, onToggleAdmin: () -> Unit) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            AsyncImage(
                    model = user.getAvatarUrl(),
                    contentDescription = "Avatar",
                    modifier = Modifier.size(48.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // User Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                            text = user.username,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                    )
                    if (user.isAdmin) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Admin",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Row {
                    Text(
                            text = "Level ${user.level} • ${user.xp} XP",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Admin Toggle
            Switch(checked = user.isAdmin, onCheckedChange = { onToggleAdmin() })
        }
    }
}
