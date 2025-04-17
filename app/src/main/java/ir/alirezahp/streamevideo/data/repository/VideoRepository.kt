package ir.alirezahp.streamevideo.data.repository

import ir.alirezahp.streamevideo.data.api.ApiService
import ir.alirezahp.streamevideo.data.model.Video
import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getVideos(offset: Int): Pair<List<Video>, Boolean> {
        val response = apiService.getVideos(offset)
        val videos = response.result.videos
        val isEnd = response.result.end

        return videos to isEnd
    }
}