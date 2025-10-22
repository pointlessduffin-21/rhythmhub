package edu.uc.intprog32.escarro.myapplication.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.uc.intprog32.escarro.myapplication.data.repository.UserRepository

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
        userRepository.setOnboardingCompleted()
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
