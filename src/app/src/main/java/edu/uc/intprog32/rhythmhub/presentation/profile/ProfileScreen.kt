package edu.uc.intprog32.rhythmhub.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import edu.uc.intprog32.rhythmhub.presentation.components.GradientBackground
import edu.uc.intprog32.rhythmhub.presentation.components.RhythmButton
import edu.uc.intprog32.rhythmhub.presentation.components.RhythmOutlinedButton
import edu.uc.intprog32.rhythmhub.presentation.components.RhythmTextField

/**
 * Profile/Settings screen where users can view and edit their profile. Displays DiceBear avatar,
 * username, and editable bio.
 *
 * @param onLogout Callback when user logs out
 * @param viewModel ViewModel for profile management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
        onLogout: () -> Unit,
        onNavigateToAdmin: () -> Unit = {},
        viewModel: ProfileViewModel
) {
        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        // Show success/error messages
        LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
                uiState.successMessage?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearMessages()
                }
                uiState.errorMessage?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearMessages()
                }
        }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text("Profile & Settings") },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                titleContentColor =
                                                        MaterialTheme.colorScheme.onPrimary
                                        )
                        )
                },
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
                GradientBackground {
                        if (uiState.isLoading && uiState.user == null) {
                                // Initial loading
                                Box(
                                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                                        contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator() }
                        } else {
                                Column(
                                        modifier =
                                                Modifier.fillMaxSize()
                                                        .verticalScroll(rememberScrollState())
                                                        .padding(paddingValues)
                                                        .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Avatar Section
                                        Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                elevation =
                                                        CardDefaults.cardElevation(
                                                                defaultElevation = 6.dp
                                                        ),
                                                colors =
                                                        CardDefaults.cardColors(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .surface
                                                        )
                                        ) {
                                                Column(
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .padding(24.dp),
                                                        horizontalAlignment =
                                                                Alignment.CenterHorizontally
                                                ) {
                                                        // Avatar Image
                                                        Box(
                                                                modifier =
                                                                        Modifier.size(120.dp)
                                                                                .clip(CircleShape)
                                                                                .background(
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primaryContainer
                                                                                ),
                                                                contentAlignment = Alignment.Center
                                                        ) {
                                                                uiState.user?.let { user ->
                                                                        AsyncImage(
                                                                                model =
                                                                                        user.getDisplayAvatarUrl(),
                                                                                contentDescription =
                                                                                        "Profile Avatar",
                                                                                modifier =
                                                                                        Modifier.size(
                                                                                                        120.dp
                                                                                                )
                                                                                                .clip(
                                                                                                        CircleShape
                                                                                                ),
                                                                                contentScale =
                                                                                        ContentScale
                                                                                                .Crop
                                                                        )
                                                                }
                                                        }

                                                        Spacer(modifier = Modifier.height(16.dp))

                                                        // Generate New Avatar Button
                                                        RhythmOutlinedButton(
                                                                text = "Generate New Avatar",
                                                                onClick = {
                                                                        viewModel
                                                                                .generateNewAvatar()
                                                                },
                                                                enabled = !uiState.isLoading
                                                        )

                                                        Spacer(modifier = Modifier.height(24.dp))

                                                        // Gamification Stats
                                                        uiState.user?.let { user ->
                                                                Text(
                                                                        text =
                                                                                "Level ${user.level}",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .headlineMedium,
                                                                        fontWeight =
                                                                                FontWeight.Bold,
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .primary
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        8.dp
                                                                                )
                                                                )

                                                                // Simple XP Ratio
                                                                val levelThreshold =
                                                                        user.level * 100
                                                                val progress =
                                                                        (user.xp.toFloat() /
                                                                                        levelThreshold
                                                                                                .toFloat())
                                                                                .coerceIn(0f, 1f)

                                                                LinearProgressIndicator(
                                                                        progress = progress,
                                                                        modifier =
                                                                                Modifier.fillMaxWidth()
                                                                                        .height(
                                                                                                12.dp
                                                                                        )
                                                                                        .clip(
                                                                                                RoundedCornerShape(
                                                                                                        6.dp
                                                                                                )
                                                                                        ),
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        4.dp
                                                                                )
                                                                )
                                                                Text(
                                                                        text =
                                                                                "${user.xp} / $levelThreshold XP",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodySmall,
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .onSurfaceVariant
                                                                )
                                                        }
                                                }
                                        }

                                        Spacer(modifier = Modifier.height(24.dp))

                                        // Profile Info Card
                                        Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                elevation =
                                                        CardDefaults.cardElevation(
                                                                defaultElevation = 6.dp
                                                        ),
                                                colors =
                                                        CardDefaults.cardColors(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .surface
                                                        )
                                        ) {
                                                Column(
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .padding(24.dp)
                                                ) {
                                                        Text(
                                                                text = "Profile Information",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleLarge,
                                                                fontWeight = FontWeight.Bold,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onSurface
                                                        )

                                                        Spacer(modifier = Modifier.height(16.dp))

                                                        // Username (readonly)
                                                        OutlinedTextField(
                                                                value = uiState.user?.username
                                                                                ?: "",
                                                                onValueChange = {},
                                                                label = { Text("Username") },
                                                                readOnly = true,
                                                                enabled = false,
                                                                modifier = Modifier.fillMaxWidth(),
                                                                colors =
                                                                        OutlinedTextFieldDefaults
                                                                                .colors(
                                                                                        disabledTextColor =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .onSurface,
                                                                                        disabledBorderColor =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .outline,
                                                                                        disabledLabelColor =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .onSurfaceVariant
                                                                                )
                                                        )

                                                        Spacer(modifier = Modifier.height(16.dp))

                                                        // Bio (editable)
                                                        if (uiState.isEditingBio) {
                                                                RhythmTextField(
                                                                        value = uiState.tempBio,
                                                                        onValueChange = {
                                                                                viewModel
                                                                                        .updateTempBio(
                                                                                                it
                                                                                        )
                                                                        },
                                                                        label = "Bio",
                                                                        enabled = !uiState.isLoading
                                                                )

                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        8.dp
                                                                                )
                                                                )

                                                                Row(
                                                                        modifier =
                                                                                Modifier.fillMaxWidth(),
                                                                        horizontalArrangement =
                                                                                Arrangement
                                                                                        .spacedBy(
                                                                                                8.dp
                                                                                        )
                                                                ) {
                                                                        RhythmOutlinedButton(
                                                                                text = "Cancel",
                                                                                onClick = {
                                                                                        viewModel
                                                                                                .cancelEditingBio()
                                                                                },
                                                                                modifier =
                                                                                        Modifier.weight(
                                                                                                1f
                                                                                        ),
                                                                                enabled =
                                                                                        !uiState.isLoading
                                                                        )
                                                                        RhythmButton(
                                                                                text = "Save",
                                                                                onClick = {
                                                                                        viewModel
                                                                                                .saveBio()
                                                                                },
                                                                                modifier =
                                                                                        Modifier.weight(
                                                                                                1f
                                                                                        ),
                                                                                enabled =
                                                                                        !uiState.isLoading
                                                                        )
                                                                }
                                                        } else {
                                                                OutlinedTextField(
                                                                        value = uiState.user?.bio
                                                                                        ?: "",
                                                                        onValueChange = {},
                                                                        label = { Text("Bio") },
                                                                        readOnly = true,
                                                                        enabled = false,
                                                                        modifier =
                                                                                Modifier.fillMaxWidth(),
                                                                        trailingIcon = {
                                                                                IconButton(
                                                                                        onClick = {
                                                                                                viewModel
                                                                                                        .startEditingBio()
                                                                                        }
                                                                                ) {
                                                                                        Icon(
                                                                                                Icons.Default
                                                                                                        .Edit,
                                                                                                contentDescription =
                                                                                                        "Edit"
                                                                                        )
                                                                                }
                                                                        },
                                                                        colors =
                                                                                OutlinedTextFieldDefaults
                                                                                        .colors(
                                                                                                disabledTextColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onSurface,
                                                                                                disabledBorderColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .outline,
                                                                                                disabledLabelColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onSurfaceVariant
                                                                                        )
                                                                )
                                                        }

                                                        Spacer(modifier = Modifier.height(16.dp))

                                                        // Role Badge
                                                        if (uiState.user?.isAdmin == true) {
                                                                Card(
                                                                        colors =
                                                                                CardDefaults
                                                                                        .cardColors(
                                                                                                containerColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .primaryContainer
                                                                                        )
                                                                ) {
                                                                        Row(
                                                                                modifier =
                                                                                        Modifier.padding(
                                                                                                12.dp
                                                                                        ),
                                                                                verticalAlignment =
                                                                                        Alignment
                                                                                                .CenterVertically
                                                                        ) {
                                                                                Icon(
                                                                                        Icons.Default
                                                                                                .Shield,
                                                                                        contentDescription =
                                                                                                "Admin",
                                                                                        tint =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .primary
                                                                                )
                                                                                Spacer(
                                                                                        modifier =
                                                                                                Modifier.width(
                                                                                                        8.dp
                                                                                                )
                                                                                )
                                                                                Text(
                                                                                        text =
                                                                                                "Administrator",
                                                                                        color =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .onPrimaryContainer,
                                                                                        fontWeight =
                                                                                                FontWeight
                                                                                                        .Bold
                                                                                )
                                                                        }
                                                                }

                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        12.dp
                                                                                )
                                                                )

                                                                // Admin Dashboard Button
                                                                RhythmButton(
                                                                        text = "Admin Dashboard",
                                                                        onClick = onNavigateToAdmin,
                                                                        modifier =
                                                                                Modifier.fillMaxWidth()
                                                                )
                                                        }
                                                }
                                        }

                                        Spacer(modifier = Modifier.height(32.dp))

                                        // Logout Button
                                        RhythmOutlinedButton(
                                                text = "Logout",
                                                onClick = onLogout,
                                                modifier = Modifier.fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))
                                }
                        }
                }
        }
}

