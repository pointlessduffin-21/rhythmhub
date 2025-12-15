package edu.uc.intprog32.escarro.myapplication.presentation.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.uc.intprog32.escarro.myapplication.data.AppConfig
import edu.uc.intprog32.escarro.myapplication.data.model.User
import edu.uc.intprog32.escarro.myapplication.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AdminUiState(
        val isLoading: Boolean = true,
        val users: List<User> = emptyList(),
        val totalCheckInsToday: Int = 0,
        val totalUsers: Int = 0,
        val environment: String = "",
        val errorMessage: String? = null,
        val successMessage: String? = null
)

class AdminViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val firestore = Firebase.firestore

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        loadAdminData()
    }

    fun loadAdminData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // Load all users
                val usersSnapshot = firestore.collection("users").get().await()
                val users =
                        usersSnapshot.documents.mapNotNull { doc ->
                            try {
                                User(
                                        id = doc.id,
                                        username = doc.getString("username") ?: doc.id,
                                        email = doc.getString("email") ?: "",
                                        xp = doc.getLong("xp")?.toInt() ?: 0,
                                        level = doc.getLong("level")?.toInt() ?: 1,
                                        trustScore = doc.getDouble("trustScore")?.toFloat() ?: 1.0f,
                                        bio = doc.getString("bio") ?: "",
                                        isAdmin = doc.getBoolean("isAdmin") ?: false,
                                        avatarUrl = doc.getString("avatarUrl") ?: ""
                                )
                            } catch (e: Exception) {
                                Log.w("AdminVM", "Error parsing user ${doc.id}", e)
                                null
                            }
                        }

                // Get today's check-ins
                val todayStart = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
                val checkInsSnapshot =
                        firestore
                                .collection("checkins")
                                .whereGreaterThan("timestamp", todayStart)
                                .get()
                                .await()

                _uiState.update {
                    it.copy(
                            isLoading = false,
                            users = users.sortedByDescending { u -> u.xp },
                            totalUsers = users.size,
                            totalCheckInsToday = checkInsSnapshot.size(),
                            environment = if (AppConfig.isDev) "DEV" else "PROD"
                    )
                }
            } catch (e: Exception) {
                Log.e("AdminVM", "Error loading admin data", e)
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Failed to load data: ${e.message}")
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun toggleUserAdmin(user: User) {
        viewModelScope.launch {
            try {
                val newAdminStatus = !user.isAdmin
                firestore
                        .collection("users")
                        .document(user.id)
                        .update("isAdmin", newAdminStatus)
                        .await()

                _uiState.update { state ->
                    state.copy(
                            users =
                                    state.users.map {
                                        if (it.id == user.id) it.copy(isAdmin = newAdminStatus)
                                        else it
                                    },
                            successMessage =
                                    if (newAdminStatus) "${user.username} is now an admin"
                                    else "${user.username} is no longer an admin"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to update user: ${e.message}") }
            }
        }
    }

    class Factory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
                return AdminViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
