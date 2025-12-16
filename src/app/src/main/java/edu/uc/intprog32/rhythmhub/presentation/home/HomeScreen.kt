package edu.uc.intprog32.rhythmhub.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import edu.uc.intprog32.rhythmhub.presentation.components.*
import edu.uc.intprog32.rhythmhub.presentation.theme.*

/** Redesigned Home/Dashboard screen with premium Maimai aesthetic. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
        onLogout: () -> Unit,
        onNavigateToArcades: () -> Unit = {},
        onNavigateToCommunity: () -> Unit = {},
        viewModel: HomeViewModel = viewModel()
) {
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(Unit) { viewModel.loadUserData() }

        GradientBackground {
                if (uiState.isLoading) {
                        Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = RhythmPink) }
                } else {
                        Column(
                                modifier =
                                        Modifier.fillMaxSize()
                                                .verticalScroll(rememberScrollState())
                                                .padding(16.dp)
                        ) {
                                // Top Header
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                        Column {
                                                Text(
                                                        text = "Welcome back,",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = TextSecondary
                                                )
                                                Text(
                                                        text = uiState.user?.username
                                                                        ?: "Rhythm Player",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .headlineMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextPrimary
                                                )
                                        }
                                        // Avatar
                                        Box(
                                                modifier =
                                                        Modifier.size(56.dp)
                                                                .clip(CircleShape)
                                                                .background(
                                                                        RhythmPink.copy(
                                                                                alpha = 0.3f
                                                                        )
                                                                ),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                uiState.user?.let { user ->
                                                        AsyncImage(
                                                                model = user.getDisplayAvatarUrl(),
                                                                contentDescription = "Avatar",
                                                                modifier =
                                                                        Modifier.size(56.dp)
                                                                                .clip(CircleShape),
                                                                contentScale = ContentScale.Crop
                                                        )
                                                }
                                        }
                                }

                                Spacer(Modifier.height(24.dp))

                                // Level & XP Card
                                uiState.user?.let { user ->
                                        NeonCard(
                                                glowColor = RhythmCyan,
                                                modifier = Modifier.fillMaxWidth()
                                        ) {
                                                Column(modifier = Modifier.padding(16.dp)) {
                                                        Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement =
                                                                        Arrangement.SpaceBetween,
                                                                verticalAlignment =
                                                                        Alignment.CenterVertically
                                                        ) {
                                                                Text(
                                                                        text =
                                                                                "Level ${user.level}",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .titleLarge,
                                                                        fontWeight =
                                                                                FontWeight.Bold,
                                                                        color = RhythmCyan
                                                                )
                                                                StatusChip(
                                                                        text = "Rhythm Player",
                                                                        color = RhythmPink
                                                                )
                                                        }
                                                        Spacer(Modifier.height(12.dp))
                                                        val levelThreshold = user.level * 100
                                                        val progress =
                                                                (user.xp.toFloat() /
                                                                                levelThreshold
                                                                                        .toFloat())
                                                                        .coerceIn(0f, 1f)
                                                        LinearProgressIndicator(
                                                                progress = { progress },
                                                                modifier =
                                                                        Modifier.fillMaxWidth()
                                                                                .height(10.dp)
                                                                                .clip(
                                                                                        RoundedCornerShape(
                                                                                                5.dp
                                                                                        )
                                                                                ),
                                                                color = RhythmCyan,
                                                                trackColor = SurfaceDark
                                                        )
                                                        Spacer(Modifier.height(6.dp))
                                                        Text(
                                                                text =
                                                                        "${user.xp} / $levelThreshold XP to level ${user.level + 1}",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodySmall,
                                                                color = TextSecondary
                                                        )
                                                }
                                        }
                                }

                                Spacer(Modifier.height(24.dp))

                                // Quick Actions
                                SectionHeader(title = "Quick Actions")
                                Spacer(Modifier.height(12.dp))

                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                        QuickActionCard(
                                                icon = Icons.Default.QueuePlayNext,
                                                title = "Find Arcade",
                                                subtitle = "View queues",
                                                modifier = Modifier.weight(1f),
                                                onClick = onNavigateToArcades
                                        )
                                        QuickActionCard(
                                                icon = Icons.Default.People,
                                                title = "Community",
                                                subtitle = "See posts",
                                                modifier = Modifier.weight(1f),
                                                onClick = onNavigateToCommunity
                                        )
                                }

                                Spacer(Modifier.height(24.dp))

                                // Tips Card
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                        Column(Modifier.padding(16.dp)) {
                                                Row(
                                                        verticalAlignment =
                                                                Alignment.CenterVertically
                                                ) {
                                                        Icon(
                                                                Icons.Default.Lightbulb,
                                                                contentDescription = null,
                                                                tint = RhythmYellow
                                                        )
                                                        Spacer(Modifier.width(8.dp))
                                                        Text(
                                                                "Pro Tip",
                                                                fontWeight = FontWeight.Bold,
                                                                color = RhythmYellow
                                                        )
                                                }
                                                Spacer(Modifier.height(8.dp))
                                                Text(
                                                        text =
                                                                "Check in at an arcade to earn +50 XP! The queue system is community-driven, so be a good sport and update your status.",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = TextSecondary
                                                )
                                        }
                                }

                                Spacer(Modifier.height(32.dp))

                                // Logout
                                RhythmOutlinedButton(
                                        text = "Logout",
                                        onClick = {
                                                viewModel.logout()
                                                onLogout()
                                        }
                                )
                                Spacer(Modifier.height(24.dp))
                        }
                }
        }
}

@Composable
fun QuickActionCard(
        icon: ImageVector,
        title: String,
        subtitle: String,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
) {
        Card(
                onClick = onClick,
                modifier = modifier.height(100.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
                Column(
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        verticalArrangement = Arrangement.Center
                ) {
                        Icon(
                                icon,
                                contentDescription = null,
                                tint = RhythmPink,
                                modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                                title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                        )
                        Text(
                                subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                        )
                }
        }
}
