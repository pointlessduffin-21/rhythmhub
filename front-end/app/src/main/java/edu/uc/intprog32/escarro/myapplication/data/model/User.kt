package edu.uc.intprog32.escarro.myapplication.data.model

/**
 * Data class representing a user in the RhythmHub application.
 *
 * @property username The unique username of the user
 * @property password The user's password (stored as plain text in SharedPreferences for MVP)
 */
data class User(
    val username: String,
    val password: String
)
