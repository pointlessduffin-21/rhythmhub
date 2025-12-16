package edu.uc.intprog32.rhythmhub.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.uc.intprog32.rhythmhub.data.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class RealCommunityRepository : CommunityRepository {
    private val firestore: FirebaseFirestore = Firebase.firestore
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    override val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    init {
        // Start listening to posts immediately
        listenToPosts()
    }

    private fun listenToPosts() {
        firestore
                .collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("RealCommunityRepo", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val fetchedPosts =
                                snapshot.documents.mapNotNull { doc ->
                                    try {
                                        Post(
                                                id = doc.id,
                                                authorName = doc.getString("authorName")
                                                                ?: "Unknown",
                                                content = doc.getString("content") ?: "",
                                                timestamp = doc.getLong("timestamp") ?: 0L,
                                                likes = doc.getLong("likes")?.toInt() ?: 0
                                        )
                                    } catch (e: Exception) {
                                        null
                                    }
                                }
                        _posts.value = fetchedPosts
                    }
                }
    }

    override suspend fun addPost(author: String, content: String) {
        try {
            val newPost =
                    hashMapOf(
                            "authorName" to author,
                            "content" to content,
                            "timestamp" to System.currentTimeMillis(),
                            "likes" to 0
                    )
            firestore.collection("posts").add(newPost).await()
        } catch (e: Exception) {
            Log.e("RealCommunityRepo", "Error adding post", e)
        }
    }

    override suspend fun upvotePost(postId: String) {
        try {
            firestore
                    .collection("posts")
                    .document(postId)
                    .update("likes", com.google.firebase.firestore.FieldValue.increment(1))
                    .await()
        } catch (e: Exception) {
            Log.e("RealCommunityRepo", "Error upvoting post", e)
        }
    }

    override suspend fun downvotePost(postId: String) {
        try {
            // Get current likes first to prevent negative
            val doc = firestore.collection("posts").document(postId).get().await()
            val currentLikes = doc.getLong("likes")?.toInt() ?: 0
            if (currentLikes > 0) {
                firestore
                        .collection("posts")
                        .document(postId)
                        .update("likes", com.google.firebase.firestore.FieldValue.increment(-1))
                        .await()
            }
        } catch (e: Exception) {
            Log.e("RealCommunityRepo", "Error downvoting post", e)
        }
    }
}
