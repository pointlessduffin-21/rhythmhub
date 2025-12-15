package edu.uc.intprog32.escarro.myapplication.presentation.queue

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import edu.uc.intprog32.escarro.myapplication.data.model.QueueItem
import edu.uc.intprog32.escarro.myapplication.data.repository.MockQueueRepository
import edu.uc.intprog32.escarro.myapplication.data.repository.QueueRepository
import edu.uc.intprog32.escarro.myapplication.data.repository.RealQueueRepository
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    // Get arcadeId from navigation arguments
    private val arcadeId: String = checkNotNull(savedStateHandle["arcadeId"])

    // Combine queue data from Real Repository with AI logic
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<QueueUiState> = repository.getQueue(arcadeId)
        .combine(MutableStateFlow(0)) { queue, _ ->
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
            val isUserInQueue = queue.any { it.userId == "current_user" }

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
        viewModelScope.launch {
            repository.joinQueue(arcadeId, "Current User", isTwoPlayer)
        }
    }

    fun leaveQueue() {
        viewModelScope.launch {
            repository.leaveQueue(arcadeId, "current_user")
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val savedStateHandle = extras.createSavedStateHandle()
                return QueueViewModel(
                    RealQueueRepository(),
                    savedStateHandle
                ) as T
            }
        }
    }
}
