package edu.uc.intprog32.escarro.myapplication.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.uc.intprog32.escarro.myapplication.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for authentication screens (Login and Registration).
 *
 * @property username Current username input
 * @property password Current password input
 * @property confirmPassword Confirmation password for registration
 * @property bio The user's bio
 * @property rememberMe Whether "Remember Me" is checked
 * @property isLoading Whether an operation is in progress
 * @property errorMessage Error message to display, null if no error
 * @property isSuccess Whether the operation was successful
 */
data class AuthUiState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val bio: String = "",
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

/**
 * ViewModel for authentication operations (Login and Registration).
 * Manages UI state and communicates with UserRepository using SharedPreferences.
 *
 * This ViewModel implements MVVM pattern
 * It uses StateFlow to expose UI state to Compose screens and handles
 * authentication logic by delegating to the Repository layer.
 *
 * @property userRepository Repository for user data operations
 */
class AuthViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()


    /**
     * Updates the username field.
     */
    fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username, errorMessage = null) }
    }

    /**
     * Updates the password field.
     */
    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    /**
     * Updates the confirm password field.
     */
    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, errorMessage = null) }
    }

    /**
     * Updates the bio field.
     */
    fun updateBio(bio: String) {
        _uiState.update { it.copy(bio = bio, errorMessage = null) }
    }

    /**
     * Toggles the "Remember Me" checkbox.
     */
    fun toggleRememberMe() {
        _uiState.update { it.copy(rememberMe = !it.rememberMe) }
    }

    /**
     * Clears any error message.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Performs user login.
     * Validates credentials using SharedPreferences through the Repository.
     */
    fun login() {
        viewModelScope.launch {
            val currentState = _uiState.value

            // Validation
            if (currentState.username.isBlank() || currentState.password.isBlank()) {
                _uiState.update {
                    it.copy(errorMessage = "Username and password are required")
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Validate credentials
            val isValid = userRepository.validateUser(
                currentState.username,
                currentState.password
            )

            if (isValid) {
                // Save current user and remember preference to SharedPreferences
                userRepository.setCurrentUser(currentState.username)
                userRepository.setRememberMe(currentState.rememberMe)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = "Invalid username or password"
                    )
                }
            }
        }
    }

    /**
     * Performs user registration.
     * Creates new user in SharedPreferences through the Repository.
     */
    fun register() {
        viewModelScope.launch {
            val currentState = _uiState.value

            // Validation
            if (currentState.username.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Username is required") }
                return@launch
            }

            if (currentState.password.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Password is required") }
                return@launch
            }

            if (currentState.password.length < 4) {
                _uiState.update {
                    it.copy(errorMessage = "Password must be at least 4 characters")
                }
                return@launch
            }

            if (currentState.password != currentState.confirmPassword) {
                _uiState.update { it.copy(errorMessage = "Passwords do not match") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Attempt to register user
            val success = userRepository.registerUser(
                currentState.username,
                currentState.password
            )

            if (success) {
                userRepository.updateBio(currentState.username, currentState.bio)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = "Username already exists"
                    )
                }
            }
        }
    }

    /**
     * Resets the UI state (useful when navigating between screens).
     */
    fun resetState() {
        _uiState.value = AuthUiState()
    }

    /**
     * Factory for creating AuthViewModel with UserRepository dependency.
     */
    class Factory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                return AuthViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
