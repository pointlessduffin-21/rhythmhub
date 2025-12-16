package edu.uc.intprog32.rhythmhub.presentation.queue

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import edu.uc.intprog32.rhythmhub.data.model.QueueItem
import edu.uc.intprog32.rhythmhub.data.repository.MockQueueRepository
import edu.uc.intprog32.rhythmhub.data.repository.QueueRepository
import edu.uc.intprog32.rhythmhub.data.repository.RealQueueRepository
import edu.uc.intprog32.rhythmhub.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class QueueUiState(
        val queue: List<QueueItem> = emptyList(),
        val estimatedWaitMins: Int = 0,
        val crowdLevel: String = "Low",
        val aiAdvice: String = "Great time to play!",
        val isUserInQueue: Boolean = false,
        val isFirstInQueue: Boolean = false,
        val userQueuePosition: Int = 0
)

class QueueViewModel(
        private val repository: QueueRepository,
        private val userRepository: UserRepository,
        savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Get arcadeId from navigation arguments
    private val arcadeId: String = checkNotNull(savedStateHandle["arcadeId"])

    // Combine queue data from Real Repository with AI logic
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<QueueUiState> =
            repository
                    .getQueue(arcadeId)
                    .combine(userRepository.currentUser) { queue, currentUser ->
                        // AI Logic: Calculate wait time
                        // Assumption: Avg game time is 3 mins per person (or pair)
                        val avgGameTime = 3 // minutes
                        val waitTime = queue.size * avgGameTime

                        // AI Logic: Determine crowd level
                        val crowdLevel =
                                when {
                                    queue.size > 10 -> "High"
                                    queue.size > 5 -> "Medium"
                                    else -> "Low"
                                }

                        // AI Logic: Generate advice
                        val advice =
                                when (crowdLevel) {
                                    "High" -> "Queue is long. Maybe grab a snack first?"
                                    "Medium" -> "Expect a short wait. Good time to warm up!"
                                    "Low" -> "Machines are free! Go play now!"
                                    else -> "Enjoy the game!"
                                }

                        // Check if current user is in queue and their position
                        val userIndex = queue.indexOfFirst { it.userId == currentUser?.id }
                        val isUserInQueue = userIndex >= 0
                        val isFirstInQueue = userIndex == 0
                        val userQueuePosition = if (userIndex >= 0) userIndex + 1 else 0

                        QueueUiState(
                                queue = queue,
                                estimatedWaitMins = waitTime,
                                crowdLevel = crowdLevel,
                                aiAdvice = advice,
                                isUserInQueue = isUserInQueue,
                                isFirstInQueue = isFirstInQueue,
                                userQueuePosition = userQueuePosition
                        )
                    }
                    .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5000),
                            initialValue = QueueUiState()
                    )

    // Message to show to user (Toast)
    private val _message = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)
    val message: StateFlow<String?> =
            _message.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun clearMessage() {
        _message.value = null
    }

    init {
        // No simulation loop needed for Real Repository
    }

    fun joinQueue(isTwoPlayer: Boolean) {
        val user = userRepository.currentUser.value ?: return
        viewModelScope.launch {
            repository.joinQueue(arcadeId, user.id, isTwoPlayer)
            _message.value = "You're in the queue! Wait for your turn."
        }
    }

    fun leaveQueue() {
        val user = userRepository.currentUser.value ?: return
        viewModelScope.launch {
            repository.leaveQueue(arcadeId, user.id)
            _message.value = "Left the queue."
        }
    }

    /** Called when user taps "Play Now" - marks them as actively playing. Awards XP for playing! */
    fun startPlaying() {
        val user = userRepository.currentUser.value ?: return
        viewModelScope.launch {
            // Award XP for playing (gamification!)
            val xpGained = 25
            userRepository.addXp(xpGained)
            _message.value = "🎮 Now Playing! +$xpGained XP"
        }
    }

    /** Called when user finishes playing - removes from queue, next person gets their turn. */
    fun finishPlaying() {
        val user = userRepository.currentUser.value ?: return
        viewModelScope.launch {
            repository.leaveQueue(arcadeId, user.id)
            _message.value = "Great game! 🎉 Thanks for playing!"
        }
    }

    class Factory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val savedStateHandle = extras.createSavedStateHandle()

            // Use Mock in Dev Mode for demo testing
            val queueRepo: QueueRepository =
                    if (edu.uc.intprog32.rhythmhub.data.AppConfig.isDev) {
                        MockQueueRepository
                    } else {
                        RealQueueRepository()
                    }

            return QueueViewModel(queueRepo, userRepository, savedStateHandle) as T
        }
    }
}
