package edu.uc.intprog32.escarro.myapplication


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegisterActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Ensure users map exists and admin is present
        UsersManager.migrateLegacy(this)

        val inputUsername = findViewById<EditText>(R.id.newUsername)
        val inputPassword = findViewById<EditText>(R.id.newPassword)
        val confirmPassword = findViewById<EditText>(R.id.confirm_button)

        val register = findViewById<Button>(R.id.go_login)

        register.setOnClickListener {
            val username = inputUsername.text.toString()
            val password = inputPassword.text.toString()
            val confirm = confirmPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirm) {
                Toast.makeText(this, "Password Validation Failed", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val created = UsersManager.createUser(this, username, password)
            if (!created) {
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}