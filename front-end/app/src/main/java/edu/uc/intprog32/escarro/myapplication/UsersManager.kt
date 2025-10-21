package edu.uc.intprog32.escarro.myapplication

import android.content.Context
import org.json.JSONObject

object UsersManager {
    private const val PREFS_NAME = "UserPref"
    private const val USERS_KEY = "users" // JSON map username->password
    private const val CURRENT_USER_KEY = "current_user"
    private const val REMEMBER_KEY = "remember"

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Migrate legacy single username/password keys into the users map (idempotent)
    fun migrateLegacy(context: Context) {
        val pref = prefs(context)
        if (!pref.contains(USERS_KEY)) {
            val legacyUser = pref.getString("username", null)
            val legacyPass = pref.getString("password", null)
            val users = JSONObject()
            // Use safe-let to avoid platform-type warnings and ensure non-null keys when calling JSONObject.put
            if (!legacyUser.isNullOrEmpty() && legacyPass != null) {
                val u = legacyUser
                val p = legacyPass
                users.put(u, p)
            }
            // ensure admin exists even if no legacy user
            if (!users.has("admin")) {
                users.put("admin", "admin")
            }
            pref.edit().putString(USERS_KEY, users.toString()).apply()
        } else {
            // users exists; ensure admin exists
            // Use elvis to guarantee a non-null JSON string for JSONObject constructor
            val users = JSONObject(pref.getString(USERS_KEY, "{}") ?: "{}")
            if (!users.has("admin")) {
                users.put("admin", "admin")
                pref.edit().putString(USERS_KEY, users.toString()).apply()
            }
        }
    }

    fun getUsers(context: Context): Map<String, String> {
        val pref = prefs(context)
        val usersJson = pref.getString(USERS_KEY, "{}") ?: "{}"
        val obj = JSONObject(usersJson)
        val map = mutableMapOf<String, String>()
        val keys = obj.keys()
        while (keys.hasNext()) {
            val k = keys.next()
            map[k] = obj.optString(k, "")
        }
        return map
    }

    private fun saveUsers(context: Context, map: Map<String, String>) {
        val obj = JSONObject()
        for ((k, v) in map) obj.put(k, v)
        prefs(context).edit().putString(USERS_KEY, obj.toString()).apply()
    }

    fun createUser(context: Context, username: String, password: String): Boolean {
        val users = getUsers(context).toMutableMap()
        if (users.containsKey(username)) return false
        users[username] = password
        saveUsers(context, users)
        return true
    }

    fun updateUser(context: Context, username: String, password: String): Boolean {
        val users = getUsers(context).toMutableMap()
        if (!users.containsKey(username)) return false
        users[username] = password
        saveUsers(context, users)
        return true
    }

    fun deleteUser(context: Context, username: String): Boolean {
        val users = getUsers(context).toMutableMap()
        if (!users.containsKey(username)) return false
        users.remove(username)
        saveUsers(context, users)
        return true
    }

    fun validateUser(context: Context, username: String, password: String): Boolean {
        val users = getUsers(context)
        return users[username] == password
    }

    fun setCurrentUser(context: Context, username: String?) {
        prefs(context).edit().putString(CURRENT_USER_KEY, username).apply()
    }

    fun getCurrentUser(context: Context): String? = prefs(context).getString(CURRENT_USER_KEY, null)

    fun clearCurrentUser(context: Context) { setCurrentUser(context, null) }

    fun getRemember(context: Context): Boolean = prefs(context).getBoolean(REMEMBER_KEY, false)
    fun setRemember(context: Context, remember: Boolean) { prefs(context).edit().putBoolean(REMEMBER_KEY, remember).apply() }
}
