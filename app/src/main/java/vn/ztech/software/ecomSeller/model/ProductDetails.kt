package vn.ztech.software.ecomSeller.model

data class ProductDetails(
    val __v: Int,
    val _id: String,
    val brandName: String,
    val createdAt: String,
    val description: String,
    val imageUrls: List<String>,
    val numOfReviews: Int,
    val origin: String,
    val productId: String,
    val reviews: List<Any>,
    val unit: String,
    val updatedAt: String
)