package edu.uc.intprog32.escarro.myapplication.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.uc.intprog32.escarro.myapplication.data.model.User
import edu.uc.intprog32.escarro.myapplication.data.repository.UserRepository
import edu.uc.intprog32.escarro.myapplication.data.util.AvatarGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the Profile/Settings screen.
 *
 * @property user Current user data
 * @property isLoading Whether an operation is in progress
 * @property errorMessage Error message to display
 * @property successMessage Success message to display
 * @property isEditingBio Whether bio edit mode is active
 * @property tempBio Temporary bio value during editing
 */
data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isEditingBio: Boolean = false,
    val tempBio: String = ""
)

/**
 * ViewModel for Profile/Settings screen.
 * Manages user profile data and settings.
 *
 * @property userRepository Repository for user data operations
 */
class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    /**
     * Loads the current user's profile data.
     */
    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val username = userRepository.getCurrentUser()
            if (username != null) {
                val user = userRepository.getUser(username)
                _uiState.update {
                    it.copy(
                        user = user,
                        isLoading = false,
                        tempBio = user?.bio ?: ""
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No user logged in"
                    )
                }
            }
        }
    }

    /**
     * Starts editing the bio.
     */
    fun startEditingBio() {
        _uiState.update {
            it.copy(
                isEditingBio = true,
                tempBio = it.user?.bio ?: "",
                errorMessage = null
            )
        }
    }

    /**
     * Updates the temporary bio value.
     */
    fun updateTempBio(newValue: String) {
        _uiState.update { it.copy(tempBio = newValue) }
    }

    /**
     * Saves the updated bio.
     */
    fun saveBio() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val username = currentState.user?.username

            if (username == null) {
                _uiState.update { it.copy(errorMessage = "No user found") }
                return@launch
            }

            val newBio = currentState.tempBio.trim()

            _uiState.update { it.copy(isLoading = true) }

            userRepository.updateBio(username, newBio)

            // Reload user data
            val updatedUser = userRepository.getUser(username)

            _uiState.update {
                it.copy(
                    user = updatedUser,
                    isLoading = false,
                    isEditingBio = false,
                    successMessage = "Bio updated!",
                    errorMessage = null
                )
            }
        }
    }

    /**
     * Cancels editing the bio.
     */
    fun cancelEditingBio() {
        _uiState.update {
            it.copy(
                isEditingBio = false,
                tempBio = it.user?.bio ?: "",
                errorMessage = null
            )
        }
    }

    /**
     * Generates a new random avatar.
     */
    fun generateNewAvatar() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val username = currentState.user?.username

            if (username == null) {
                _uiState.update { it.copy(errorMessage = "No user found") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }

            // Generate new random seed
            val newSeed = AvatarGenerator.generate()
            userRepository.updateAvatarSeed(username, newSeed)

            // Reload user data
            val updatedUser = userRepository.getUser(username)

            _uiState.update {
                it.copy(
                    user = updatedUser,
                    isLoading = false,
                    successMessage = "Avatar updated!",
                    errorMessage = null
                )
            }
        }
    }

    /**
     * Clears any messages.
     */
    fun clearMessages() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                successMessage = null
            )
        }
    }

    /**
     * Factory for creating ProfileViewModel with UserRepository dependency.
     */
    class Factory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
