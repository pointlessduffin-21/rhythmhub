package edu.uc.intprog32.escarro.myapplication

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.widget.*

class UserManagementActivity : Activity() {
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)

        UsersManager.migrateLegacy(this)

        listView = findViewById(R.id.userListView)
        val addBtn = findViewById<Button>(R.id.addUserButton)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        refreshUsers()

        addBtn.setOnClickListener { showAddUserDialog() }

        listView.setOnItemClickListener { _, _, position, _ ->
            val username = adapter.getItem(position) ?: return@setOnItemClickListener
            showUserActionsDialog(username)
        }
    }

    private fun refreshUsers() {
        val users = UsersManager.getUsers(this)
        val names = users.keys.toList().sorted()
        adapter.clear()
        adapter.addAll(names)
        adapter.notifyDataSetChanged()
    }

    private fun showAddUserDialog() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 0)
        }
        val usernameInput = EditText(this).apply { hint = "Username" }
        val passwordInput = EditText(this).apply { hint = "Password" }
        layout.addView(usernameInput)
        layout.addView(passwordInput)

        AlertDialog.Builder(this)
            .setTitle("Add User")
            .setView(layout)
            .setPositiveButton("Create") { dialog, _ ->
                val u = usernameInput.text.toString().trim()
                val p = passwordInput.text.toString()
                if (u.isEmpty() || p.isEmpty()) {
                    Toast.makeText(this, "Please provide username and password", Toast.LENGTH_SHORT).show()
                } else {
                    val ok = UsersManager.createUser(this, u, p)
                    if (ok) {
                        Toast.makeText(this, "User created", Toast.LENGTH_SHORT).show()
                        refreshUsers()
                    } else {
                        Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showUserActionsDialog(username: String) {
        val options = arrayOf("Edit Password", "Delete User", "Cancel")
        AlertDialog.Builder(this)
            .setTitle(username)
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> showEditPasswordDialog(username)
                    1 -> confirmDeleteUser(username)
                    else -> dialog.dismiss()
                }
            }
            .show()
    }

    private fun showEditPasswordDialog(username: String) {
        val input = EditText(this).apply { hint = "New password" }
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 0)
            addView(input)
        }
        AlertDialog.Builder(this)
            .setTitle("Edit Password for $username")
            .setView(container)
            .setPositiveButton("Save") { dialog, _ ->
                val newPass = input.text.toString()
                if (newPass.isEmpty()) {
                    Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
                } else {
                    val ok = UsersManager.updateUser(this, username, newPass)
                    if (ok) {
                        Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to update user", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDeleteUser(username: String) {
        if (username == "admin") {
            Toast.makeText(this, "Cannot delete admin user", Toast.LENGTH_SHORT).show()
            return
        }
        AlertDialog.Builder(this)
            .setTitle("Delete $username?")
            .setMessage("This action cannot be undone")
            .setPositiveButton("Delete") { dialog, _ ->
                val ok = UsersManager.deleteUser(this, username)
                if (ok) {
                    // if deleted user was current user, clear current user
                    if (UsersManager.getCurrentUser(this) == username) {
                        UsersManager.clearCurrentUser(this)
                        UsersManager.setRemember(this, false)
                    }
                    Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show()
                    refreshUsers()
                } else {
                    Toast.makeText(this, "Failed to delete user", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

