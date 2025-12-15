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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class QueueUiState(
    val queue: List<QueueItem> = emptyList(),
    val estimatedWaitMins: Int = 0,
    val crowdLevel: String = "Low",
    val aiAdvice: String = "Great time to play!",
    val isUserInQueue: Boolean = false
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
    val uiState: StateFlow<QueueUiState> = repository.getQueue(arcadeId)
        .combine(userRepository.currentUser) { queue, currentUser ->
            // AI Logic: Calculate wait time
            // Assumption: Avg game time is 3 mins per person (or pair)
            val avgGameTime = 3 // minutes
            val waitTime = queue.size * avgGameTime
            
            // AI Logic: Determine crowd level
            val crowdLevel = when {
                queue.size > 10 -> "High"
                queue.size > 5 -> "Medium"
                else -> "Low"
            }

            // AI Logic: Generate advice
            val advice = when(crowdLevel) {
                "High" -> "Queue is long. Maybe grab a snack first?"
                "Medium" -> "Expect a short wait. Good time to warm up!"
                "Low" -> "Machines are free! Go play now!"
                else -> "Enjoy the game!"
            }

            // Check if current user is in queue
            val isUserInQueue = queue.any { it.userId == currentUser?.id }

            QueueUiState(
                queue = queue,
                estimatedWaitMins = waitTime,
                crowdLevel = crowdLevel,
                aiAdvice = advice,
                isUserInQueue = isUserInQueue
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = QueueUiState()
        )

    init {
        // No simulation loop needed for Real Repository
    }

    fun joinQueue(isTwoPlayer: Boolean) {
        val user = userRepository.currentUser.value ?: return
        viewModelScope.launch {
            repository.joinQueue(arcadeId, user.id, isTwoPlayer)
        }
    }

    fun leaveQueue() {
        val user = userRepository.currentUser.value ?: return
        viewModelScope.launch {
            repository.leaveQueue(arcadeId, user.id)
        }
    }

    class Factory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras
        ): T {
            val savedStateHandle = extras.createSavedStateHandle()
            return QueueViewModel(
                RealQueueRepository(),
                userRepository,
                savedStateHandle
            ) as T
        }
    }
}

