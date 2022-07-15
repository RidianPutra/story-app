package com.ridianputra.storyapp.ui.signup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.ridianputra.storyapp.DataDummy
import com.ridianputra.storyapp.data.Repository
import com.ridianputra.storyapp.data.Result
import com.ridianputra.storyapp.data.network.response.SignUpResponse
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
class SignUpViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: Repository
    private lateinit var signUpViewModel: SignUpViewModel
    private val dummyRegister = DataDummy.generateSignUp()

    private val dummyName = "ridian putra"
    private val invalidName = ""

    private val dummyEmail = "ridian@gmail.com"
    private val invalidEmail = "ridian@invalid"

    private val dummyPass = "password123" // valid pass is at least 6 characters
    private val invalidPass = "Abc45" // just 5 characters

    @Before
    fun setUp() {
        signUpViewModel = SignUpViewModel(repository)
    }

    @Test
    fun `when User is Success to Create Account Should Not Null and Return Success`() {
        val expectedData = MutableLiveData<Result<SignUpResponse>>()
        expectedData.value = Result.Success(dummyRegister)

        `when`(signUpViewModel.createUser(dummyName, dummyEmail, dummyPass)).thenReturn(expectedData)

        val actualData = signUpViewModel.createUser(dummyName, dummyEmail, dummyPass).getOrAwaitValue()
        verify(repository).createUser(dummyName, dummyEmail, dummyPass)
        assertNotNull(actualData)
        assertTrue(actualData is Result.Success)
        assertEquals(dummyRegister, (actualData as Result.Success).data)
    }

    @Test
    fun `when Network Error Should Return Error`() {
        val signUpResp = MutableLiveData<Result<SignUpResponse>>()
        signUpResp.value = Result.Error("Error")
        `when`(signUpViewModel.createUser(dummyName, dummyEmail, dummyPass)).thenReturn(signUpResp)
        val actualData = signUpViewModel.createUser(dummyName, dummyEmail, dummyPass).getOrAwaitValue()
        verify(repository).createUser(dummyName, dummyEmail, dummyPass)
        assertNotNull(actualData)
        assertTrue(actualData is Result.Error)
    }

    @Test
    fun `when User Input Invalid Data or Wrong Data Should Return Error`() {
        val signUpResp = MutableLiveData<Result<SignUpResponse>>()
        signUpResp.value = Result.Error("Error")
        `when`(signUpViewModel.createUser(invalidName, dummyEmail, dummyPass)).thenReturn(signUpResp)
        `when`(signUpViewModel.createUser(dummyName, invalidEmail, dummyPass)).thenReturn(signUpResp)
        `when`(signUpViewModel.createUser(dummyName, dummyEmail, invalidPass)).thenReturn(signUpResp)
        `when`(signUpViewModel.createUser(invalidName, invalidEmail, invalidPass)).thenReturn(signUpResp)
        `when`(signUpViewModel.createUser("", "", "")).thenReturn(signUpResp)

        val actualData1 = signUpViewModel.createUser(invalidName, dummyEmail, dummyPass).getOrAwaitValue()
        val actualData2 = signUpViewModel.createUser(dummyName, invalidEmail, dummyPass).getOrAwaitValue()
        val actualData3 = signUpViewModel.createUser(dummyName, dummyEmail, invalidPass).getOrAwaitValue()
        val actualData4 = signUpViewModel.createUser(invalidName, invalidEmail, invalidPass).getOrAwaitValue()
        val actualData5 = signUpViewModel.createUser("", "", "").getOrAwaitValue()

        verify(repository).createUser(invalidName, dummyEmail, dummyPass)
        verify(repository).createUser(dummyName, invalidEmail, dummyPass)
        verify(repository).createUser(dummyName, dummyEmail, invalidPass)
        verify(repository).createUser(invalidName, invalidEmail, invalidPass)
        verify(repository).createUser("", "", "")

        assertNotNull(actualData1)
        assertNotNull(actualData2)
        assertNotNull(actualData3)
        assertNotNull(actualData4)
        assertNotNull(actualData5)

        assertTrue(actualData1 is Result.Error)
        assertTrue(actualData2 is Result.Error)
        assertTrue(actualData3 is Result.Error)
        assertTrue(actualData4 is Result.Error)
        assertTrue(actualData5 is Result.Error)
    }
}