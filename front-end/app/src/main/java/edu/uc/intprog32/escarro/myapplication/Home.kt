package edu.uc.intprog32.escarro.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class Home : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        // Use UsersManager
        UsersManager.migrateLegacy(this)

        val profileName = findViewById<TextView>(R.id.profile_username)
        val username = UsersManager.getCurrentUser(this)
        val logoutBtn = findViewById<Button>(R.id.logoutButton)
        val nextBtn = findViewById<Button>(R.id.nextButton)
        val userMgmtBtn = findViewById<Button>(R.id.userManagementButton)

        profileName.text = "${username ?: ""}"

        // Show admin-only button
        if (username == "admin") {
            userMgmtBtn.visibility = Button.VISIBLE
        } else {
            userMgmtBtn.visibility = Button.GONE
        }

        userMgmtBtn.setOnClickListener {
            val intent = Intent(this, UserManagementActivity::class.java)
            startActivity(intent)
        }

        logoutBtn.setOnClickListener {
            Toast.makeText(this, "Logging out, byebye", Toast.LENGTH_LONG).show()

            // Clear remember flag and current user
            UsersManager.setRemember(this, false)
            UsersManager.clearCurrentUser(this)

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        nextBtn.setOnClickListener {

            Toast.makeText(this, "Next Page", Toast.LENGTH_LONG).show()

            val intent = Intent(this, Dummy::class.java)
            startActivity(intent)
        }
    }
}