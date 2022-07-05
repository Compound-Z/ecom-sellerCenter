package vn.ztech.software.ecomSeller.api.response

import vn.ztech.software.ecomSeller.model.Order

data class PagedGetAllOrdersResponse(
    val docs: List<Order>,
    val hasNextPage: Boolean,
    val hasPrevPage: Boolean,
    val limit: Int,
    val nextPage: Int,
    val page: Int,
    val pagingCounter: Int,
    val prevPage: Any,
    val totalDocs: Int,
    val totalPages: Int
)