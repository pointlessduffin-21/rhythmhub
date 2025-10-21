package edu.uc.intprog32.escarro.myapplication.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.uc.intprog32.escarro.myapplication.presentation.components.GradientBackground
import edu.uc.intprog32.escarro.myapplication.presentation.components.RhythmButton
import edu.uc.intprog32.escarro.myapplication.presentation.components.VibrantGradientBackground

/**
 * Onboarding screen that introduces users to RhythmHub.
 * Displayed on first launch to explain app features.
 *
 * Implements MVP Feature 1: User Onboarding & Management requirement.
 *
 * @param onContinue Callback when user completes onboarding
 * @param viewModel ViewModel for onboarding logic
 */
@Composable
fun OnboardingScreen(
    onContinue: () -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    VibrantGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Icon/Logo - Album represents music/rhythm games
            Icon(
                imageVector = Icons.Default.Album,
                contentDescription = "RhythmHub Logo",
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // App Name
            Text(
                text = "RhythmHub",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tagline
            Text(
                text = "Your Maimai Queue Manager",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Feature 1
            OnboardingFeature(
                title = "Join Digital Queues",
                description = "See your position in real-time and get notified when it's your turn"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Feature 2
            OnboardingFeature(
                title = "Find Arcades",
                description = "Locate nearby arcades with Maimai machines"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Feature 3
            OnboardingFeature(
                title = "Connect with Players",
                description = "Share scores and chat with the local Maimai community"
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Continue Button
            RhythmButton(
                text = "Get Started",
                onClick = {
                    viewModel.completeOnboarding()
                    onContinue()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Individual feature item for onboarding screen.
 *
 * @param title Feature title
 * @param description Feature description
 */
@Composable
private fun OnboardingFeature(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "• $title",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
