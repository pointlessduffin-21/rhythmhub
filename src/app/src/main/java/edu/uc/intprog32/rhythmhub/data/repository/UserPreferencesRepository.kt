package edu.uc.intprog32.rhythmhub.data.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Note: Using dataStore extension property defined in UserRepository.kt

/**
 * Repository for managing simple user preferences (Onboarding state, Session info) using Jetpack
 * DataStore. Replaces legacy SharedPreferences for these specific keys.
 */
class UserPreferencesRepository(private val context: Context) {

    companion object {
        private val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        private val CURRENT_USER = stringPreferencesKey("current_user")
        private val REMEMBER_ME = booleanPreferencesKey("remember_me")
    }

    /** Flow indicating if this is the first time the app is launched. Default: true */
    val isFirstLaunch: Flow<Boolean> =
            context.dataStore.data.map { preferences: Preferences ->
                preferences[IS_FIRST_LAUNCH] ?: true
            }

    /** Flow containing the currently logged-in username, if any. */
    val currentUser: Flow<String?> =
            context.dataStore.data.map { preferences: Preferences -> preferences[CURRENT_USER] }

    /** Flow indicating if the "Remember Me" option is checked. Default: false */
    val rememberMe: Flow<Boolean> =
            context.dataStore.data.map { preferences: Preferences ->
                preferences[REMEMBER_ME] ?: false
            }

    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { preferences -> preferences[IS_FIRST_LAUNCH] = false }
    }

    suspend fun setCurrentUser(username: String) {
        context.dataStore.edit { preferences -> preferences[CURRENT_USER] = username }
    }

    suspend fun clearCurrentUser() {
        context.dataStore.edit { preferences -> preferences.remove(CURRENT_USER) }
    }

    suspend fun setRememberMe(remember: Boolean) {
        context.dataStore.edit { preferences -> preferences[REMEMBER_ME] = remember }
    }
}

