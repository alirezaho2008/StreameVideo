package ir.alirezahp.streamevideo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playback_state")
data class PlaybackState(
    @PrimaryKey val userId: Int,
    val videoPlayed: Int = 0,
    val adIndexForPlay: Int = 0,
)