package edu.uc.intprog32.rhythmhub.presentation.auth

import edu.uc.intprog32.rhythmhub.data.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private lateinit var userRepository: UserRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userRepository = mockk(relaxed = true) // Relaxed to avoid stubbing everything
        viewModel = AuthViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateUsername updates state`() = runTest {
        viewModel.updateUsername("testUser")
        assertEquals("testUser", viewModel.uiState.value.username)
    }

    @Test
    fun `login success`() = runTest {
        // Given
        viewModel.updateUsername("validUser")
        viewModel.updatePassword("password")
        
        coEvery { userRepository.validateUser("validUser", "password") } returns true

        // When
        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.isSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
        
        coVerify { userRepository.setCurrentUser("validUser") }
    }

    @Test
    fun `login failure`() = runTest {
        // Given
        viewModel.updateUsername("wrongUser")
        viewModel.updatePassword("wrongPass")
        
        coEvery { userRepository.validateUser("wrongUser", "wrongPass") } returns false

        // When
        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("Invalid username or password", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `register success`() = runTest {
        // Given
        viewModel.updateUsername("newUser")
        viewModel.updatePassword("password")
        viewModel.updateConfirmPassword("password")
        
        coEvery { userRepository.registerUser("newUser", "password") } returns true

        // When
        viewModel.register()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.isSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
    }
}
