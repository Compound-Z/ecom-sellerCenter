package vn.ztech.software.ecomSeller.api.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CreateProductRequest(
    val product: Product,
    val productDetail: ProductDetail
):Parcelable

@Parcelize
data class Product(
    val category: String,
    var imageUrl: String,
    val name: String,
    val price: Int,
    val sku: String,
    val stockNumber: Int,
    val weight: Int
):Parcelable

@Parcelize
data class ProductDetail(
    val brandName: String,
    val description: String,
    var imageUrls: List<String>,
    val origin: String,
    val unit: String
):Parcelable