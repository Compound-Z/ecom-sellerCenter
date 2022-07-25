package vn.ztech.software.ecomSeller.api.request

data class SearchProductInCategoryRequest(
    val searchWordsProduct: String,
    var page: Int = 1

)