package com.ridianputra.storyapp.ui.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.ridianputra.storyapp.DataDummy
import com.ridianputra.storyapp.data.Repository
import com.ridianputra.storyapp.data.network.response.ListStoryItem
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
class MapViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: Repository
    private lateinit var mapViewModel: MapViewModel
    private val dummyStories = DataDummy.generateStoriesList()
    private val dummyToken = "Token"

    @Before
    fun setUp() {
        mapViewModel = MapViewModel(repository)
    }

    @Test
    fun `when Get Stories Should Not Null and Return Success`() {
        val expectedData = MutableLiveData<Result<List<ListStoryItem>>>()
        expectedData.value = Result.Success(dummyStories)
        `when`(mapViewModel.getStoriesMap(dummyToken)).thenReturn(expectedData)
        val actualData = mapViewModel.getStoriesMap(dummyToken).getOrAwaitValue()
        verify(repository).getStoriesMap(dummyToken)
        assertNotNull(actualData)
        assertTrue(actualData is Result.Success)
        assertEquals(dummyStories.size, (actualData as Result.Success).data.size)
    }

    @Test
    fun `when Network Error Should Return Error`() {
        val listStories = MutableLiveData<Result<List<ListStoryItem>>>()
        listStories.value = Result.Error("Error")
        `when`(mapViewModel.getStoriesMap(dummyToken)).thenReturn(listStories)
        val actualData = mapViewModel.getStoriesMap(dummyToken).getOrAwaitValue()
        verify(repository).getStoriesMap(dummyToken)
        assertNotNull(actualData)
        assertTrue(actualData is Result.Error)
    }
}