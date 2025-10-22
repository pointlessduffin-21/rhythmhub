package edu.uc.intprog32.escarro.myapplication.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Dark color scheme for RhythmHub following Maimai's vibrant arcade aesthetic.
 * Optimized for OLED displays with deep blacks and vibrant accents.
 */
private val DarkColorScheme = darkColorScheme(
    primary = RhythmPink,
    onPrimary = TextOnPrimary,
    primaryContainer = RhythmPinkDark,
    onPrimaryContainer = TextPrimary,

    secondary = RhythmCyan,
    onSecondary = BackgroundDark,
    secondaryContainer = RhythmCyanDark,
    onSecondaryContainer = TextPrimary,

    tertiary = RhythmPurple,
    onTertiary = TextOnPrimary,
    tertiaryContainer = RhythmPurpleDark,
    onTertiaryContainer = TextPrimary,

    background = BackgroundDark,
    onBackground = TextPrimary,

    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceDarkElevated,
    onSurfaceVariant = TextSecondary,

    error = ErrorColor,
    onError = TextOnPrimary
)

/**
 * Light color scheme for RhythmHub.
 * Maintains vibrant Maimai colors while being suitable for daytime use.
 */
private val LightColorScheme = lightColorScheme(
    primary = RhythmPink,
    onPrimary = TextOnPrimary,
    primaryContainer = RhythmPinkLight,
    onPrimaryContainer = BackgroundDark,

    secondary = RhythmCyan,
    onSecondary = TextOnPrimary,
    secondaryContainer = RhythmCyanLight,
    onSecondaryContainer = BackgroundDark,

    tertiary = RhythmPurple,
    onTertiary = TextOnPrimary,
    tertiaryContainer = RhythmPurpleLight,
    onTertiaryContainer = BackgroundDark,

    background = BackgroundLight,
    onBackground = BackgroundDark,

    surface = SurfaceLight,
    onSurface = BackgroundDark,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = TextSecondary,

    error = ErrorColor,
    onError = TextOnPrimary
)

/**
 * Main theme composable for RhythmHub application.
 * Applies Material 3 theming with custom Maimai-inspired colors and typography.
 *
 * @param darkTheme Whether to use dark theme (defaults to system setting)
 * @param dynamicColor Whether to use dynamic color (disabled for consistent branding)
 * @param content The composable content to wrap with the theme
 */
@Composable
fun RhythmHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled for consistent branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Dynamic color is disabled to maintain Maimai branding
        // If needed in future, can be enabled with dynamic color check
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RhythmTypography,
        content = content
    )
}
