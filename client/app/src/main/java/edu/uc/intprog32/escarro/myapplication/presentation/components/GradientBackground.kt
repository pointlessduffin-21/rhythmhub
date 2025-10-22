package edu.uc.intprog32.escarro.myapplication.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import edu.uc.intprog32.escarro.myapplication.presentation.theme.BackgroundDark
import edu.uc.intprog32.escarro.myapplication.presentation.theme.RhythmPink
import edu.uc.intprog32.escarro.myapplication.presentation.theme.RhythmPurple

/**
 * Creates a gradient background suitable for the RhythmHub app.
 * Features diagonal gradient with Maimai-inspired colors.
 *
 * @param content The content to display over the gradient background
 */
@Composable
fun GradientBackground(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BackgroundDark,
                        RhythmPurple.copy(alpha = 0.2f),
                        RhythmPink.copy(alpha = 0.1f),
                        BackgroundDark
                    )
                )
            )
    ) {
        content()
    }
}

/**
 * Creates a vibrant diagonal gradient background.
 * More colorful variant for special screens like onboarding.
 *
 * @param content The content to display over the gradient background
 */
@Composable
fun VibrantGradientBackground(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        RhythmPurple.copy(alpha = 0.3f),
                        RhythmPink.copy(alpha = 0.3f),
                        BackgroundDark,
                        BackgroundDark
                    )
                )
            )
    ) {
        content()
    }
}
