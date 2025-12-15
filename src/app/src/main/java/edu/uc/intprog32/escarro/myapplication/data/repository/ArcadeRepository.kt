package edu.uc.intprog32.escarro.myapplication.data.repository

import edu.uc.intprog32.escarro.myapplication.data.model.Arcade
import kotlinx.coroutines.flow.StateFlow

interface ArcadeRepository {
    val arcades: StateFlow<List<Arcade>>
    suspend fun refreshArcades()
}
