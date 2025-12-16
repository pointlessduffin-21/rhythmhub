package edu.uc.intprog32.rhythmhub.presentation.community

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.uc.intprog32.rhythmhub.data.model.Arcade
import edu.uc.intprog32.rhythmhub.data.model.Post
import edu.uc.intprog32.rhythmhub.data.repository.UserRepository
import edu.uc.intprog32.rhythmhub.presentation.components.GradientBackground
import edu.uc.intprog32.rhythmhub.presentation.components.RhythmHeader
import edu.uc.intprog32.rhythmhub.presentation.theme.RhythmCyan
import edu.uc.intprog32.rhythmhub.presentation.theme.RhythmGreen
import edu.uc.intprog32.rhythmhub.presentation.theme.RhythmPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
        userRepository: UserRepository,
        viewModel: CommunityViewModel =
                viewModel<CommunityViewModel>(factory = CommunityViewModel.Factory(userRepository))
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                        onClick = { showDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary
                ) { Icon(Icons.Default.Add, contentDescription = "Add Post") }
            }
    ) { paddingValues ->
        GradientBackground {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                // Premium Header
                RhythmHeader(title = "Community Hub")

                // Arcade Filter Chips
                ArcadeFilterRow(
                        arcades = uiState.arcadeFilters,
                        selectedArcadeId = uiState.selectedArcadeId,
                        onSelectArcade = { viewModel.selectArcadeFilter(it) }
                )

                LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (uiState.posts.isEmpty()) {
                        item {
                            Box(
                                    modifier = Modifier.fillParentMaxSize(),
                                    contentAlignment = Alignment.Center
                            ) {
                                Text(
                                        "No posts yet. Be the first!",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(items = uiState.posts) { post ->
                            PostItem(
                                    post = post,
                                    onUpvote = { viewModel.upvotePost(post.id) },
                                    onDownvote = { viewModel.downvotePost(post.id) }
                            )
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AddPostDialog(
                    arcades = uiState.arcadeFilters,
                    onDismiss = { showDialog = false },
                    onPost = { content, arcadeId, arcadeName ->
                        viewModel.addPost(content, arcadeId, arcadeName)
                        showDialog = false
                    }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArcadeFilterRow(
        arcades: List<Arcade>,
        selectedArcadeId: String?,
        onSelectArcade: (String?) -> Unit
) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
                selected = selectedArcadeId == null,
                onClick = { onSelectArcade(null) },
                label = { Text("All") }
        )
        arcades.forEach { arcade ->
            FilterChip(
                    selected = selectedArcadeId == arcade.id,
                    onClick = { onSelectArcade(arcade.id) },
                    label = { Text(arcade.name.split(" ").take(2).joinToString(" ")) }
            )
        }
    }
}

@Composable
fun PostItem(post: Post, onUpvote: () -> Unit = {}, onDownvote: () -> Unit = {}) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                        modifier =
                                Modifier.size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                ) {
                    Text(
                            text = post.authorName.first().uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                            text = post.authorName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                    )
                    Text(
                            text =
                                    "${(System.currentTimeMillis() - post.timestamp) / 60000} mins ago",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // Arcade Tag
                if (post.arcadeName.isNotBlank() && post.arcadeName != "General") {
                    Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = RhythmCyan.copy(alpha = 0.15f)
                    ) {
                        Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = RhythmCyan
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                    post.arcadeName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = RhythmCyan
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content
            Text(text = post.content, style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(12.dp))

            // Voting Actions
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onUpvote) {
                    Icon(
                            Icons.Default.KeyboardArrowUp,
                            contentDescription = "Upvote",
                            modifier = Modifier.size(24.dp),
                            tint = RhythmGreen
                    )
                }
                Text(
                        text = "${post.likes}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDownvote) {
                    Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = "Downvote",
                            modifier = Modifier.size(24.dp),
                            tint = RhythmPink
                    )
                }
            }
        }
    }
}

@Composable
fun AddPostDialog(
        arcades: List<Arcade>,
        onDismiss: () -> Unit,
        onPost: (String, String?, String?) -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    var selectedArcade by remember { mutableStateOf<Arcade?>(null) }

    AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("New Post") },
            text = {
                Column {
                    OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            label = { Text("What's on your mind?") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("Tag an Arcade (Optional)", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(8.dp))
                    Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                                selected = selectedArcade == null,
                                onClick = { selectedArcade = null },
                                label = { Text("General") }
                        )
                        arcades.forEach { arcade ->
                            FilterChip(
                                    selected = selectedArcade?.id == arcade.id,
                                    onClick = { selectedArcade = arcade },
                                    label = {
                                        Text(arcade.name.split(" ").take(2).joinToString(" "))
                                    }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                        onClick = {
                            if (text.text.isNotBlank()) {
                                onPost(
                                        text.text,
                                        selectedArcade?.id,
                                        selectedArcade?.name ?: "General"
                                )
                            }
                        }
                ) { Text("Post") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
