package edu.uc.intprog32.rhythmhub.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.uc.intprog32.rhythmhub.data.repository.UserRepository
import edu.uc.intprog32.rhythmhub.presentation.admin.AdminScreen
import edu.uc.intprog32.rhythmhub.presentation.auth.AuthViewModel
import edu.uc.intprog32.rhythmhub.presentation.auth.LoginScreen
import edu.uc.intprog32.rhythmhub.presentation.auth.RegisterScreen
import edu.uc.intprog32.rhythmhub.presentation.main.MainScreen
import edu.uc.intprog32.rhythmhub.presentation.onboarding.OnboardingScreen
import edu.uc.intprog32.rhythmhub.presentation.onboarding.OnboardingViewModel
import edu.uc.intprog32.rhythmhub.presentation.queue.QueueScreen
import kotlinx.coroutines.runBlocking

/** Navigation routes for the RhythmHub app. */
object RhythmRoutes {
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val QUEUE_BASE = "queue"
    const val QUEUE = "$QUEUE_BASE/{arcadeId}"
    const val ADMIN = "admin"
}

/**
 * Main navigation graph for RhythmHub application. Implements proper screen navigation
 *
 * Uses Navigation Compose to handle transitions between:
 * - Onboarding (first launch only)
 * - Login
 * - Registration
 * - Main Screen with Bottom Navigation:
 * - Home/Dashboard
 * - Arcade Locator (placeholder)
 * - Community Hub (placeholder)
 * - Profile/Settings
 *
 * @param navController Navigation controller
 * @param context Application context for Repository initialization
 * @param startDestination Initial destination based on app state
 */
@Composable
fun RhythmNavGraph(navController: NavHostController, context: Context, startDestination: String) {
    // Initialize repository (uses SharedPreferences as required)
    val userRepository = UserRepository(context)

    NavHost(navController = navController, startDestination = startDestination) {
        // Onboarding Screen
        composable(RhythmRoutes.ONBOARDING) {
            val viewModel: OnboardingViewModel =
                    viewModel(factory = OnboardingViewModel.Factory(userRepository))

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
            val viewModel: AuthViewModel =
                    viewModel(factory = AuthViewModel.Factory(userRepository))

            LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(RhythmRoutes.HOME) {
                            popUpTo(RhythmRoutes.LOGIN) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(RhythmRoutes.REGISTER) },
                    viewModel = viewModel
            )
        }

        // Registration Screen
        composable(RhythmRoutes.REGISTER) {
            val viewModel: AuthViewModel =
                    viewModel(factory = AuthViewModel.Factory(userRepository))

            RegisterScreen(
                    onRegistrationSuccess = { navController.popBackStack() },
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = viewModel
            )
        }

        // Main Screen with Bottom Navigation (Home, Arcades, Community, Profile)
        composable(RhythmRoutes.HOME) {
            MainScreen(
                    onLogout = {
                        navController.navigate(RhythmRoutes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToQueue = { arcadeId ->
                        navController.navigate("${RhythmRoutes.QUEUE_BASE}/$arcadeId")
                    },
                    onNavigateToAdmin = { navController.navigate(RhythmRoutes.ADMIN) },
                    userRepository = userRepository
            )
        }

        // Admin Dashboard Screen
        composable(RhythmRoutes.ADMIN) {
            AdminScreen(
                    userRepository = userRepository,
                    onNavigateBack = { navController.popBackStack() }
            )
        }

        // Queue Detail Screen
        composable(
                route = RhythmRoutes.QUEUE,
                arguments =
                        listOf(
                                androidx.navigation.navArgument("arcadeId") {
                                    type = androidx.navigation.NavType.StringType
                                }
                        )
        ) {
            val viewModel: edu.uc.intprog32.rhythmhub.presentation.queue.QueueViewModel =
                    viewModel(
                            factory =
                                    edu.uc.intprog32.rhythmhub.presentation.queue.QueueViewModel
                                            .Factory(userRepository)
                    )
            QueueScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
        }
    }
}

/**
 * Determines the start destination based on app state. Checks SharedPreferences for first launch
 * and auto-login state.
 *
 * @param context Application context
 * @return The appropriate start destination route
 */
fun getStartDestination(context: Context): String = runBlocking {
    val userRepository = UserRepository(context)

    when {
        // First launch - show onboarding
        userRepository.isFirstLaunch() -> RhythmRoutes.ONBOARDING

        // Auto-login if "Remember Me" is enabled and user exists
        userRepository.shouldAutoLogin() -> RhythmRoutes.HOME

        // Default to login
        else -> RhythmRoutes.LOGIN
    }
}
