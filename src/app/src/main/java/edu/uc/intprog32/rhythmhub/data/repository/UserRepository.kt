package edu.uc.intprog32.rhythmhub.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.uc.intprog32.rhythmhub.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Unified Repository managing User Authentication (Mock/Local via DataStore) and User Profile Data
 * (Firestore).
 */
class UserRepository(private val context: Context) {
    private val firestore: FirebaseFirestore = Firebase.firestore
    private val dataStore = context.dataStore
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Keys
    companion object {
        val CURRENT_USER_KEY = stringPreferencesKey("current_user")
        val REMEMBER_ME_KEY = booleanPreferencesKey("remember_me")
        val IS_FIRST_LAUNCH_KEY = booleanPreferencesKey("is_first_launch")
        // Dynamic keys for user storage: "user_$username" -> password, "bio_$username" -> bio
    }

    // Live User Data from Firestore
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        // Initialize current user from DataStore (asynchronously)
        scope.launch {
            val username = getCurrentUser()
            if (username != null) {
                listenToFirestoreUser(username)
            }
        }
    }

    // --- Firestore / Gamification Logic ---

    private fun listenToFirestoreUser(username: String) {
        firestore.collection("users").document(username).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("UserRepo", "Listen failed", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                // Launch in scope since getBio is suspend
                scope.launch {
                    try {
                        val localBio = getBio(username) // Fallback to local
                        val user =
                                User(
                                        id = snapshot.id,
                                        username = snapshot.getString("username") ?: username,
                                        email = snapshot.getString("email") ?: "",
                                        xp = snapshot.getLong("xp")?.toInt() ?: 0,
                                        level = snapshot.getLong("level")?.toInt() ?: 1,
                                        trustScore = snapshot.getDouble("trustScore")?.toFloat()
                                                        ?: 1.0f,
                                        bio = snapshot.getString("bio") ?: localBio,
                                        isAdmin = snapshot.getBoolean("isAdmin") ?: false,
                                        avatarUrl = snapshot.getString("avatarUrl") ?: ""
                                )
                        _currentUser.value = user
                    } catch (ex: Exception) {
                        Log.e("UserRepo", "Error parsing user", ex)
                    }
                }
            } else {
                val defaultUser = User(id = username, username = username, xp = 0, level = 1)
                firestore.collection("users").document(username).set(defaultUser)
                _currentUser.value = defaultUser
            }
        }
    }

    // --- Mock Authentication Logic (DataStore) ---

    suspend fun getCurrentUser(): String? {
        val prefs = dataStore.data.first()
        return prefs[CURRENT_USER_KEY]
    }

    suspend fun setCurrentUser(username: String) {
        dataStore.edit { prefs -> prefs[CURRENT_USER_KEY] = username }
        listenToFirestoreUser(username)
    }

    suspend fun getUser(username: String): User? {
        val passwordKey = stringPreferencesKey("user_$username")
        val prefs = dataStore.data.first()
        val storedPassword = prefs[passwordKey]

        return if (storedPassword != null) {
            if (_currentUser.value?.username == username) {
                _currentUser.value
            } else {
                User(id = username, username = username)
            }
        } else {
            null
        }
    }

    suspend fun validateUser(username: String, password: String): Boolean {
        val passwordKey = stringPreferencesKey("user_$username")
        val prefs = dataStore.data.first()
        val storedPassword = prefs[passwordKey]
        return storedPassword == password
    }

    suspend fun registerUser(username: String, password: String): Boolean {
        val passwordKey = stringPreferencesKey("user_$username")
        val prefs = dataStore.data.first()

        if (prefs.contains(passwordKey)) {
            return false
        }

        dataStore.edit { it[passwordKey] = password }

        val newUser = User(id = username, username = username, xp = 0, level = 1)
        firestore.collection("users").document(username).set(newUser)

        return true
    }

    private suspend fun getBio(username: String): String {
        val bioKey = stringPreferencesKey("bio_$username")
        return dataStore.data.first()[bioKey] ?: ""
    }

    suspend fun updateBio(username: String, bio: String) {
        val bioKey = stringPreferencesKey("bio_$username")
        dataStore.edit { it[bioKey] = bio }
        firestore.collection("users").document(username).update("bio", bio)
    }

    suspend fun setRememberMe(enabled: Boolean) {
        dataStore.edit { it[REMEMBER_ME_KEY] = enabled }
    }

    suspend fun logout() {
        dataStore.edit { it.remove(CURRENT_USER_KEY) }
        _currentUser.value = null
    }

    suspend fun isFirstLaunch(): Boolean {
        val prefs = dataStore.data.first()
        val isFirst = prefs[IS_FIRST_LAUNCH_KEY] ?: true
        if (isFirst) {
            dataStore.edit { it[IS_FIRST_LAUNCH_KEY] = false }
        }
        return isFirst
    }

    suspend fun shouldAutoLogin(): Boolean {
        val prefs = dataStore.data.first()
        val remember = prefs[REMEMBER_ME_KEY] ?: false
        val user = prefs[CURRENT_USER_KEY]
        return remember && user != null
    }

    /** Marks onboarding as completed so it won't show again. */
    suspend fun setOnboardingCompleted() {
        dataStore.edit { it[IS_FIRST_LAUNCH_KEY] = false }
    }

    /**
     * Add XP to the current user. Updates both local state and Firestore. Also calculates level
     * based on XP thresholds.
     */
    fun addXp(amount: Int) {
        val user = _currentUser.value ?: return
        val newXp = user.xp + amount
        val newLevel = calculateLevel(newXp)

        // Update local state immediately for responsive UI
        _currentUser.value = user.copy(xp = newXp, level = newLevel)

        // Update Firestore
        scope.launch {
            try {
                firestore
                        .collection("users")
                        .document(user.username)
                        .update(mapOf("xp" to newXp, "level" to newLevel))
            } catch (e: Exception) {
                Log.e("UserRepo", "Failed to update XP in Firestore", e)
            }
        }
    }

    /** Calculate level from XP. Every 100 XP = 1 level. */
    private fun calculateLevel(xp: Int): Int {
        return (xp / 100) + 1
    }
}
