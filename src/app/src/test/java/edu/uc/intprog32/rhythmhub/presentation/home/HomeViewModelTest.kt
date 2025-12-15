package edu.uc.intprog32.rhythmhub.presentation.home

import edu.uc.intprog32.rhythmhub.data.model.User
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var userRepository: UserRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userRepository = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUserData success`() = runTest {
        // Given
        val username = "testUser"
        val user = User(id = "1", username = username)
        
        coEvery { userRepository.getCurrentUser() } returns username
        coEvery { userRepository.getUser(username) } returns user

        // When
        viewModel = HomeViewModel(userRepository) // init calls loadUserData
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(user, viewModel.uiState.value.user)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadUserData no user`() = runTest {
        // Given
        coEvery { userRepository.getCurrentUser() } returns null

        // When
        viewModel = HomeViewModel(userRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNull(viewModel.uiState.value.user)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `logout calls repository`() = runTest {
        // Given
        viewModel = HomeViewModel(userRepository)
        
        // When
        viewModel.logout()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { userRepository.logout() }
    }
}
