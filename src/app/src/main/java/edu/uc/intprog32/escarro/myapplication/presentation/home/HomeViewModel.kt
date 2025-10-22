package edu.uc.intprog32.escarro.myapplication.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.uc.intprog32.escarro.myapplication.data.model.User
import edu.uc.intprog32.escarro.myapplication.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the Home/Dashboard screen.
 *
 * @property user Current user data (includes username, displayName, avatar)
 * @property isLoading Whether data is being loaded
 */
data class HomeUiState(
    val user: User? = null,
    val isLoading: Boolean = true
)

/**
 * ViewModel for the Home/Dashboard screen.
 * Manages user profile display and logout functionality.
 *
 * This ViewModel implements MVVM pattern and uses SharedPreferences
 * through the Repository to retrieve and manage user session data.
 *
 * @property userRepository Repository for user data operations
 */
class HomeViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    /**
     * Loads current user data from SharedPreferences via Repository.
     */
    fun loadUserData() {
        viewModelScope.launch {
            val currentUsername = userRepository.getCurrentUser()
            val user = if (currentUsername != null) {
                userRepository.getUser(currentUsername)
            } else {
                null
            }

            _uiState.update {
                it.copy(
                    user = user,
                    isLoading = false
                )
            }
        }
    }

    /**
     * Performs logout operation.
     * Clears user session from SharedPreferences.
     */
    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    /**
     * Factory for creating HomeViewModel with UserRepository dependency.
     */
    class Factory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
