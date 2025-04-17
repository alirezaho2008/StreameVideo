package ir.alirezahp.streamevideo.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ir.alirezahp.streamevideo.data.db.AppDatabase
import ir.alirezahp.streamevideo.data.repository.LikeRepository
import ir.alirezahp.streamevideo.databinding.ActivityLikedVideosBinding
import ir.alirezahp.streamevideo.ui.adapter.LikedVideosAdapter
import ir.alirezahp.streamevideo.viewmodel.FavoriteVideosViewModel
import ir.alirezahp.streamevideo.viewmodel.FavoriteVideosViewModelFactory

class FavoriteVideosActivity : BaseActivity() {
    private lateinit var binding: ActivityLikedVideosBinding
    private val userId by lazy {
        getSharedPreferences("app_prefs", MODE_PRIVATE).getInt("selected_user_id", 1)
    }

    private val viewModel: FavoriteVideosViewModel by viewModels {
        FavoriteVideosViewModelFactory(LikeRepository(AppDatabase.getDb(this)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLikedVideosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        setupRecyclerView()
        observeVideos()
        viewModel.loadLikes(userId)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observeVideos() {
        viewModel.videos.observe(this) { videos ->
            binding.recyclerView.adapter = LikedVideosAdapter(videos)
        }
    }
}