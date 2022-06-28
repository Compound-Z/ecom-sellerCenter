package vn.ztech.software.ecomSeller.api.request

data class CreateCategoryRequest(
    val imageUrl: String,
    val name: String,
    val numberOfProduct: Int
)