package edu.uc.intprog32.escarro.myapplication


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast

class LoginActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Ensure users map exists and admin is present
        UsersManager.migrateLegacy(this)

        val input_username = findViewById<EditText>(R.id.editText)
        val input_password = findViewById<EditText>(R.id.editPassword)
        val rememberCheckBox = findViewById<CheckBox>(R.id.rememberCheckBox)

        // If remember and a current user exists, auto-login
        val remembered = UsersManager.getRemember(this)
        val currentUser = UsersManager.getCurrentUser(this)
        if (remembered && !currentUser.isNullOrEmpty()) {
            // proceed to Home directly
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Prefill username if current user exists
        if (!currentUser.isNullOrEmpty()) {
            input_username.setText(currentUser)
            rememberCheckBox.isChecked = remembered
        }

        val login_button = findViewById<Button>(R.id.loginButton)

        login_button.setOnClickListener {
            val username = input_username.text.toString()
            val password = input_password.text.toString()

            if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
                Toast.makeText(this, "Username or Password is empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!UsersManager.validateUser(this, username, password)) {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Successful login
            UsersManager.setCurrentUser(this, username)
            val editorRemember = rememberCheckBox.isChecked
            UsersManager.setRemember(this, editorRemember)

            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()

        }
        val register_button = findViewById<Button>(R.id.registerButton)
        register_button.setOnClickListener {
            Log.e("Sample","Clicky")

            Toast.makeText(this, "Register", Toast.LENGTH_LONG).show()

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }



    }
}