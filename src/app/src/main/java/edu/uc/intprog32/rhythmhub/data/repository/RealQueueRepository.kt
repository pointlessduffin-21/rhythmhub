package edu.uc.intprog32.rhythmhub.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.uc.intprog32.rhythmhub.data.model.QueueItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RealQueueRepository : QueueRepository {
    private val firestore: FirebaseFirestore = Firebase.firestore

    override fun getQueue(arcadeId: String): Flow<List<QueueItem>> = callbackFlow {
        // Listen to the 'queue' subcollection of the specific arcade
        // Ordered by timestamp (FIFO)
        val subscription =
                firestore
                        .collection("arcades")
                        .document(arcadeId)
                        .collection("queue")
                        .orderBy("timestamp", Query.Direction.ASCENDING)
                        .addSnapshotListener { snapshot, e ->
                            if (e != null) {
                                Log.w("RealQueueRepo", "Listen failed.", e)
                                return@addSnapshotListener
                            }

                            if (snapshot != null) {
                                val queueItems =
                                        snapshot.documents.mapNotNull { doc ->
                                            try {
                                                QueueItem(
                                                        id = doc.id,
                                                        userId = doc.getString("userId") ?: "",
                                                        username = doc.getString("username")
                                                                        ?: "Unknown",
                                                        avatarUrl = doc.getString("avatarUrl")
                                                                        ?: "",
                                                        isTwoPlayer = doc.getBoolean("isTwoPlayer")
                                                                        ?: false,
                                                        timestamp = doc.getLong("timestamp") ?: 0L
                                                )
                                            } catch (e: Exception) {
                                                null
                                            }
                                        }
                                trySend(queueItems)
                            }
                        }

        awaitClose { subscription.remove() }
    }

    override suspend fun joinQueue(arcadeId: String, userId: String, isTwoPlayer: Boolean) {
        try {
            val newItem =
                    hashMapOf(
                            "userId" to userId,
                            "username" to userId, // Use userId as username for now
                            "avatarUrl" to
                                    "https://api.dicebear.com/7.x/avataaars/svg?seed=$userId",
                            "isTwoPlayer" to isTwoPlayer,
                            "timestamp" to System.currentTimeMillis()
                    )

            // Add to subcollection
            firestore
                    .collection("arcades")
                    .document(arcadeId)
                    .collection("queue")
                    .add(newItem)
                    .await()

            // Ideally also increment arcade.currentQueueSize here via specific API or Cloud
            // Function
        } catch (e: Exception) {
            Log.e("RealQueueRepo", "Error joining queue", e)
        }
    }

    override suspend fun leaveQueue(arcadeId: String, userId: String) {
        try {
            val querySnapshot =
                    firestore
                            .collection("arcades")
                            .document(arcadeId)
                            .collection("queue")
                            .whereEqualTo("userId", userId)
                            .get()
                            .await()

            for (doc in querySnapshot) {
                doc.reference.delete()
            }
        } catch (e: Exception) {
            Log.e("RealQueueRepo", "Error leaving queue", e)
        }
    }
}
