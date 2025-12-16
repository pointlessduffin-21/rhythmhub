package edu.uc.intprog32.rhythmhub.presentation.queue

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import edu.uc.intprog32.rhythmhub.data.model.QueueItem
import edu.uc.intprog32.rhythmhub.presentation.components.AIAdvisorCard
import edu.uc.intprog32.rhythmhub.presentation.components.GradientBackground
import edu.uc.intprog32.rhythmhub.presentation.components.RhythmHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueScreen(viewModel: QueueViewModel = viewModel(), onNavigateBack: () -> Unit = {}) {
    val uiState by viewModel.uiState.collectAsState()
    val message by viewModel.message.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    // Show Toast messages
    androidx.compose.runtime.LaunchedEffect(message) {
        message?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Scaffold(
            bottomBar = {
                QueueBottomBar(
                        isUserInQueue = uiState.isUserInQueue,
                        isFirstInQueue = uiState.isFirstInQueue,
                        onJoin = { viewModel.joinQueue(false) },
                        onLeave = { viewModel.leaveQueue() },
                        onPlayNow = { viewModel.startPlaying() },
                        onDonePlaying = { viewModel.finishPlaying() }
                )
            }
    ) { paddingValues ->
        GradientBackground {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                RhythmHeader(title = "Live Queue", onBack = onNavigateBack)

                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    // RhythmAI Advisor
                    AIAdvisorCard(
                            estimatedWaitMins = uiState.estimatedWaitMins,
                            crowdLevel = uiState.crowdLevel,
                            advice = uiState.aiAdvice
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                            text = "Current Queue",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        itemsIndexed(uiState.queue) { index, item ->
                            QueueItemRow(index = index + 1, item = item)
                        }
                        if (uiState.queue.isEmpty()) {
                            item {
                                Box(
                                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                                        contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                            text = "Queue is empty. Be the first!",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color =
                                                    MaterialTheme.colorScheme.onBackground.copy(
                                                            alpha = 0.6f
                                                    )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QueueItemRow(index: Int, item: QueueItem) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            // Position Badge
            Box(
                    modifier =
                            Modifier.size(32.dp)
                                    .background(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            CircleShape
                                    ),
                    contentAlignment = Alignment.Center
            ) {
                Text(
                        text = "#$index",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Avatar
            AsyncImage(
                    model = item.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                        text = item.username,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                )
                Text(
                        text = if (item.isTwoPlayer) "2 Players" else "1 Player",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun QueueBottomBar(
        isUserInQueue: Boolean,
        isFirstInQueue: Boolean,
        onJoin: () -> Unit,
        onLeave: () -> Unit,
        onPlayNow: () -> Unit,
        onDonePlaying: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            if (isFirstInQueue) {
                // User is first - show Play Now!
                Button(
                        onClick = onPlayNow,
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.tertiary
                                )
                ) {
                    Text(
                            text = "🎮 PLAY NOW - It's Your Turn!",
                            style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = onDonePlaying, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Done Playing", style = MaterialTheme.typography.bodyMedium)
                }
            } else if (isUserInQueue) {
                // User is in queue but not first
                Button(
                        onClick = onLeave,
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                )
                ) { Text(text = "Leave Queue", style = MaterialTheme.typography.titleMedium) }
            } else {
                // User not in queue
                Button(
                        onClick = onJoin,
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                )
                ) { Text(text = "Join Queue", style = MaterialTheme.typography.titleMedium) }
            }
        }
    }
}
