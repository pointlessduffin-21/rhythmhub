package edu.uc.intprog32.rhythmhub.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.uc.intprog32.rhythmhub.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for the Onboarding screen.
 * Handles marking onboarding as completed in SharedPreferences.
 *
 * @property userRepository Repository for user data operations
 */
class OnboardingViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    /**
     * Marks onboarding as completed.
     * Stores this preference in SharedPreferences so user won't see it again.
     */
    fun completeOnboarding() {
        viewModelScope.launch {
            userRepository.setOnboardingCompleted()
        }
    }

    /**
     * Factory for creating OnboardingViewModel with UserRepository dependency.
     */
    class Factory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
                return OnboardingViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

