package edu.uc.intprog32.escarro.myapplication.data.repository

import android.content.Context
import android.content.SharedPreferences
import edu.uc.intprog32.escarro.myapplication.data.model.User
import org.json.JSONObject
import androidx.core.content.edit

class UserRepository(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "RhythmHubPreferences"
        private const val KEY_USERS = "users_json"
        private const val KEY_CURRENT_USER = "current_user"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
        private const val KEY_USER_PROFILES = "user_profiles_json"
    }

    init {
        ensureAdminExists()
    }

    private fun ensureAdminExists() {
        val users = getAllUsers()
        if (!users.containsKey("admin")) {
            val updatedUsers = users.toMutableMap()
            updatedUsers["admin"] = "admin"
            saveAllUsers(updatedUsers)
        }
    }

    private fun getAllUsers(): Map<String, String> {
        val jsonString = sharedPreferences.getString(KEY_USERS, "{}") ?: "{}"
        val jsonObject = JSONObject(jsonString)
        val usersMap = mutableMapOf<String, String>()

        jsonObject.keys().forEach { key ->
            usersMap[key] = jsonObject.getString(key)
        }

        return usersMap
    }

    private fun saveAllUsers(users: Map<String, String>) {
        val jsonObject = JSONObject()
        users.forEach { (username, password) ->
            jsonObject.put(username, password)
        }

        sharedPreferences.edit {
            putString(KEY_USERS, jsonObject.toString())
        }
    }

    fun registerUser(username: String, password: String): Boolean {
        val users = getAllUsers().toMutableMap()

        if (users.containsKey(username)) {
            return false
        }

        users[username] = password
        saveAllUsers(users)
        return true
    }

    fun validateUser(username: String, password: String): Boolean {
        val users = getAllUsers()
        return users[username] == password
    }

    fun userExists(username: String): Boolean {
        return getAllUsers().containsKey(username)
    }

    fun setCurrentUser(username: String) {
        sharedPreferences.edit {
            putString(KEY_CURRENT_USER, username)
        }
    }

    fun getCurrentUser(): String? {
        return sharedPreferences.getString(KEY_CURRENT_USER, null)
    }

    fun clearCurrentUser() {
        sharedPreferences.edit {
            remove(KEY_CURRENT_USER)
        }
    }

    fun setRememberMe(remember: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_REMEMBER_ME, remember)
        }
    }

    fun getRememberMe(): Boolean {
        return sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)
    }

    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_FIRST_LAUNCH, true)
    }

    fun setOnboardingCompleted() {
        sharedPreferences.edit {
            putBoolean(KEY_IS_FIRST_LAUNCH, false)
        }
    }

    fun shouldAutoLogin(): Boolean {
        return getRememberMe() && getCurrentUser() != null
    }

    fun logout() {
        clearCurrentUser()
        setRememberMe(false)
    }

    fun getUser(username: String): User? {
        val users = getAllUsers()
        val password = users[username] ?: return null
        val profiles = getUserProfiles()
        val profile = profiles[username] as? Map<String, String>

        return User(
            username = username,
            password = password,
            bio = profile?.get("bio") ?: "",
            avatarSeed = profile?.get("avatarSeed") ?: username,
            isAdmin = username == "admin"
        )
    }

    fun updateAvatarSeed(username: String, avatarSeed: String) {
        val profiles = getUserProfiles().toMutableMap()
        val userProfile = (profiles[username] as? Map<String, String>)?.toMutableMap() ?: mutableMapOf()
        userProfile["avatarSeed"] = avatarSeed
        profiles[username] = userProfile
        saveUserProfiles(profiles)
    }
    
    fun updateBio(username: String, bio: String) {
        val profiles = getUserProfiles().toMutableMap()
        val userProfile = (profiles[username] as? Map<String, String>)?.toMutableMap() ?: mutableMapOf()
        userProfile["bio"] = bio
        profiles[username] = userProfile
        saveUserProfiles(profiles)
    }

    private fun getUserProfiles(): Map<String, Any> {
        val jsonString = sharedPreferences.getString(KEY_USER_PROFILES, "{}") ?: "{}"
        val jsonObject = JSONObject(jsonString)
        val profilesMap = mutableMapOf<String, Any>()

        jsonObject.keys().forEach { key ->
            val profileJson = jsonObject.getJSONObject(key)
            val profileData = mutableMapOf<String, String>()
            profileJson.keys().forEach { profileKey ->
                profileData[profileKey] = profileJson.optString(profileKey, "")
            }
            profilesMap[key] = profileData
        }
        return profilesMap
    }

    private fun saveUserProfiles(profiles: Map<String, Any>) {
        val jsonObject = JSONObject()
        profiles.forEach { (username, profileData) ->
            if (profileData is Map<*, *>) {
                val profileJson = JSONObject(profileData)
                jsonObject.put(username, profileJson)
            }
        }

        sharedPreferences.edit {
            putString(KEY_USER_PROFILES, jsonObject.toString())
        }
    }
}