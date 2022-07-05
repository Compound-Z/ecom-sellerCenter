package vn.ztech.software.ecomSeller.api.response

import vn.ztech.software.ecomSeller.common.Constants

class GetOrdersRequest (
    val statusFilter: String,
    var page: Int = 1
)