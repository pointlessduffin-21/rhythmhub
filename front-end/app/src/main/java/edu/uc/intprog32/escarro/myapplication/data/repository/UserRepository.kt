package edu.uc.intprog32.escarro.myapplication.data.repository

import android.content.Context
import android.content.SharedPreferences
import edu.uc.intprog32.escarro.myapplication.data.model.User
import org.json.JSONObject

/**
 * Repository class for managing user data using SharedPreferences.
 * Implements the Repository pattern to abstract data source from ViewModels.
 *
 * This class uses SharedPreferences as required by the assignment specifications
 * to store and retrieve user credentials, authentication state, and preferences.
 */
class UserRepository(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "RhythmHubPreferences"
        private const val KEY_USERS = "users_json"
        private const val KEY_CURRENT_USER = "current_user"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
    }

    init {
        // Ensure default admin user exists
        ensureAdminExists()
    }

    /**
     * Ensures the admin user exists in the system.
     * This is called on initialization to guarantee a default login account.
     */
    private fun ensureAdminExists() {
        val users = getAllUsers()
        if (!users.containsKey("admin")) {
            val updatedUsers = users.toMutableMap()
            updatedUsers["admin"] = "admin"
            saveAllUsers(updatedUsers)
        }
    }

    /**
     * Retrieves all users from SharedPreferences.
     *
     * @return Map of username to password
     */
    private fun getAllUsers(): Map<String, String> {
        val jsonString = sharedPreferences.getString(KEY_USERS, "{}") ?: "{}"
        val jsonObject = JSONObject(jsonString)
        val usersMap = mutableMapOf<String, String>()

        jsonObject.keys().forEach { key ->
            usersMap[key] = jsonObject.getString(key)
        }

        return usersMap
    }

    /**
     * Saves all users to SharedPreferences.
     *
     * @param users Map of username to password
     */
    private fun saveAllUsers(users: Map<String, String>) {
        val jsonObject = JSONObject()
        users.forEach { (username, password) ->
            jsonObject.put(username, password)
        }

        sharedPreferences.edit()
            .putString(KEY_USERS, jsonObject.toString())
            .apply()
    }

    /**
     * Registers a new user.
     *
     * @param username The desired username
     * @param password The user's password
     * @return true if registration successful, false if username already exists
     */
    fun registerUser(username: String, password: String): Boolean {
        val users = getAllUsers().toMutableMap()

        // Check if username already exists
        if (users.containsKey(username)) {
            return false
        }

        // Add new user
        users[username] = password
        saveAllUsers(users)
        return true
    }

    /**
     * Validates user credentials.
     *
     * @param username The username to validate
     * @param password The password to check
     * @return true if credentials are valid, false otherwise
     */
    fun validateUser(username: String, password: String): Boolean {
        val users = getAllUsers()
        return users[username] == password
    }

    /**
     * Checks if a username exists in the system.
     *
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    fun userExists(username: String): Boolean {
        return getAllUsers().containsKey(username)
    }

    /**
     * Sets the currently logged-in user.
     *
     * @param username The username of the current user
     */
    fun setCurrentUser(username: String) {
        sharedPreferences.edit()
            .putString(KEY_CURRENT_USER, username)
            .apply()
    }

    /**
     * Gets the currently logged-in user.
     *
     * @return The username of the current user, or null if no user is logged in
     */
    fun getCurrentUser(): String? {
        return sharedPreferences.getString(KEY_CURRENT_USER, null)
    }

    /**
     * Clears the current user (logout).
     */
    fun clearCurrentUser() {
        sharedPreferences.edit()
            .remove(KEY_CURRENT_USER)
            .apply()
    }

    /**
     * Sets the "Remember Me" preference.
     *
     * @param remember true to remember the user, false otherwise
     */
    fun setRememberMe(remember: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_REMEMBER_ME, remember)
            .apply()
    }

    /**
     * Gets the "Remember Me" preference.
     *
     * @return true if "Remember Me" is enabled, false otherwise
     */
    fun getRememberMe(): Boolean {
        return sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)
    }

    /**
     * Checks if this is the first launch of the app.
     *
     * @return true if first launch, false otherwise
     */
    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_FIRST_LAUNCH, true)
    }

    /**
     * Marks that the onboarding has been completed.
     */
    fun setOnboardingCompleted() {
        sharedPreferences.edit()
            .putBoolean(KEY_IS_FIRST_LAUNCH, false)
            .apply()
    }

    /**
     * Checks if user is currently logged in and should be remembered.
     *
     * @return true if user should auto-login, false otherwise
     */
    fun shouldAutoLogin(): Boolean {
        return getRememberMe() && getCurrentUser() != null
    }

    /**
     * Performs a complete logout, clearing all session data.
     */
    fun logout() {
        clearCurrentUser()
        setRememberMe(false)
    }
}
