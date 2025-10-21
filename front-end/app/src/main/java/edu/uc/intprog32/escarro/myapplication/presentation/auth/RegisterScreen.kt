package edu.uc.intprog32.escarro.myapplication.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.uc.intprog32.escarro.myapplication.presentation.components.GradientBackground
import edu.uc.intprog32.escarro.myapplication.presentation.components.RhythmButton
import edu.uc.intprog32.escarro.myapplication.presentation.components.RhythmPasswordField
import edu.uc.intprog32.escarro.myapplication.presentation.components.RhythmTextField

/**
 * Registration screen for creating new user accounts.
 * Implements MVP Feature 1: User Onboarding & Management requirement.
 *
 * Uses MVVM pattern with AuthViewModel and SharedPreferences through Repository.
 *
 * @param onRegistrationSuccess Callback when registration succeeds
 * @param onNavigateBack Callback to navigate back to login
 * @param viewModel ViewModel for authentication logic
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegistrationSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle registration success
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            snackbarHostState.showSnackbar("Registration successful! Please login.")
            viewModel.resetState()
            onRegistrationSuccess()
        }
    }

    // Show error snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Account") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetState()
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        GradientBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Create Account",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = "Join RhythmHub",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle
                Text(
                    text = "Create your account to start queuing",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Username field
                RhythmTextField(
                    value = uiState.username,
                    onValueChange = viewModel::updateUsername,
                    label = "Username",
                    imeAction = ImeAction.Next,
                    enabled = !uiState.isLoading,
                    isError = uiState.errorMessage?.contains("Username") == true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password field
                RhythmPasswordField(
                    value = uiState.password,
                    onValueChange = viewModel::updatePassword,
                    label = "Password",
                    imeAction = ImeAction.Next,
                    enabled = !uiState.isLoading,
                    isError = uiState.errorMessage?.contains("Password") == true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Password field
                RhythmPasswordField(
                    value = uiState.confirmPassword,
                    onValueChange = viewModel::updateConfirmPassword,
                    label = "Confirm Password",
                    imeAction = ImeAction.Done,
                    enabled = !uiState.isLoading,
                    isError = uiState.errorMessage?.contains("match") == true,
                    onImeAction = { viewModel.register() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Password requirements
                Text(
                    text = "Password must be at least 4 characters",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Register button
                RhythmButton(
                    text = if (uiState.isLoading) "Creating Account..." else "Create Account",
                    onClick = { viewModel.register() },
                    enabled = !uiState.isLoading
                )

                if (uiState.isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
