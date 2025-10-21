package edu.uc.intprog32.escarro.myapplication.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.uc.intprog32.escarro.myapplication.data.repository.UserRepository
import edu.uc.intprog32.escarro.myapplication.presentation.auth.AuthViewModel
import edu.uc.intprog32.escarro.myapplication.presentation.auth.LoginScreen
import edu.uc.intprog32.escarro.myapplication.presentation.auth.RegisterScreen
import edu.uc.intprog32.escarro.myapplication.presentation.home.HomeScreen
import edu.uc.intprog32.escarro.myapplication.presentation.home.HomeViewModel
import edu.uc.intprog32.escarro.myapplication.presentation.onboarding.OnboardingScreen
import edu.uc.intprog32.escarro.myapplication.presentation.onboarding.OnboardingViewModel

/**
 * Navigation routes for the RhythmHub app.
 */
object RhythmRoutes {
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
}

/**
 * Main navigation graph for RhythmHub application.
 * Implements proper screen navigation as required by the assignment.
 *
 * Uses Navigation Compose to handle transitions between:
 * - Onboarding (first launch only)
 * - Login
 * - Registration
 * - Home/Dashboard
 *
 * @param navController Navigation controller
 * @param context Application context for Repository initialization
 * @param startDestination Initial destination based on app state
 */
@Composable
fun RhythmNavGraph(
    navController: NavHostController,
    context: Context,
    startDestination: String
) {
    // Initialize repository (uses SharedPreferences as required)
    val userRepository = UserRepository(context)

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding Screen
        composable(RhythmRoutes.ONBOARDING) {
            val viewModel: OnboardingViewModel = viewModel(
                factory = OnboardingViewModel.Factory(userRepository)
            )

            OnboardingScreen(
                onContinue = {
                    navController.navigate(RhythmRoutes.LOGIN) {
                        popUpTo(RhythmRoutes.ONBOARDING) { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }

        // Login Screen
        composable(RhythmRoutes.LOGIN) {
            val viewModel: AuthViewModel = viewModel(
                factory = AuthViewModel.Factory(userRepository)
            )

            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(RhythmRoutes.HOME) {
                        popUpTo(RhythmRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(RhythmRoutes.REGISTER)
                },
                viewModel = viewModel
            )
        }

        // Registration Screen
        composable(RhythmRoutes.REGISTER) {
            val viewModel: AuthViewModel = viewModel(
                factory = AuthViewModel.Factory(userRepository)
            )

            RegisterScreen(
                onRegistrationSuccess = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }

        // Home/Dashboard Screen
        composable(RhythmRoutes.HOME) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(userRepository)
            )

            HomeScreen(
                onLogout = {
                    navController.navigate(RhythmRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }
    }
}

/**
 * Determines the start destination based on app state.
 * Checks SharedPreferences for first launch and auto-login state.
 *
 * @param context Application context
 * @return The appropriate start destination route
 */
fun getStartDestination(context: Context): String {
    val userRepository = UserRepository(context)

    return when {
        // First launch - show onboarding
        userRepository.isFirstLaunch() -> RhythmRoutes.ONBOARDING

        // Auto-login if "Remember Me" is enabled and user exists
        userRepository.shouldAutoLogin() -> RhythmRoutes.HOME

        // Default to login
        else -> RhythmRoutes.LOGIN
    }
}
