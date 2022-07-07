package vn.ztech.software.ecomSeller.api.request

class SearchOrderByNameRequest (
    val userName: String,
    val statusFilter: String,
    var page: Int = 1
)