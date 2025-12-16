package edu.uc.intprog32.rhythmhub.data.repository

import edu.uc.intprog32.rhythmhub.data.model.Post
import kotlinx.coroutines.flow.StateFlow

interface CommunityRepository {
    val posts: StateFlow<List<Post>>
    suspend fun addPost(author: String, content: String)
    suspend fun upvotePost(postId: String)
    suspend fun downvotePost(postId: String)
}
