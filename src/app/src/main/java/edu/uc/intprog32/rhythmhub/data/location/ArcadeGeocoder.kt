package edu.uc.intprog32.rhythmhub.data.location

import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.util.Log
import edu.uc.intprog32.rhythmhub.data.model.Arcade
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class ArcadeGeocoder(private val context: Context) {

    @Suppress("DEPRECATION")
    suspend fun getCoordinatesFromAddress(address: String): Pair<Double, Double>? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                // Use a broader search if specific address fails, append "Philippines"
                val searchString = "$address, Philippines"
                
                val addresses = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Modern async API (simplified for synchronous wrapping here)
                    geocoder.getFromLocationName(searchString, 1)
                } else {
                    geocoder.getFromLocationName(searchString, 1)
                }

                if (!addresses.isNullOrEmpty()) {
                    val location = addresses[0]
                    Pair(location.latitude, location.longitude)
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.e("ArcadeGeocoder", "Geocoding failed for $address", e)
                null
            }
        }
    }
}

