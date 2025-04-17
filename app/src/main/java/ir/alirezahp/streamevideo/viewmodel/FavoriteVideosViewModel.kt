package ir.alirezahp.streamevideo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.alirezahp.streamevideo.data.model.Video
import ir.alirezahp.streamevideo.data.repository.LikeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteVideosViewModel(private val repository: LikeRepository) : ViewModel() {

    private val _videos = MutableLiveData<List<Video>>()
    val videos: LiveData<List<Video>> get() = _videos

    fun loadLikes(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val likes = repository.getLikes(userId)
            _videos.postValue(likes)
        }
    }
}