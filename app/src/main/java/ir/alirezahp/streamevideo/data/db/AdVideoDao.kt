package ir.alirezahp.streamevideo.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ir.alirezahp.streamevideo.data.model.AdVideo

@Dao
interface AdVideoDao {
    @Insert
    suspend fun insertAdVideos(videos: List<AdVideo>)

    @Query("SELECT * FROM ad_videos")
    suspend fun getAdVideos(): List<AdVideo>
}