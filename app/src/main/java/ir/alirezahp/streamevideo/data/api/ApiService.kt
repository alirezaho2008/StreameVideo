package ir.alirezahp.streamevideo.data.api

import ir.alirezahp.streamevideo.data.model.AdResponse
import ir.alirezahp.streamevideo.data.model.VideoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET(Urls.VIDEOS_ENDPOINT)
    suspend fun getVideos(
        @Query("offset") offset: Int
    ): VideoResponse

    @GET(Urls.ADS_ENDPOINT)
    suspend fun getAdVideos(): AdResponse
}