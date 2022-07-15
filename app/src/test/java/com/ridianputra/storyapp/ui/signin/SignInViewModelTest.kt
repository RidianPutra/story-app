package com.ridianputra.storyapp.ui.signin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.ridianputra.storyapp.DataDummy
import com.ridianputra.storyapp.data.Repository
import com.ridianputra.storyapp.data.network.response.LoginResult
import com.ridianputra.storyapp.data.Result
import com.ridianputra.storyapp.getOrAwaitValue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SignInViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: Repository
    private lateinit var signInViewModel: SignInViewModel
    private val dummyLogin = DataDummy.generateLoginResult()

    private val dummyEmail = "ridian@gmail.com"
    private val invalidEmail = "ridian@invalid"

    private val dummyPass = "password123" // valid pass is at least 6 characters
    private val invalidPass = "Abc45" // just 5 characters

    @Before
    fun setUp() {
        signInViewModel = SignInViewModel(repository)
    }

    @Test
    fun `when Get LoginResult Should Not Null and Return Success`() {
        val expectedData = MutableLiveData<Result<LoginResult>>()
        expectedData.value = Result.Success(dummyLogin)

        `when`(signInViewModel.getUser(dummyEmail, dummyPass)).thenReturn(expectedData)

        val actualData = signInViewModel.getUser(dummyEmail, dummyPass).getOrAwaitValue()
        verify(repository).getUser(dummyEmail, dummyPass)
        assertNotNull(actualData)
        assertTrue(actualData is Result.Success)
        assertEquals(dummyLogin, (actualData as Result.Success).data)
    }

    @Test
    fun `when Network Error Should Return Error`() {
        val loginResult = MutableLiveData<Result<LoginResult>>()
        loginResult.value = Result.Error("Error")
        `when`(signInViewModel.getUser(dummyEmail, dummyPass)).thenReturn(loginResult)
        val actualData = signInViewModel.getUser(dummyEmail, dummyPass).getOrAwaitValue()
        verify(repository).getUser(dummyEmail, dummyPass)
        assertNotNull(actualData)
        assertTrue(actualData is Result.Error)
    }

    @Test
    fun `when User Input Invalid Data or Wrong Data Should Return Error`() {
        val loginResult = MutableLiveData<Result<LoginResult>>()
        loginResult.value = Result.Error("Error")
        `when`(signInViewModel.getUser(invalidEmail, dummyPass)).thenReturn(loginResult)
        `when`(signInViewModel.getUser(dummyEmail, invalidPass)).thenReturn(loginResult)
        `when`(signInViewModel.getUser(invalidEmail, invalidPass)).thenReturn(loginResult)
        `when`(signInViewModel.getUser("", "")).thenReturn(loginResult)

        val actualData1 = signInViewModel.getUser(invalidEmail, dummyPass).getOrAwaitValue()
        val actualData2 = signInViewModel.getUser(dummyEmail, invalidPass).getOrAwaitValue()
        val actualData3 = signInViewModel.getUser(invalidEmail, invalidPass).getOrAwaitValue()
        val actualData4 = signInViewModel.getUser("", "").getOrAwaitValue()

        verify(repository).getUser(invalidEmail, dummyPass)
        verify(repository).getUser(dummyEmail, invalidPass)
        verify(repository).getUser(invalidEmail, invalidPass)
        verify(repository).getUser("", "")

        assertNotNull(actualData1)
        assertNotNull(actualData2)
        assertNotNull(actualData3)
        assertNotNull(actualData4)

        assertTrue(actualData1 is Result.Error)
        assertTrue(actualData2 is Result.Error)
        assertTrue(actualData3 is Result.Error)
        assertTrue(actualData4 is Result.Error)
    }
}