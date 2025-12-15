package edu.uc.intprog32.escarro.myapplication.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.uc.intprog32.escarro.myapplication.data.model.Arcade
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class RealArcadeRepository(
    private val context: android.content.Context
) : ArcadeRepository {
    private val firestore: FirebaseFirestore = Firebase.firestore
    private val _arcades = MutableStateFlow<List<Arcade>>(emptyList())
    override val arcades: StateFlow<List<Arcade>> = _arcades.asStateFlow()

    override suspend fun refreshArcades() {
        try {
            val snapshot = firestore.collection("arcades")
                .get()
                .await()

            val fetchedArcades = snapshot.documents.mapNotNull { doc ->
                try {
                    val lat = doc.getDouble("latitude") ?: 0.0
                    val lng = doc.getDouble("longitude") ?: 0.0
                    
                    var arcade = Arcade(
                        id = doc.id,
                        name = doc.getString("name") ?: "Unknown Agent",
                        address = doc.getString("address") ?: "Unknown Location",
                        city = doc.getString("city") ?: "Unknown City",
                        latitude = lat,
                        longitude = lng,
                        distanceKm = 0.0, 
                        machineCount = doc.getLong("machineCount")?.toInt() ?: 2,
                        currentQueueSize = doc.getLong("queueSize")?.toInt() ?: 0,
                        isOpen = doc.getBoolean("isOpen") ?: true
                    )
                    
                    // Trigger geocoding if coordinates are missing (0.0)
                    if (lat == 0.0 && lng == 0.0) {
                        backfillCoordinates(arcade)
                    }
                    
                    arcade
                } catch (e: Exception) {
                    Log.e("RealArcadeRepo", "Error parsing arcade ${doc.id}", e)
                    null
                }
            }
            _arcades.value = fetchedArcades
        } catch (e: Exception) {
            Log.e("RealArcadeRepo", "Error fetching arcades", e)
        }
    }

    private suspend fun backfillCoordinates(arcade: Arcade) {
        // Run in background to avoid blocking UI or main fetch
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            val geocoder = edu.uc.intprog32.escarro.myapplication.data.location.ArcadeGeocoder(context)
            val coords = geocoder.getCoordinatesFromAddress(arcade.address)
            if (coords != null) {
                try {
                    firestore.collection("arcades").document(arcade.id)
                        .update(mapOf("latitude" to coords.first, "longitude" to coords.second))
                        .await()
                    Log.d("RealArcadeRepo", "Backfilled coordinates for ${arcade.name}")
                } catch (e: Exception) {
                    Log.e("RealArcadeRepo", "Failed to update coords for ${arcade.name}", e)
                }
            }
        }
    }
}
