package vn.ztech.software.ecomSeller.api.request

data class CreateReviewRequest(
    val content: String,
    val productId: String,
    val rating: Int,
    val reviewQueueId: String
)