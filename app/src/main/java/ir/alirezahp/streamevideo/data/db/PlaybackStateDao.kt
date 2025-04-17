package ir.alirezahp.streamevideo.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.alirezahp.streamevideo.data.model.PlaybackState

@Dao
interface PlaybackStateDao {
    @Insert
    suspend fun insert(state: PlaybackState)

    @Update
    suspend fun update(state: PlaybackState)

    @Query("SELECT * FROM playback_state WHERE userId = :userId")
    suspend fun getState(userId: Int): PlaybackState?

    @Query("DELETE FROM playback_state WHERE userId = :userId")
    suspend fun deleteState(userId: Int)
}