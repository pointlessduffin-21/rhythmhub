package edu.uc.intprog32.rhythmhub.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import edu.uc.intprog32.rhythmhub.data.model.Arcade
import kotlinx.coroutines.tasks.await
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// Coordinates for verified arcades
// Quantum SM Seaside: 10.2820, 123.8820 (Approx)
// Q Power Station JMall: 10.3315, 123.9065 (Approx)

/**
 * Manages location services and geofencing calculations.
 * Includes simulation mode for testing verification features.
 */
class LocationManager(private val context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    
    // Simulation Mode State
    private var isSimulationMode = false
    private var simulatedLocation: Location? = null

    /**
     * Gets the current device location (or simulated location).
     * Requires ACCESS_FINE_LOCATION permission to be granted.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        if (isSimulationMode && simulatedLocation != null) {
            return simulatedLocation
        }

        try {
            val cancellationTokenSource = CancellationTokenSource()
            return fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Checks if the user is within the check-in radius (100m) of an arcade.
     */
    suspend fun isNearArcade(arcade: Arcade): Boolean {
        // For the demo/verified arcades, we hardcode their coordinates mostly because
        // the MockArcadeRepository doesn't store LatLng yet (just address).
        // In a real app, Arcade model would have lat/lng fields.
        
        // Mapping ID to Coordinates for verified arcades
        val targetLat = when(arcade.id) {
            "1" -> 10.2820 // Quantum SM Seaside
            "2" -> 10.3315 // Q Power Station JMall
            else -> 0.0
        }
        val targetLng = when(arcade.id) {
            "1" -> 123.8820
            "2" -> 123.9065
            else -> 0.0
        }

        val userLocation = getCurrentLocation() ?: return false
        
        val results = FloatArray(1)
        Location.distanceBetween(
            userLocation.latitude,
            userLocation.longitude,
            targetLat,
            targetLng,
            results
        )
        
        // Check if distance is <= 100 meters
        return results[0] <= 100
    }

    /**
     * Enables simulation mode and sets location to a specific arcade.
     * Useful for testing "Verified Check-In" without moving.
     */
    fun setSimulationMode(enabled: Boolean, arcadeId: String? = null) {
        isSimulationMode = enabled
        if (enabled && arcadeId != null) {
            val loc = Location("simulated")
            when(arcadeId) {
                "1" -> { // Quantum SM Seaside
                    loc.latitude = 10.2820
                    loc.longitude = 123.8820
                }
                "2" -> { // Q Power Station JMall
                    loc.latitude = 10.3315
                    loc.longitude = 123.9065
                }
            }
            // Add some random noise to make it realistic (within 5-10 meters)
            loc.latitude += 0.00005 
            loc.time = System.currentTimeMillis()
            simulatedLocation = loc
        } else {
            simulatedLocation = null
        }
    }
}

