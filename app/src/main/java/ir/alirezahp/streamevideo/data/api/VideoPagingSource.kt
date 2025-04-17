package ir.alirezahp.streamevideo.data.api

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ir.alirezahp.streamevideo.data.model.Video
import ir.alirezahp.streamevideo.data.repository.VideoRepository
import javax.inject.Inject

class VideoPagingSource @Inject constructor(
    private val repository: VideoRepository
) : PagingSource<Int, Video>() {
    companion object {
        const val PAGE_SIZE = 5
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Video> {
        val offset = params.key ?: 0
        return try {
            println("Loading videos with offset: $offset")
            val (videos, isEnd) = repository.getVideos(offset)
            println("Loaded ${videos.size} videos: $videos, isEnd: $isEnd")

            LoadResult.Page(
                data = videos,
                prevKey = if (offset == 0) null else offset - PAGE_SIZE,
                nextKey = if (isEnd || videos.isEmpty()) null else offset + PAGE_SIZE
            )
        } catch (e: Exception) {
            println("Error loading videos: $e")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Video>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(PAGE_SIZE) ?: anchorPage?.nextKey?.minus(PAGE_SIZE)
        }
    }
}