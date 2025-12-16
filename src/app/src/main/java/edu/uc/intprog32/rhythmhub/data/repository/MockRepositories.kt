package edu.uc.intprog32.rhythmhub.data.repository

import edu.uc.intprog32.rhythmhub.data.model.Arcade
import edu.uc.intprog32.rhythmhub.data.model.Machine
import edu.uc.intprog32.rhythmhub.data.model.Post
import edu.uc.intprog32.rhythmhub.data.model.QueueItem
import edu.uc.intprog32.rhythmhub.data.model.QueueStatus
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

object MockArcadeRepository : ArcadeRepository {
        private val _arcades = MutableStateFlow<List<Arcade>>(emptyList())
        override val arcades = _arcades.asStateFlow()

        init {
                // Seed initial data - Verified Cebu Locations with Machines
                // Queue sizes match MockQueueRepository seeded data
                _arcades.value =
                        listOf(
                                Arcade(
                                        id = "arcade_sm_seaside",
                                        name = "Quantum SM Seaside Cebu",
                                        address = "3104-3106 SM Seaside Complex, SRP, Cebu City",
                                        city = "Cebu City",
                                        latitude = 10.2818,
                                        longitude = 123.8804,
                                        distanceKm = 5.8,
                                        machineCount = 2,
                                        currentQueueSize = 3, // Matches seeded MockQueueRepository
                                        isOpen = true,
                                        machines =
                                                listOf(
                                                        Machine(
                                                                "m1_seaside",
                                                                "arcade_sm_seaside",
                                                                "Left Cabinet",
                                                                true
                                                        ),
                                                        Machine(
                                                                "m2_seaside",
                                                                "arcade_sm_seaside",
                                                                "Right Cabinet",
                                                                true
                                                        )
                                                )
                                ),
                                Arcade(
                                        id = "arcade_jmall",
                                        name = "Q Power Station SM JMall",
                                        address = "165 A.S. Fortuna St, Mandaue City",
                                        city = "Mandaue City",
                                        latitude = 10.3315,
                                        longitude = 123.9317,
                                        distanceKm = 6.5,
                                        machineCount = 2,
                                        currentQueueSize = 0, // Empty queue
                                        isOpen = true,
                                        machines =
                                                listOf(
                                                        Machine(
                                                                "m1_jmall",
                                                                "arcade_jmall",
                                                                "Machine 1",
                                                                true
                                                        ),
                                                        Machine(
                                                                "m2_jmall",
                                                                "arcade_jmall",
                                                                "Machine 2",
                                                                true
                                                        )
                                                )
                                ),
                                Arcade(
                                        id = "arcade_ayala",
                                        name = "Timezone Ayala Center Cebu",
                                        address = "Ayala Center Cebu, Cebu Business Park",
                                        city = "Cebu City",
                                        latitude = 10.3181,
                                        longitude = 123.9050,
                                        distanceKm = 3.2,
                                        machineCount = 1,
                                        currentQueueSize = 0, // Empty queue
                                        isOpen = true,
                                        machines =
                                                listOf(
                                                        Machine(
                                                                "m1_ayala",
                                                                "arcade_ayala",
                                                                "Solo Cabinet",
                                                                true
                                                        )
                                                )
                                )
                        )
        }

        override suspend fun refreshArcades() {
                delay(500) // Simulate network delay
                // Queue sizes are now synced dynamically - no random changes
        }

        // Update queue count for a specific arcade (called by MockQueueRepository)
        fun updateQueueCount(arcadeId: String, count: Int) {
                _arcades.update { currentList ->
                        currentList.map { arcade ->
                                if (arcade.id == arcadeId) {
                                        arcade.copy(currentQueueSize = count)
                                } else arcade
                        }
                }
        }
}

object MockQueueRepository : QueueRepository {
        private val _queue = MutableStateFlow<List<QueueItem>>(emptyList())

        // Filter queue by arcadeId for proper demo behavior
        override fun getQueue(arcadeId: String): Flow<List<QueueItem>> =
                _queue.asStateFlow().map { allItems -> allItems.filter { it.arcadeId == arcadeId } }

