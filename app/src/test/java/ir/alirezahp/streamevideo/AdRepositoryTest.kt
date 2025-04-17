/*
package ir.alirezahp.streamevideo

import ir.alirezahp.streamevideo.data.api.ApiService
import ir.alirezahp.streamevideo.data.db.AdVideoDao
import ir.alirezahp.streamevideo.data.model.AdResponse
import ir.alirezahp.streamevideo.data.model.AdResult
import ir.alirezahp.streamevideo.data.model.AdVideo
import ir.alirezahp.streamevideo.data.repository.AdRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.mock

@RunWith(JUnit4::class)
class AdRepositoryTest {
    private lateinit var repository: AdRepository
    private lateinit var apiService: ApiService
    private lateinit var adVideoDao: AdVideoDao

    @Before
    fun setup() {
        apiService = mock(ApiService::class.java)
        adVideoDao = mock(AdVideoDao::class.java)
        repository = AdRepository(apiService, adVideoDao)
    }

    // Tests for fetchAndSaveAdVideos
    @Test
    fun `fetchAndSaveAdVideos returns success when API is successful`() = runBlocking {
        // Arrange
        val adVideos = listOf(AdVideo(id = 1, title = "Ad 1", url = "url1", duration = 30.0))
        val response = AdResponse(done = true, result = AdResult(adVideos))
        `when`(apiService.getAdVideos()).thenReturn(response)
        `when`(adVideoDao.insertAdVideos(adVideos)).thenReturn(Unit)

        // Act
        val result = repository.fetchAndSaveAdVideos()

        // Assert
        assert(result.isSuccess)
        verify(adVideoDao).insertAdVideos(adVideos)
    }

    @Test
    fun `fetchAndSaveAdVideos returns failure when API fails`() = runBlocking {
        // Arrange
        val response = AdResponse(done = false, result = AdResult(emptyList()))
        `when`(apiService.getAdVideos()).thenReturn(response)

        // Act
        val result = repository.fetchAndSaveAdVideos()

        // Assert
        assert(result.isFailure)
        assert(result.exceptionOrNull()?.message == "API request failed")
    }

    // Tests for getAdVideosFromDb
    @Test
    fun `getAdVideosFromDb returns videos from database`() = runBlocking {
        // Arrange
        val adVideos = listOf(AdVideo(id = 1, title = "تبلیغ 3", url = "https://sandbox.innova-co.com/sandbox-storage/advertises/3.mp4", duration = 19.74712))
        `when`(adVideoDao.getAdVideos()).thenReturn(adVideos)

        // Act
        val result = repository.getAdVideosFromDb()

        // Assert
        assert(result == adVideos)
    }

    @Test
    fun `getAdVideosFromDb returns empty list when database is empty`() = runBlocking {
        // Arrange
        `when`(adVideoDao.getAdVideos()).thenReturn(emptyList())

        // Act
        val result = repository.getAdVideosFromDb()

        // Assert
        assert(result.isEmpty())
    }
}*/
