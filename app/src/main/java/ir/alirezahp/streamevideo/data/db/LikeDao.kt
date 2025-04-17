package ir.alirezahp.streamevideo.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ir.alirezahp.streamevideo.data.model.Like

@Dao
interface LikeDao {
    @Insert
    suspend fun addLike(like: Like)

    @Query("SELECT * FROM likes_table WHERE userId = :userId")
    suspend fun getLikes(userId: Int): List<Like>

    @Query("DELETE FROM likes_table WHERE videoId = :videoId AND userId = :userId")
    suspend fun removeLike(videoId: Int, userId: Int)
}