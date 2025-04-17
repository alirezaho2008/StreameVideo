package ir.alirezahp.streamevideo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ad_videos")
data class AdVideo(
    @PrimaryKey val id: Int,
    val title: String,
    val url: String,
    val duration: Double
)