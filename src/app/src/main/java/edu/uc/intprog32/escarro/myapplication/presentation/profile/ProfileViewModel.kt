package edu.uc.intprog32.escarro.myapplication.presentation.profile

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

data class ProfileUiState(
        val user: User? = null,
        val isLoading: Boolean = false,
        val isEditingBio: Boolean = false,
        val tempBio: String = "",
        val successMessage: String? = null,
        val errorMessage: String? = null
)

class ProfileViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.currentUser.collect { user ->
                _uiState.update { it.copy(user = user, isLoading = false) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }

    fun startEditingBio() {
        _uiState.update { it.copy(isEditingBio = true, tempBio = it.user?.bio ?: "") }
    }

    fun cancelEditingBio() {
        _uiState.update { it.copy(isEditingBio = false, tempBio = "") }
    }

    fun updateTempBio(newBio: String) {
        _uiState.update { it.copy(tempBio = newBio) }
    }

    fun saveBio() {
        // In a real app, calls Repository.updateBio(...)
        // For now, simplify mock update
        val newBio = _uiState.value.tempBio
        _uiState.update {
            it.copy(
                    isEditingBio = false,
                    user = it.user?.copy(bio = newBio),
                    successMessage = "Bio updated!"
            )
        }
    }

    fun generateNewAvatar() {
        val randomSeed = System.currentTimeMillis().toString()
        val newUrl = "https://api.dicebear.com/7.x/avataaars/svg?seed=$randomSeed"
        _uiState.update {
            it.copy(user = it.user?.copy(avatarUrl = newUrl), successMessage = "Avatar updated!")
        }
    }

    /** Factory for creating ProfileViewModel with UserRepository dependency. */
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
