package ir.alirezahp.streamevideo.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ir.alirezahp.streamevideo.R
import ir.alirezahp.streamevideo.data.db.AppDatabase
import ir.alirezahp.streamevideo.data.model.Like
import ir.alirezahp.streamevideo.data.model.Video
import ir.alirezahp.streamevideo.databinding.VideoItemBinding
import ir.alirezahp.streamevideo.ui.VideoPlayerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList
import java.util.Locale
import java.util.concurrent.TimeUnit

class VideoAdapter(private val db: AppDatabase, private val userId: Int) :
    PagingDataAdapter<Video, VideoAdapter.VideoViewHolder>(VideoDiffCallback()) {

    class VideoViewHolder(private val binding: VideoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindVid(video: Video, userId: Int, db: AppDatabase, position: Int, adapter: VideoAdapter) {
            binding.txtTitle.text = video.title

            if (userId == 1) {
                binding.txtVideoItemUser.text = "کاربر اول"
            }
            else{
                binding.txtVideoItemUser.text = "کاربر دوم"
            }

            val sec = video.duration.toLong()
            val min = TimeUnit.SECONDS.toMinutes(sec)
            val seconds = sec - TimeUnit.MINUTES.toSeconds(min)
            binding.txtDuration.text = String.format(Locale.getDefault(), "%02d:%02d", min, seconds)

            Glide.with(binding.ivThumbnail.context)
                .load(video.url)
                .centerCrop()
                .into(binding.ivThumbnail)

            CoroutineScope(Dispatchers.IO).launch {
                val isLiked = db.likeDao().getLikes(userId).any { it.videoId == video.id }
                CoroutineScope(Dispatchers.Main).launch {
                    if (isLiked) {
                        binding.btnLike.setImageResource(R.drawable.liked)
                    } else {
                        binding.btnLike.setImageResource(R.drawable.not_liked)
                    }
                }
            }

            binding.btnLike.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val likes = db.likeDao().getLikes(userId)
                    val alreadyLiked = likes.any { it.videoId == video.id }
                    if (alreadyLiked) {
                        db.likeDao().removeLike(video.id, userId)
                        CoroutineScope(Dispatchers.Main).launch {
                            binding.btnLike.setImageResource(R.drawable.not_liked)
                        }
                    } else {
                        val like = Like(
                            videoId = video.id,
                            title = video.title,
                            url = video.url,
                            duration = video.duration,
                            userId = userId
                        )
                        db.likeDao().addLike(like)
                        CoroutineScope(Dispatchers.Main).launch {
                            binding.btnLike.setImageResource(R.drawable.liked)
                        }
                    }
                }
            }

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, VideoPlayerActivity::class.java)
                intent.putExtra("video_url", video.url)
                intent.putExtra("video_index", position)
                intent.putExtra("video_id", video.id)
                intent.putExtra("video_title", video.title)
                intent.putExtra("video_duration", video.duration)

                val urlList = ArrayList<String>()
                for (i in 0 until adapter.itemCount) {
                    val video = adapter.getItem(i)
                    if (video != null) {
                        urlList.add(video.url)
                    }
                }
                intent.putStringArrayListExtra("video_url_list", urlList)

                val idList = ArrayList<Int>()
                for (i in 0 until adapter.itemCount) {
                    val video = adapter.getItem(i)
                    if (video != null) {
                        idList.add(video.id)
                    }
                }
                intent.putIntegerArrayListExtra("video_id_list", idList)
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val bind = VideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(bind)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val vid = getItem(position)
        if (vid != null) {
            holder.bindVid(vid, userId, db, position, this)
        }
    }
}

class VideoDiffCallback : DiffUtil.ItemCallback<Video>() {
    override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
        return oldItem == newItem
    }
}