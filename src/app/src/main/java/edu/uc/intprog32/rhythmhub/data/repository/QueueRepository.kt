package edu.uc.intprog32.rhythmhub.data.repository

import edu.uc.intprog32.rhythmhub.data.model.QueueItem
import kotlinx.coroutines.flow.Flow

interface QueueRepository {
    fun getQueue(arcadeId: String): Flow<List<QueueItem>>
    suspend fun joinQueue(arcadeId: String, userId: String, isTwoPlayer: Boolean)
    suspend fun leaveQueue(arcadeId: String, userId: String)
}
