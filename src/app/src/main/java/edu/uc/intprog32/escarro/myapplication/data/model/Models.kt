package edu.uc.intprog32.escarro.myapplication.data.model

data class Arcade(
        val id: String = "",
        val name: String = "",
        val address: String = "",
        val city: String = "Unknown",
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val distanceKm: Double = 0.0,
        val machineCount: Int = 0,
        val currentQueueSize: Int = 0,
        val isOpen: Boolean = true
) {
    // No-argument constructor for Firestore deserialization
    constructor() : this("", "", "", "Unknown", 0.0, 0.0, 0.0, 0, 0, true)
}

data class QueueItem(
        val id: String,
        val userId: String,
        val username: String,
        val avatarUrl: String,
        val isTwoPlayer: Boolean,
        val timestamp: Long
)

data class User(
        val id: String = "",
        val username: String = "",
        val password: String = "", // For local auth (guest mode)
        val email: String = "",
        val xp: Int = 0,
        val level: Int = 1,
        val trustScore: Float = 1.0f,
        // Profile Fields
        val bio: String = "",
        val isAdmin: Boolean = false,
        val avatarUrl: String = ""
) {
    fun getAvatarUrl(): String {
        return if (avatarUrl.isNotEmpty()) avatarUrl
        else "https://api.dicebear.com/7.x/avataaars/svg?seed=$username"
    }
}

data class Post(
        val id: String,
        val authorName: String,
        val content: String,
        val timestamp: Long,
        val likes: Int
)
