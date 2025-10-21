package edu.uc.intprog32.escarro.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class Dummy : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy)

        val sharedPref = getSharedPreferences("UserPref", MODE_PRIVATE)
        val profileName = findViewById<TextView>(R.id.profile_username)
        val username = sharedPref.getString("username", null)
        val backBtn = findViewById<Button>(R.id.backButton)

        profileName.text = "$username"

        backBtn.setOnClickListener {

            Toast.makeText(this, "Back again", Toast.LENGTH_LONG).show()

            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
    }
}