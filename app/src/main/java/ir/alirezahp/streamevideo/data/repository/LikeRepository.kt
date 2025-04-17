package ir.alirezahp.streamevideo.data.repository

import ir.alirezahp.streamevideo.data.db.AppDatabase
import ir.alirezahp.streamevideo.data.model.Video

class LikeRepository(private val database: AppDatabase) {

    suspend fun getLikes(userId: Int): List<Video> {
        return database.likeDao().getLikes(userId).map { like ->
            Video(
                id = like.id,
                title = like.title ?: "",
                url = like.url ?: "",
                duration = 10f
            )
        }
    }
}