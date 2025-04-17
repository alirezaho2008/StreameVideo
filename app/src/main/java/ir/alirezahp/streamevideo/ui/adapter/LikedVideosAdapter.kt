package ir.alirezahp.streamevideo.ui.adapter

import android.content.Context.MODE_PRIVATE
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ir.alirezahp.streamevideo.R
import ir.alirezahp.streamevideo.data.model.Video
import ir.alirezahp.streamevideo.databinding.VideoItemBinding
import ir.alirezahp.streamevideo.helper.G
import java.util.Locale
import java.util.concurrent.TimeUnit

class LikedVideosAdapter(private val videos: List<Video>) :
    RecyclerView.Adapter<LikedVideosAdapter.LikedViewHolder>() {

    class LikedViewHolder(private val binding: VideoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var userId: Int = 1

        fun bindVideo(vid: Video) {
            binding.txtTitle.text = vid.title

            userId = G.context?.getSharedPreferences("app_prefs", MODE_PRIVATE)!!.getInt("selected_user_id", 1)

            if (userId == 1) {
                binding.txtVideoItemUser.text = G.context!!.getString(R.string.User1)
            }
            else{
                binding.txtVideoItemUser.text =  G.context!!.getString(R.string.User2)
            }

            val sec = vid.duration.toLong()
            val min = TimeUnit.SECONDS.toMinutes(sec)
            val seconds = sec - TimeUnit.MINUTES.toSeconds(min)
            binding.txtDuration.text = String.format(Locale.getDefault(), "%02d:%02d", min, seconds)

            Glide.with(binding.ivThumbnail.context)
                .load(vid.url)
                .centerCrop()
                .into(binding.ivThumbnail)

            binding.btnLike.setImageResource(R.drawable.liked)
            binding.btnLike.isEnabled = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikedViewHolder {
        val bind = VideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LikedViewHolder(bind)
    }

    override fun onBindViewHolder(holder: LikedViewHolder, position: Int) {
        holder.bindVideo(videos[position])
    }

    override fun getItemCount(): Int {
        return videos.size
    }
}