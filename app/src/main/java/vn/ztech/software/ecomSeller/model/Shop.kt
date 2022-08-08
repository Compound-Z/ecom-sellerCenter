package vn.ztech.software.ecomSeller.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Shop(
    val __v: Int,
    val _id: String,
    val addressItem: AddressItem? = null,
    val categories: List<CategoryShop>,
    val createdAt: String,
    val imageUrl: String,
    val description: String,
    val name: String,
    val numberOfProduct: Int,
    val shippingShopId: String,
    val updatedAt: String,
    val userId: UserId
): Parcelable

@Parcelize
data class CategoryShop(
    val _id: String,
    val categoryRef: String,
    val createdAt: String,
    val numberOfProduct: Int,
    val updatedAt: String
): Parcelable

@Parcelize
data class UserId(
    val _id: String,
    val phoneNumber: String
):Parcelable