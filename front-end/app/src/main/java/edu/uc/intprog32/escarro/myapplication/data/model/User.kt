package edu.uc.intprog32.escarro.myapplication.data.model

/**
 * Data class representing a user in the RhythmHub application.
 *
 * @property username The unique username of the user (auto-generated Reddit-style, editable)
 * @property password The user's password (stored as plain text in SharedPreferences for MVP)
 * @property bio A short bio or description for the user.
 * @property avatarSeed Seed for DiceBear avatar generation (username-based)
 * @property isAdmin Whether the user has admin privileges
 */
data class User(
    val username: String,
    val password: String,
    val bio: String = "", // Default to empty, editable in settings
    val avatarSeed: String = username, // Seed for DiceBear avatar
    val isAdmin: Boolean = false
) {
    /**
     * Generates the DiceBear avatar URL using the avatarSeed.
     * Uses the "adventurer" style for fun, unique avatars.
     */
    fun getAvatarUrl(): String {
        return "https://api.dicebear.com/7.x/adventurer/svg?seed=$avatarSeed"
    }
}
