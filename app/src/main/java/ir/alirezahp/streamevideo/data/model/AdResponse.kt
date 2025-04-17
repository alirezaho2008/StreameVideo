package ir.alirezahp.streamevideo.data.model

data class AdResponse(
    val done: Boolean,
    val result: AdResult
)

data class AdResult(
    val advertises: List<AdVideo>
)