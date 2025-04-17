package ir.alirezahp.streamevideo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ir.alirezahp.streamevideo.data.repository.LikeRepository

class FavoriteVideosViewModelFactory(private val repository: LikeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteVideosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoriteVideosViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}