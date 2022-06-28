package vn.ztech.software.ecomSeller.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val imageUrl: String,
    val name: String,
    val numberOfProduct: Int,
    val updatedAt: String
): Parcelable