package ir.alirezahp.streamevideo.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.alirezahp.streamevideo.R
import ir.alirezahp.streamevideo.data.api.ApiService
import ir.alirezahp.streamevideo.data.db.AppDatabase
import ir.alirezahp.streamevideo.databinding.ActivityVideoListBinding
import ir.alirezahp.streamevideo.dialog.LoadingDialog
import ir.alirezahp.streamevideo.ui.adapter.VideoAdapter
import ir.alirezahp.streamevideo.viewmodel.VideoListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class VideoListActivity : BaseActivity() {
    private lateinit var binding: ActivityVideoListBinding
    private val viewModel: VideoListViewModel by viewModels()
    private lateinit var videoAdapter: VideoAdapter
    private val prefs by lazy {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
    }
    private val userId by lazy {
        prefs.getInt("selected_user_id", 1)
    }

    @Inject
    lateinit var apiService: ApiService
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupUserSwitchButton()
        setupFavoriteButton()
        observeVideos()
        fetchAndSaveAdVideos()
//        observeAdViewModel()

        setUserButton(userId)

        loadingDialog = LoadingDialog(this)

    }

    private fun fetchAndSaveAdVideos() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val adResponse = apiService.getAdVideos()
                if (adResponse.done) {
                    val adVideos = adResponse.result.advertises
                    if (adVideos.isNotEmpty()) {
                        val db = AppDatabase.getDb(this@VideoListActivity)
                        db.adVideoDao().insertAdVideos(adVideos)
                    }
                }
            } catch (e: Exception) {
                Log.e("VideoListActivity", "خطا در گرفتن ویدیوهای تبلیغاتی")
            }
        }
    }


    private fun setupRecyclerView() {
        val db = AppDatabase.getDb(this)
        videoAdapter = VideoAdapter(db, userId)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@VideoListActivity)
            adapter = videoAdapter
        }
    }

    private fun setUserButton(userId: Int) {
        if (userId == 1) {
            binding.ivVideoListSelectUser.setImageResource(R.drawable.user_1)
            binding.txtChooseUser.text = getString(R.string.User1)
        } else {
            binding.ivVideoListSelectUser.setImageResource(R.drawable.user_2)
            binding.txtChooseUser.text = getString(R.string.User2)
        }
    }

    private fun setupFavoriteButton() {
        binding.btnVideoListFavorite.setOnClickListener {
            val intent = Intent(this, FavoriteVideosActivity::class.java)
            startActivity(intent)
        }
    }
    private fun setupUserSwitchButton() {
        binding.btnVideoListSelectUser.setOnClickListener {
            val bottomSheet = UserSwitchBottomSheet.newInstance { userId ->
                prefs.edit { putInt("selected_user_id", userId) }

                setUserButton(userId)
                videoAdapter = VideoAdapter(AppDatabase.getDb(this), userId)
                binding.recyclerView.adapter = videoAdapter
                observeVideos()
            }
            bottomSheet.show(supportFragmentManager, "UserSwitchBottomSheet")
        }
    }

    private fun observeVideos() {
        lifecycleScope.launch {
            viewModel.videos.collectLatest { pagingData ->
                videoAdapter.submitData(pagingData)
            }
        }
    }
}