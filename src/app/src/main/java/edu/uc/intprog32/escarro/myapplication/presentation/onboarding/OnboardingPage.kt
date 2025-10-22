package edu.uc.intprog32.escarro.myapplication.presentation.onboarding

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data class representing a single onboarding page.
 *
 * @property title The title of the onboarding page
 * @property description The description text
 * @property icon The icon to display
 */
data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector
)
