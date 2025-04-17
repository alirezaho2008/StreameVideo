package ir.alirezahp.streamevideo.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ir.alirezahp.streamevideo.data.model.AdVideo
import ir.alirezahp.streamevideo.data.model.Like
import ir.alirezahp.streamevideo.data.model.PlaybackState


@Database(entities = [Like::class, AdVideo::class, PlaybackState::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun likeDao(): LikeDao
    abstract fun adVideoDao(): AdVideoDao
    abstract fun playbackStateDao(): PlaybackStateDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDb(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stream_video_db"
                ).build().also { instance = it }
            }
        }
    }
}