package vn.ztech.software.ecomSeller.api.request


class GetOrdersRequest (
    val statusFilter: String,
    var page: Int = 1
)