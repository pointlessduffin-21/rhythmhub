package edu.uc.intprog32.rhythmhub.presentation.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import edu.uc.intprog32.rhythmhub.data.model.Arcade
import edu.uc.intprog32.rhythmhub.data.model.Post
import edu.uc.intprog32.rhythmhub.data.repository.ArcadeRepository
import edu.uc.intprog32.rhythmhub.data.repository.CheckInState
import edu.uc.intprog32.rhythmhub.data.repository.CommunityRepository
import edu.uc.intprog32.rhythmhub.data.repository.MockArcadeRepository
import edu.uc.intprog32.rhythmhub.data.repository.MockCommunityRepository
import edu.uc.intprog32.rhythmhub.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CommunityUiState(
        val posts: List<Post> = emptyList(),
        val arcadeFilters: List<Arcade> = emptyList(),
        val selectedArcadeId: String? = null, // null = "All"
        val isCheckedIn: Boolean = false,
        val checkedInArcadeName: String? = null
)

class CommunityViewModel(
        private val repository: CommunityRepository,
        private val userRepository: UserRepository,
        private val arcadeRepository: ArcadeRepository
) : ViewModel() {

    // Auto-select filter based on check-in state
    private val _selectedArcadeId = MutableStateFlow<String?>(CheckInState.currentArcadeId)
    val selectedArcadeId: StateFlow<String?> = _selectedArcadeId.asStateFlow()

    val uiState: StateFlow<CommunityUiState> =
            combine(repository.posts, arcadeRepository.arcades, _selectedArcadeId) {
                            posts,
                            arcades,
                            selectedId ->
                        val filteredPosts =
                                if (selectedId == null) {
                                    posts
                                } else {
                                    posts.filter { it.arcadeId == selectedId }
                                }
                        CommunityUiState(
                                posts = filteredPosts,
                                arcadeFilters = arcades,
                                selectedArcadeId = selectedId
                        )
                    }
                    .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5000),
                            initialValue = CommunityUiState()
                    )

    fun selectArcadeFilter(arcadeId: String?) {
        _selectedArcadeId.value = arcadeId
    }

    fun addPost(content: String, arcadeId: String?, arcadeName: String?) {
        viewModelScope.launch {
            val currentUser = userRepository.currentUser.value
            repository.addPost(author = currentUser?.username ?: "Anonymous", content = content)
        }
    }

    fun upvotePost(postId: String) {
        viewModelScope.launch { repository.upvotePost(postId) }
    }

    fun downvotePost(postId: String) {
        viewModelScope.launch { repository.downvotePost(postId) }
    }

    class Factory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return CommunityViewModel(
                    MockCommunityRepository,
                    userRepository,
                    MockArcadeRepository
            ) as
                    T
        }
    }
}
