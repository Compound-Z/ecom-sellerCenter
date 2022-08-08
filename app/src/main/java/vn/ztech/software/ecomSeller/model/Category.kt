package vn.ztech.software.ecomSeller.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Category(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val imageUrl: String,
    var name: String,
    val numberOfProduct: Int,
    val updatedAt: String
): Parcelable