        // Helper to get total count for an arcade (for dynamic display)
        fun getQueueCountForArcade(arcadeId: String): Int =
                _queue.value.count { it.arcadeId == arcadeId }

        init {
                // Seed with fake players only at SM Seaside
                // JMall and Ayala are EMPTY for "first in queue" testing
                _queue.value =
                        listOf(
                                QueueItem(
                                        id = "101",
                                        arcadeId = "arcade_sm_seaside",
                                        machineId = "m1_seaside",
                                        userId = "u1",
                                        username = "RhythmMaster99",
                                        avatarUrl =
                                                "https://api.dicebear.com/7.x/avataaars/svg?seed=Felix",
                                        isTwoPlayer = false,
                                        timestamp = System.currentTimeMillis(),
                                        status = QueueStatus.PLAYING
                                ),
                                QueueItem(
                                        id = "102",
                                        arcadeId = "arcade_sm_seaside",
                                        machineId = "m1_seaside",
                                        userId = "u2",
                                        username = "MaimaiGod",
                                        avatarUrl =
                                                "https://api.dicebear.com/7.x/avataaars/svg?seed=Aneka",
                                        isTwoPlayer = true,
                                        timestamp = System.currentTimeMillis() + 60000,
                                        status = QueueStatus.WAITING
                                ),
                                QueueItem(
                                        id = "103",
                                        arcadeId = "arcade_sm_seaside",
                                        machineId = "m1_seaside",
                                        userId = "u3",
                                        username = "NewbieOne",
                                        avatarUrl =
                                                "https://api.dicebear.com/7.x/avataaars/svg?seed=Bob",
                                        isTwoPlayer = false,
                                        timestamp = System.currentTimeMillis() + 120000,
                                        status = QueueStatus.WAITING
                                )
                        )
        }

        override suspend fun joinQueue(arcadeId: String, userId: String, isTwoPlayer: Boolean) {
                delay(300)
                val newItem =
                        QueueItem(
                                id = Random.nextLong().toString(),
                                arcadeId = arcadeId,
                                machineId = "",
                                userId = userId,
                                username = userId,
                                avatarUrl =
                                        "https://api.dicebear.com/7.x/avataaars/svg?seed=$userId",
                                isTwoPlayer = isTwoPlayer,
                                timestamp = System.currentTimeMillis(),
                                status = QueueStatus.WAITING
                        )
                _queue.update { it + newItem }
                // Sync arcade count
                updateArcadeQueueCount(arcadeId)
        }

        override suspend fun leaveQueue(arcadeId: String, userId: String) {
                delay(300)
                _queue.update { it.filter { item -> item.userId != userId } }
                // Sync arcade count
                updateArcadeQueueCount(arcadeId)
        }

        // Update arcade queue count when queue changes
        private fun updateArcadeQueueCount(arcadeId: String) {
                val count = getQueueCountForArcade(arcadeId)
                MockArcadeRepository.updateQueueCount(arcadeId, count)
        }

        // Simulate queue moving
        fun simulateQueueMovement() {
                if (_queue.value.isNotEmpty() && Random.nextBoolean()) {
                        _queue.update { it.drop(1) }
                }
        }
}

object MockCommunityRepository : CommunityRepository {
        private val _posts = MutableStateFlow<List<Post>>(emptyList())
        override val posts = _posts.asStateFlow()

