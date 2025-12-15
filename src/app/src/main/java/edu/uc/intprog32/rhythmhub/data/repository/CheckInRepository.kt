package edu.uc.intprog32.rhythmhub.data.repository

import android.location.Location
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.uc.intprog32.rhythmhub.data.model.Arcade
import kotlinx.coroutines.tasks.await

interface CheckInRepository {
    suspend fun attemptCheckIn(arcade: Arcade, userLocation: Location, userId: String): Result<Int>
}

class RealCheckInRepository : CheckInRepository {
    private val firestore: FirebaseFirestore = Firebase.firestore

    // Constant: Max distance in meters
    private val GEOFENCE_RADIUS_METERS = 200.0f

    // Constant: XP awarded
    private val CHECKIN_XP = 50

    override suspend fun attemptCheckIn(
            arcade: Arcade,
            userLocation: Location,
            userId: String
    ): Result<Int> {
        return try {
            val arcadeLocation = Location("ArcadeProvider")
            arcadeLocation.latitude = arcade.latitude
            arcadeLocation.longitude = arcade.longitude

            val distance = userLocation.distanceTo(arcadeLocation)

            if (distance <= GEOFENCE_RADIUS_METERS) {
                // Verify Success!
                // 1. Log Check-in to Firestore
                val checkIn =
                        hashMapOf(
                                "userId" to userId,
                                "arcadeId" to arcade.id,
                                "arcadeName" to arcade.name,
                                "timestamp" to System.currentTimeMillis(),
                                "verified" to true
                        )
                firestore.collection("checkins").add(checkIn).await()

                // 2. Award XP (Atomic Increment)
                try {
                    val userRef = firestore.collection("users").document(userId)
                    // Use field increment for atomic update
                    userRef.update(
                                    "xp",
                                    com.google.firebase.firestore.FieldValue.increment(
                                            CHECKIN_XP.toLong()
                                    )
                            )
                            .await()
                    Log.d("CheckInRepo", "XP Awarded to $userId")
                } catch (e: Exception) {
                    Log.w("CheckInRepo", "Could not update XP (User doc might not exist)", e)
                }

                Result.success(CHECKIN_XP)
            } else {
                Result.failure(Exception("Too far away! Distance: ${distance.toInt()}m"))
            }
        } catch (e: Exception) {
            Log.e("CheckInRepo", "Check-in failed", e)
            Result.failure(e)
        }
    }
}

