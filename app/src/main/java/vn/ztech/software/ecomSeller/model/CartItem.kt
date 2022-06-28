package vn.ztech.software.ecomSeller.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartItem(
    var itemId: String = "",
    var productId: String = "",
    var ownerId: String = "",
    var quantity: Int = 0,
) : Parcelable {
    constructor() : this("", "", "", 0)

    fun toHashMap(): HashMap<String, Any> {
        val hashMap = hashMapOf<String, Any>()
        hashMap["itemId"] = itemId
        hashMap["productId"] = productId
        hashMap["ownerId"] = ownerId
        hashMap["quantity"] = quantity
        return hashMap
    }
}