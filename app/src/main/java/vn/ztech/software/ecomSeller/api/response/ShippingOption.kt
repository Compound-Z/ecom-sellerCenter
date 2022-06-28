package vn.ztech.software.ecomSeller.api.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShippingOption(
    val fee: Fee,
    val name: String,
    val service_id: Int
):Parcelable
@Parcelize
data class Fee(
    val coupon_value: Int,
    val insurance_fee: Int,
    val pick_station_fee: Int,
    val r2s_fee: Int,
    val service_fee: Int,
    val total: Int
):Parcelable
