package vn.ztech.software.ecomSeller.api.request

class SearchOrderByIdRequest (
    val orderId: String,
    val statusFilter: String
)