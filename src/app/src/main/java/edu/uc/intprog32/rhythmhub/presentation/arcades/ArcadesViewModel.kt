package edu.uc.intprog32.rhythmhub.presentation.arcades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import edu.uc.intprog32.rhythmhub.data.AppConfig
import edu.uc.intprog32.rhythmhub.data.model.Arcade
import edu.uc.intprog32.rhythmhub.data.repository.ArcadeRepository
import edu.uc.intprog32.rhythmhub.data.repository.CheckInRepository
import edu.uc.intprog32.rhythmhub.data.repository.MockArcadeRepository
import edu.uc.intprog32.rhythmhub.data.repository.MockCheckInRepository
import edu.uc.intprog32.rhythmhub.data.repository.RealArcadeRepository
import edu.uc.intprog32.rhythmhub.data.repository.RealCheckInRepository
import edu.uc.intprog32.rhythmhub.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ArcadesViewModel(
        private val repository: ArcadeRepository,
        private val checkInRepository: CheckInRepository,
        private val userRepository: UserRepository
) : ViewModel() {
    val arcades: StateFlow<List<Arcade>> = repository.arcades

    private val _checkInResult = MutableStateFlow<String?>(null)
    val checkInResult: StateFlow<String?> = _checkInResult.asStateFlow()

    /** Get current user ID. In DEV mode, falls back to test user if not logged in. */
    private val currentUserId: String?
        get() {
            val user = userRepository.currentUser.value
            return user?.id ?: if (AppConfig.isDev) AppConfig.DEV_TEST_USER_ID else null
        }

    fun clearCheckInResult() {
        _checkInResult.value = null
    }

    fun performCheckIn(arcade: Arcade, userLocation: android.location.Location) {
        viewModelScope.launch {
            val userId = currentUserId
            if (userId == null) {
                _checkInResult.value = "Please login to check in"
                return@launch
            }
            val result = checkInRepository.attemptCheckIn(arcade, userLocation, userId)
            result.onSuccess { xp -> _checkInResult.value = "Verified! Earned $xp XP" }.onFailure {
                    e ->
                _checkInResult.value = "Check-in Failed: ${e.message}"
            }
        }
    }

    /** Factory that accepts UserRepository for proper dependency injection. */
    class Factory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val application =
                    (extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as
                            android.app.Application)

            // Use Mock repositories in Dev Mode for easier testing
            val arcadeRepo: ArcadeRepository =
                    if (AppConfig.isDev) {
                        MockArcadeRepository
                    } else {
                        RealArcadeRepository(application.applicationContext)
                    }

            val checkInRepo: CheckInRepository =
                    if (AppConfig.isDev) {
                        MockCheckInRepository
                    } else {
                        RealCheckInRepository()
                    }

            return ArcadesViewModel(arcadeRepo, checkInRepo, userRepository) as T
        }
    }
}
