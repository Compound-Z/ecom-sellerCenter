package vn.ztech.software.ecomSeller.api.response

data class UpdateCategoryResponse(
    val acknowledged: Boolean,
    val message: String,
    val modifiedCount: Int
)