        init {
                _posts.value =
                        listOf(
                                Post(
                                        id = "1",
                                        authorId = "u_arcade_fan",
                                        authorName = "ArcadeFan22",
                                        arcadeId = "arcade_ayala",
                                        arcadeName = "Timezone Ayala",
                                        content =
                                                "Timezone Ayala is packed right now! 12 people in line...",
                                        timestamp = System.currentTimeMillis() - 3600000,
                                        likes = 5,
                                        comments = 2
                                ),
                                Post(
                                        id = "2",
                                        authorId = "u_maimai_pro",
                                        authorName = "MaimaiPro",
                                        arcadeId = "arcade_sm_seaside",
                                        arcadeName = "Quantum SM Seaside",
                                        content =
                                                "Just got a new high score on Pandora Paradoxxx! AAA rank!",
                                        timestamp = System.currentTimeMillis() - 7200000,
                                        likes = 12,
                                        comments = 5
                                ),
                                Post(
                                        id = "3",
                                        authorId = "u_rhythm_seeker",
                                        authorName = "RhythmSeeker",
                                        arcadeId = "",
                                        arcadeName = "General", // No specific arcade
                                        content =
                                                "Anyone going to SM Seaside later? Looking for a duo partner.",
                                        timestamp = System.currentTimeMillis() - 10800000,
                                        likes = 2,
                                        comments = 1
                                )
                        )
        }

        override suspend fun addPost(author: String, content: String) {
                delay(300)
                val newPost =
                        Post(
                                id = Random.nextLong().toString(),
                                authorId = "current_user_id",
                                authorName = author,
                                arcadeId = CheckInState.currentArcadeId ?: "",
                                arcadeName = CheckInState.currentArcadeName ?: "General",
                                content = content,
                                timestamp = System.currentTimeMillis(),
                                likes = 0,
                                comments = 0
                        )
                _posts.update { listOf(newPost) + it }
        }

        override suspend fun upvotePost(postId: String) {
                _posts.update { posts ->
                        posts.map { post ->
                                if (post.id == postId) {
                                        post.copy(likes = post.likes + 1)
                                } else post
                        }
                }
        }

        override suspend fun downvotePost(postId: String) {
                _posts.update { posts ->
                        posts.map { post ->
                                if (post.id == postId) {
                                        post.copy(likes = (post.likes - 1).coerceAtLeast(0))
                                } else post
                        }
                }
        }
}

/** Tracks the user's current check-in state for Community filtering. */
object CheckInState {
        var currentArcadeId: String? = null
                private set
        var currentArcadeName: String? = null
                private set
        var lastCheckInXp: Int = 0
                private set
        var lastCheckInTime: Long = 0
                private set

        // Cooldown: 5 minutes between check-ins (for demo, using 30 seconds)
        private const val CHECKIN_COOLDOWN_MS = 30_000L

        fun setCheckedIn(arcadeId: String, arcadeName: String, xp: Int) {
                currentArcadeId = arcadeId
                currentArcadeName = arcadeName
                lastCheckInXp = xp
                lastCheckInTime = System.currentTimeMillis()
        }

        fun clearCheckIn() {
                currentArcadeId = null
                currentArcadeName = null
                lastCheckInXp = 0
        }

        fun isCheckedIn(): Boolean = currentArcadeId != null

        fun canCheckIn(): Boolean {
                return System.currentTimeMillis() - lastCheckInTime > CHECKIN_COOLDOWN_MS
        }

        fun getRemainingCooldownSeconds(): Int {
                val elapsed = System.currentTimeMillis() - lastCheckInTime
                val remaining = (CHECKIN_COOLDOWN_MS - elapsed) / 1000
                return remaining.coerceAtLeast(0).toInt()
        }
}

/**
 * Mock CheckInRepository that always succeeds for Dev Mode testing. Awards 50 XP and updates
 * CheckInState. Includes cooldown to prevent spam.
 */
object MockCheckInRepository : CheckInRepository {
        private val CHECKIN_XP = 50

        override suspend fun attemptCheckIn(
                arcade: edu.uc.intprog32.rhythmhub.data.model.Arcade,
                userLocation: android.location.Location,
                userId: String
        ): Result<Int> {
                delay(500) // Simulate processing

                // Check cooldown
                if (!CheckInState.canCheckIn()) {
                        val remaining = CheckInState.getRemainingCooldownSeconds()
                        return Result.failure(
                                Exception("Please wait ${remaining}s before checking in again")
                        )
                }

                // Success in Dev Mode!
                CheckInState.setCheckedIn(arcade.id, arcade.name, CHECKIN_XP)

                return Result.success(CHECKIN_XP)
        }
}
