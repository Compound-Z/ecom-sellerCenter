package vn.ztech.software.ecomSeller.api.request

class GetMyReviewQueueRequest (
    var filter: String = "NOT_REVIEWED",
    var page: Int = 1
)