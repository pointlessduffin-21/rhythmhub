package edu.uc.intprog32.rhythmhub.presentation.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import edu.uc.intprog32.rhythmhub.data.model.Post
import edu.uc.intprog32.rhythmhub.data.repository.CommunityRepository
import edu.uc.intprog32.rhythmhub.data.repository.RealCommunityRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CommunityViewModel(private val repository: CommunityRepository) : ViewModel() {
    val posts: StateFlow<List<Post>> = repository.posts

    fun addPost(content: String) {
        viewModelScope.launch {
            repository.addPost(
                    author = "Current User", // TODO: Real Auth
                    content = content
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
                object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(
                            modelClass: Class<T>,
                            extras: CreationExtras
                    ): T {
                        return CommunityViewModel(RealCommunityRepository()) as T
                    }
                }
    }
}

