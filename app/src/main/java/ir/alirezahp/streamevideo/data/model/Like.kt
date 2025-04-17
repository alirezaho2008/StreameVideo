package ir.alirezahp.streamevideo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "likes_table")
data class Like(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val videoId: Int,
    val title: String?,
    val url: String?,
    val duration: Float,
    val userId: Int
)