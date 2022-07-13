package vn.ztech.software.ecomSeller.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
@Keep
@Parcelize
data class Product(
    val _id: String,
    val name: String,
    val sku: String,
    val isSaling: Boolean,
    val price: Int,
    val imageUrl: String,
    val category: String,
    val saleNumber: Int,
    val weight: Int,
    val quantity: Int,
    val stockNumber : Int,
    val averageRating: Float,
    val numberOfRating:  Int,
) : Parcelable