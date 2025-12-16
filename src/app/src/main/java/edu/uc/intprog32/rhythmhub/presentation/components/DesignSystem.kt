package edu.uc.intprog32.rhythmhub.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import edu.uc.intprog32.rhythmhub.presentation.theme.*

/** A Card with a Neon Glow effect, suited for the Maimai arcade aesthetic. */
@Composable
fun NeonCard(
        modifier: Modifier = Modifier,
        glowColor: Color = RhythmPink,
        content: @Composable ColumnScope.() -> Unit
) {
        Card(
                modifier =
                        modifier.shadow(
                                        elevation = 16.dp,
                                        shape = RoundedCornerShape(16.dp),
                                        ambientColor = glowColor.copy(alpha = 0.5f),
                                        spotColor = glowColor.copy(alpha = 0.8f)
                                )
                                .border(
                                        width = 1.dp,
                                        brush =
                                                Brush.linearGradient(
                                                        colors =
                                                                listOf(
                                                                        glowColor.copy(
                                                                                alpha = 0.7f
                                                                        ),
                                                                        glowColor.copy(alpha = 0.2f)
                                                                )
                                                ),
                                        shape = RoundedCornerShape(16.dp)
                                ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.85f))
        ) { content() }
}

/** A Glassmorphic Card for a modern, premium feel. */
@Composable
fun GlassCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
        Card(
                modifier =
                        modifier.border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(20.dp)
                        ),
                shape = RoundedCornerShape(20.dp),
                colors =
                        CardDefaults.cardColors(
                                containerColor = BackgroundDarkElevated.copy(alpha = 0.7f)
                        )
        ) { content() }
}

/** A vibrant, attention-grabbing button for primary actions like "Join Queue". */
@Composable
fun CyberButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        icon: @Composable (() -> Unit)? = null
) {
        Button(
                onClick = onClick,
                modifier =
                        modifier.height(56.dp)
                                .shadow(8.dp, RoundedCornerShape(28.dp), ambientColor = RhythmPink),
                enabled = enabled,
                shape = RoundedCornerShape(28.dp),
                colors =
                        ButtonDefaults.buttonColors(
                                containerColor = RhythmPink,
                                contentColor = Color.White,
                                disabledContainerColor = SurfaceDark,
                                disabledContentColor = TextSecondary
                        ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
        ) {
                if (icon != null) {
                        icon()
                        Spacer(Modifier.width(8.dp))
                }
                Text(
                        text = text,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                )
        }
}

/** Status Chip for Queue status (e.g., "In Queue", "Your Turn!") */
@Composable
fun StatusChip(text: String, color: Color = RhythmCyan, modifier: Modifier = Modifier) {
        Surface(
                modifier = modifier,
                shape = RoundedCornerShape(16.dp),
                color = color.copy(alpha = 0.2f),
                border =
                        ButtonDefaults.outlinedButtonBorder(enabled = true)
                                .copy(width = 1.dp) // Placeholder, will use border
        ) {
                Box(
                        modifier =
                                Modifier.border(
                                                1.dp,
                                                color.copy(alpha = 0.5f),
                                                RoundedCornerShape(16.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                        Text(
                                text = text,
                                color = color,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                        )
                }
        }
}

/** An Animated Pulsing Dot to indicate live status. */
@Composable
fun PulsingDot(color: Color = SuccessColor, size: Dp = 10.dp) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val alpha by
                infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 0.3f,
                        animationSpec =
                                infiniteRepeatable(
                                        animation = tween(800, easing = LinearEasing),
                                        repeatMode = RepeatMode.Reverse
                                ),
                        label = "alpha"
                )
        Box(
                modifier =
                        Modifier.size(size)
                                .clip(RoundedCornerShape(50))
                                .background(color.copy(alpha = alpha))
        )
}

/** Section Header for organizing content. */
@Composable
fun SectionHeader(
        title: String,
        modifier: Modifier = Modifier,
        action: @Composable (() -> Unit)? = null
) {
        Row(
                modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
                Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                )
                action?.invoke()
        }
}

/** Premium Header bar with gradient background and optional back button. */
@Composable
fun RhythmHeader(title: String, modifier: Modifier = Modifier, onBack: (() -> Unit)? = null) {
        Row(
                modifier =
                        modifier.fillMaxWidth()
                                .background(
                                        brush =
                                                Brush.horizontalGradient(
                                                        colors =
                                                                listOf(
                                                                        RhythmPurpleDark,
                                                                        RhythmPinkDark
                                                                )
                                                )
                                )
                                .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                if (onBack != null) {
                        IconButton(onClick = onBack) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = TextPrimary
                                )
                        }
                } else {
                        Spacer(Modifier.width(8.dp))
                }
                Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                )
        }
}
