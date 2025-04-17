package ir.alirezahp.streamevideo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.alirezahp.streamevideo.data.api.VideoPagingSource
import ir.alirezahp.streamevideo.data.repository.VideoRepository
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val repository: VideoRepository
) : ViewModel() {
    val videos = Pager(
        PagingConfig(pageSize = 5)
    ) {
        VideoPagingSource(repository)
    }.flow.cachedIn(viewModelScope)
}