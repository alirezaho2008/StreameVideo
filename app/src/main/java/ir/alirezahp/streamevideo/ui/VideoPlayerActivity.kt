package ir.alirezahp.streamevideo.ui

import android.os.Bundle
import android.view.View
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import ir.alirezahp.streamevideo.R
import ir.alirezahp.streamevideo.data.db.AppDatabase
import ir.alirezahp.streamevideo.data.model.Like
import ir.alirezahp.streamevideo.data.model.PlaybackState
import ir.alirezahp.streamevideo.databinding.ActivityVideoPlayerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class VideoPlayerActivity : BaseActivity() {
    private lateinit var binding: ActivityVideoPlayerBinding
    private lateinit var mainPlayer: ExoPlayer
    private lateinit var adPlayer: ExoPlayer
    private lateinit var db: AppDatabase
    private var userId: Int = 1
    private var videoUrl: String? = null
    private var isPlaying = false
    private var videoUrlList: List<String>? = null
    private var videoIdList: List<Int>? = null
    private var currentVideoIndex: Int = 0
    private var currentVideoId: Int = 0
    private var currentVideoTitle: String? = null
    private var currentVideoDuration: Float = 0f
    private var isTimeToPlayAd = false
    private var adIndexForPlay: Int = 0
    private var videoPlayed: Int = 0
    private var lastAdPlayedId: Int = 0
    private var adWatched15Seconds = false
    private var hasWatched5Seconds = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = getSharedPreferences("app_prefs", MODE_PRIVATE).getInt("selected_user_id", 1)
        db = AppDatabase.getDb(this)
        mainPlayer = ExoPlayer.Builder(this).build()
        adPlayer = ExoPlayer.Builder(this).build()

        videoUrl = intent.getStringExtra("video_url")
        videoUrlList = intent.getStringArrayListExtra("video_url_list")
        videoIdList = intent.getIntegerArrayListExtra("video_id_list")?.toList()
        currentVideoIndex = intent.getIntExtra("video_index", 0)
        currentVideoTitle = intent.getStringExtra("video_title")
        currentVideoDuration = intent.getFloatExtra("video_duration", 0f)
        currentVideoId = intent.getIntExtra("video_id", 0)

        if (videoUrl == null || videoUrlList == null || videoIdList == null) {
            finish()
            return
        }

        mainPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    addToAddWatched()
                    if (currentVideoIndex < videoUrlList!!.size - 1) {
                        currentVideoIndex++
                    } else {
                        currentVideoIndex = 0
                    }
                    hasWatched5Seconds = false
                    decideToPlayMainVideoOrAd()
                }
            }
        })
        adPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    addToAddWatched()
                    stopAd()
                }
            }
        })


        CoroutineScope(Dispatchers.IO).launch {
            var state = db.playbackStateDao().getState(userId)
            if (state == null) {
                state = PlaybackState(userId = userId)
                db.playbackStateDao().insert(state)
            } else {
                videoPlayed = state.videoPlayed
                adIndexForPlay = state.adIndexForPlay
            }
        }

        updateLikesStatus()

        binding.btnLike.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val likes = db.likeDao().getLikes(userId)
                val videoId = videoIdList!![currentVideoIndex]
                val isLiked = likes.any { it.videoId == videoId }

                if (isLiked) {
                    db.likeDao().removeLike(videoId, userId)
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.btnLike.setImageResource(R.drawable.not_liked)
                    }
                } else {
                    val like = Like(
                        videoId = videoId,
                        title = currentVideoTitle,
                        url = videoUrlList!![currentVideoIndex],
                        duration = currentVideoDuration,
                        userId = userId
                    )
                    db.likeDao().addLike(like)
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.btnLike.setImageResource(R.drawable.liked)
                    }
                }
            }
        }


        binding.btnBack.setOnClickListener { finish() }

        if (userId == 1) {
            binding.txtVideoPlayerUser.text = "کاربر اول"
        } else {
            binding.txtVideoPlayerUser.text = "کاربر دوم"
        }

        binding.btnNext.setOnClickListener {
            if (currentVideoIndex < videoUrlList!!.size - 1) {
                currentVideoIndex++
            } else {
                currentVideoIndex = 0
            }
            hasWatched5Seconds = false
            decideToPlayMainVideoOrAd()
            updateLikesStatus()
        }

        binding.btnPrevious.setOnClickListener {
            if (currentVideoIndex > 0) {
                currentVideoIndex--
            } else {
                currentVideoIndex = videoUrlList!!.size - 1
            }
            hasWatched5Seconds = false
            decideToPlayMainVideoOrAd()
            updateLikesStatus()
        }

        binding.btnPlayPause.setOnClickListener {
            isPlaying = !isPlaying
            if (isPlaying) {
                mainPlayer.pause()
                binding.btnPlayPause.setImageResource(R.drawable.play)
            } else {
                mainPlayer.play()
                updateSeekBar()
                binding.btnPlayPause.setImageResource(R.drawable.pause)
            }
        }

        decideToPlayMainVideoOrAd()

    }

    private fun updateLikesStatus() {

        CoroutineScope(Dispatchers.IO).launch {
            val likes = db.likeDao().getLikes(userId)
            val isLiked = likes.any { it.videoId == videoIdList!![currentVideoIndex] }
            CoroutineScope(Dispatchers.Main).launch {
                binding.btnLike.setImageResource(if (isLiked) R.drawable.liked else R.drawable.not_liked)
            }
        }
    }

    private fun decideToPlayMainVideoOrAd() {
        CoroutineScope(Dispatchers.IO).launch {
            val state = db.playbackStateDao().getState(userId)
            if (state != null) {
                videoPlayed = state.videoPlayed
                adIndexForPlay = state.adIndexForPlay

                isTimeToPlayAd = videoPlayed >= 5 && videoPlayed % 5 == 0 //Time to play ad or video

                if (isTimeToPlayAd) {
                    val adVideos = db.adVideoDao().getAdVideos()
                    if (adVideos.isNotEmpty()) {
                        val adId = when (adIndexForPlay) {//choose ad to play
                            0, 1, 2 -> 1
                            3 -> 2
                            4 -> 3
                            else -> ((adIndexForPlay - 5) % 3) + 1
                        }
                        val adVideo = adVideos.find { it.id == adId }
                        if (adVideo != null) {
                            videoUrl = adVideo.url
                            lastAdPlayedId = adId
                            CoroutineScope(Dispatchers.Main).launch {
                                binding.clAd.visibility = View.VISIBLE
                                binding.clMain.visibility = View.GONE
                                playAdVideo(videoUrl!!)
                                setupSkipButton()
                            }
                        } else {
                            isTimeToPlayAd = false
                            CoroutineScope(Dispatchers.Main).launch {
                                binding.clMain.visibility = View.VISIBLE
                                binding.clAd.visibility = View.GONE
                                playMainVideo(videoUrlList!![currentVideoIndex])
                            }
                        }
                    } else {
                        isTimeToPlayAd = false
                        CoroutineScope(Dispatchers.Main).launch {
                            binding.clMain.visibility = View.VISIBLE
                            binding.clAd.visibility = View.GONE
                            playMainVideo(videoUrlList!![currentVideoIndex])
                        }
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.clMain.visibility = View.VISIBLE
                        binding.clAd.visibility = View.GONE
                        playMainVideo(videoUrlList!![currentVideoIndex])
                    }
                }
            }
        }
    }

    private fun playMainVideo(url: String) {
        binding.playerView.player = mainPlayer
        val mediaItem = MediaItem.fromUri(url)
        mainPlayer.setMediaItem(mediaItem)
        mainPlayer.prepare()
        mainPlayer.play()
        isPlaying = true
        binding.btnPlayPause.setImageResource(R.drawable.pause)
        hasWatched5Seconds = false
        updateSeekBar()
    }

    private fun playAdVideo(url: String) {
        binding.adPlayerView.player = adPlayer
        val mediaItem = MediaItem.fromUri(url)
        adPlayer.setMediaItem(mediaItem)
        adPlayer.prepare()
        adPlayer.play()
        isPlaying = true
        updateAdSeekBar()
    }

    private fun updateSeekBar() {
        CoroutineScope(Dispatchers.Main).launch {
            while (!isTimeToPlayAd && isPlaying) {
                val currentPos = mainPlayer.currentPosition / 1000
                binding.seekBar.progress = currentPos.toInt()
                val min = currentPos / 60
                val sec = currentPos % 60
                binding.tvCurrentTime.text =
                    String.format(Locale.getDefault(), "%02d:%02d", min, sec)

                val totalDuration = mainPlayer.duration / 1000
                binding.seekBar.max = totalDuration.toInt()
                val totalMin = totalDuration / 60
                val totalSec = totalDuration % 60
                binding.tvTotalTime.text =
                    String.format(Locale.getDefault(), "%02d:%02d", totalMin, totalSec)

                if (currentPos >= 5 && !hasWatched5Seconds) {
                    hasWatched5Seconds = true
                    addToAddWatched()
                }

                delay(1000)
            }
        }
    }

    private fun updateAdSeekBar() {
        CoroutineScope(Dispatchers.Main).launch {
            while (isTimeToPlayAd && isPlaying) {
                val currentPos = adPlayer.currentPosition / 1000
                binding.adSeekBar.progress = currentPos.toInt()
                val min = currentPos / 60
                val sec = currentPos % 60
                binding.tvAdCurrentTime.text =
                    String.format(Locale.getDefault(), "%02d:%02d", min, sec)

                val totalDuration = adPlayer.duration / 1000
                binding.adSeekBar.max = totalDuration.toInt()
                val totalMin = totalDuration / 60
                val totalSec = totalDuration % 60
                binding.tvAdTotalTime.text =
                    String.format(Locale.getDefault(), "%02d:%02d", totalMin, totalSec)



                if (currentPos >= 15 && !adWatched15Seconds) {
                    adWatched15Seconds = true
                    CoroutineScope(Dispatchers.IO).launch {
                        val state = db.playbackStateDao().getState(userId)
                        if (state != null) {
                            db.playbackStateDao().update(
                                state.copy(
                                    adIndexForPlay = state.adIndexForPlay + 1,
                                )
                            )
                        }
                    }
                }

                delay(1000)
            }
        }
    }

    private fun addToAddWatched() {
        CoroutineScope(Dispatchers.IO).launch {
            val state = db.playbackStateDao().getState(userId)
            if (state != null) {
                db.playbackStateDao().update(
                    state.copy(videoPlayed = state.videoPlayed + 1)
                )
            }
        }
    }

    private fun setupSkipButton() {
        binding.btnSkipAd.visibility = View.VISIBLE
        binding.btnSkipAd.isEnabled = false
        var countdown = 5
        CoroutineScope(Dispatchers.Main).launch {
            while (countdown > 0) {
                binding.btnSkipAd.text = "رد آگهی: $countdown ثانیه"
                delay(1000)
                countdown--
            }
            binding.btnSkipAd.text = "رد آگهی"
            binding.btnSkipAd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.skip_icon, 0)
            binding.btnSkipAd.isEnabled = true
            binding.btnSkipAd.setOnClickListener {
                addToAddWatched()
                stopAd()
            }
        }
    }

    private fun stopAd() {
        adPlayer.stop()
        binding.clAd.visibility = View.GONE
        binding.btnSkipAd.visibility = View.GONE
        adWatched15Seconds = false
        hasWatched5Seconds = false
        decideToPlayMainVideoOrAd()
    }

    override fun onPause() {
        super.onPause()
        if (isTimeToPlayAd) {
            adPlayer.pause()
        } else {
            mainPlayer.pause()
        }
        isPlaying = false
    }

    override fun onDestroy() {
        super.onDestroy()
        mainPlayer.release()
        adPlayer.release()
    }
}