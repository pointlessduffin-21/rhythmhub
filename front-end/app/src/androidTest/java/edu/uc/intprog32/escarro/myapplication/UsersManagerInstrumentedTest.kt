package edu.uc.intprog32.escarro.myapplication

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UsersManagerInstrumentedTest {
    @Test
    fun adminExistsAfterMigration() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        UsersManager.migrateLegacy(ctx)
        val users = UsersManager.getUsers(ctx)
        assertTrue("Admin should exist", users.containsKey("admin"))
        assertEquals("admin", users["admin"])
    }

    @Test
    fun createUpdateDeleteUserFlow() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        UsersManager.migrateLegacy(ctx)

        // Ensure clean state for test user
        UsersManager.deleteUser(ctx, "test_user")

        // Create
        val created = UsersManager.createUser(ctx, "test_user", "pass1")
        assertTrue(created)
        var users = UsersManager.getUsers(ctx)
        assertTrue(users.containsKey("test_user"))
        assertEquals("pass1", users["test_user"])

        // Update
        val updated = UsersManager.updateUser(ctx, "test_user", "pass2")
        assertTrue(updated)
        users = UsersManager.getUsers(ctx)
        assertEquals("pass2", users["test_user"])

        // Delete
        val deleted = UsersManager.deleteUser(ctx, "test_user")
        assertTrue(deleted)
        users = UsersManager.getUsers(ctx)
        assertFalse(users.containsKey("test_user"))
    }
}

