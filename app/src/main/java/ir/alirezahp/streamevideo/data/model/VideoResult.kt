package ir.alirezahp.streamevideo.data.model

data class VideoResult(
    val videos: List<Video>,
    val remaining: Int,
    val end: Boolean
)