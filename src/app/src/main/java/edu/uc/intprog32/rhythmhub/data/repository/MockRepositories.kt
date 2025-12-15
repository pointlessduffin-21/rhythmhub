package edu.uc.intprog32.rhythmhub.data.repository

import edu.uc.intprog32.rhythmhub.data.model.Arcade
import edu.uc.intprog32.rhythmhub.data.model.Post
import edu.uc.intprog32.rhythmhub.data.model.QueueItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

object MockArcadeRepository : ArcadeRepository {
    private val _arcades = MutableStateFlow<List<Arcade>>(emptyList())
    override val arcades = _arcades.asStateFlow()

    init {
        // Seed initial data - Verified Cebu Locations
        _arcades.value = listOf(
            Arcade(
                id = "1", 
                name = "Quantum SM Seaside Cebu", 
                address = "3104-3106 SM Seaside Complex, SRP, Cebu City", 
                city = "Cebu City",
                latitude = 10.2818,
                longitude = 123.8804,
                distanceKm = 5.8, 
                machineCount = 2, 
                currentQueueSize = 4, 
                isOpen = true
            ),
            Arcade(
                id = "2", 
                name = "Q Power Station SM JMall Cebu", 
                address = "165 A.S. Fortuna St, Mandaue City", 
                city = "Mandaue City",
                latitude = 10.3315,
                longitude = 123.9317,
                distanceKm = 6.5, 
                machineCount = 2, 
                currentQueueSize = 0, 
                isOpen = true
            )
        )
    }

    override suspend fun refreshArcades() {
        delay(500) // Simulate network delay
        // Randomly update queue sizes to make it feel "live"
        _arcades.update { currentList ->
            currentList.map { arcade ->
                if (arcade.isOpen) {
                    arcade.copy(currentQueueSize = (arcade.currentQueueSize + Random.nextInt(-1, 2)).coerceAtLeast(0))
                } else arcade
            }
        }
    }
}

object MockQueueRepository : QueueRepository {
    private val _queue = MutableStateFlow<List<QueueItem>>(emptyList())
    // Global queue for mock simplicity, ignores arcadeId
    
    override fun getQueue(arcadeId: String): Flow<List<QueueItem>> = _queue.asStateFlow()

    init {
        // Seed with fake players
        _queue.value = listOf(
            QueueItem("101", "u1", "RhythmMaster99", "https://api.dicebear.com/7.x/avataaars/svg?seed=Felix", false, System.currentTimeMillis()),
            QueueItem("102", "u2", "MaimaiGod", "https://api.dicebear.com/7.x/avataaars/svg?seed=Aneka", true, System.currentTimeMillis() + 60000),
            QueueItem("103", "u3", "NewbieOne", "https://api.dicebear.com/7.x/avataaars/svg?seed=Bob", false, System.currentTimeMillis() + 120000)
        )
    }

    override suspend fun joinQueue(arcadeId: String, username: String, isTwoPlayer: Boolean) {
        delay(300)
        val newItem = QueueItem(
            id = Random.nextLong().toString(),
            userId = "current_user",
            username = username,
            avatarUrl = "https://api.dicebear.com/7.x/avataaars/svg?seed=$username",
            isTwoPlayer = isTwoPlayer,
            timestamp = System.currentTimeMillis()
        )
        _queue.update { it + newItem }
    }

    override suspend fun leaveQueue(arcadeId: String, userId: String) {
        delay(300)
        _queue.update { it.filter { item -> item.userId != userId } }
    }

    // Simulate queue moving
    fun simulateQueueMovement() {
        if (_queue.value.isNotEmpty() && Random.nextBoolean()) { // 50% chance to move
            _queue.update { it.drop(1) }
        }
    }
}

object MockCommunityRepository : CommunityRepository {
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    override val posts = _posts.asStateFlow()

    init {
        _posts.value = listOf(
            Post("1", "ArcadeFan22", "Timezone Ayala is packed right now! 12 people in line...", System.currentTimeMillis() - 3600000, 5),
            Post("2", "MaimaiPro", "Just got a new high score on Pandora Paradoxxx! AAA rank!", System.currentTimeMillis() - 7200000, 12),
            Post("3", "RhythmSeeker", "Anyone going to SM Seaside later? Looking for a duo partner.", System.currentTimeMillis() - 10800000, 2)
        )
    }

    override suspend fun addPost(author: String, content: String) {
        delay(300)
        val newPost = Post(
            id = Random.nextLong().toString(),
            authorName = author,
            content = content,
            timestamp = System.currentTimeMillis(),
            likes = 0
        )
        _posts.update { listOf(newPost) + it }
    }
}

