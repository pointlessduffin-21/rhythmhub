package edu.uc.intprog32.rhythmhub.data.model

/** Represents a maimai arcade location. */
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
        val isOpen: Boolean = true,
        val machines: List<Machine> = emptyList()
) {
        // No-argument constructor for Firestore deserialization
        constructor() : this("", "", "", "Unknown", 0.0, 0.0, 0.0, 0, 0, true, emptyList())
}

/** Represents a single maimai machine (cabinet) within an arcade. */
data class Machine(
        val id: String = "",
        val arcadeId: String = "",
        val name: String = "", // e.g., "Left Cabinet", "Machine 1"
        val isActive: Boolean = true
) {
        constructor() : this("", "", "", true)
}

/** Status of a player in the queue. */
enum class QueueStatus {
        WAITING,
        PLAYING,
        COMPLETED
}

/** Represents a player in a machine queue. */
data class QueueItem(
        val id: String,
        val arcadeId: String = "",
        val machineId: String = "",
        val userId: String,
        val username: String,
        val avatarUrl: String,
        val isTwoPlayer: Boolean,
        val timestamp: Long,
        val status: QueueStatus = QueueStatus.WAITING
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
        fun getDisplayAvatarUrl(): String {
                return if (avatarUrl.isNotEmpty()) avatarUrl
                else "https://api.dicebear.com/7.x/avataaars/svg?seed=$username"
        }
}

/** Represents a community post, optionally tied to an arcade. */
data class Post(
        val id: String,
        val authorId: String = "",
        val authorName: String,
        val arcadeId: String = "", // For filtering by arcade community
        val arcadeName: String = "", // For display
        val content: String,
        val timestamp: Long,
        val likes: Int,
        val comments: Int = 0
)
