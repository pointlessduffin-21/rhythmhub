package edu.uc.intprog32.rhythmhub.data.repository

import edu.uc.intprog32.rhythmhub.data.model.Arcade
import kotlinx.coroutines.flow.StateFlow

interface ArcadeRepository {
    val arcades: StateFlow<List<Arcade>>
    suspend fun refreshArcades()
}

