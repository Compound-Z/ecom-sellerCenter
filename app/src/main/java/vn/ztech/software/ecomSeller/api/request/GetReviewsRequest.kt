package vn.ztech.software.ecomSeller.api.request

class GetReviewsRequest (
    val starFilter: Int? = null,
    var page: Int = 1,
    var pageSize: Int? = null,
)