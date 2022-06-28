package vn.ztech.software.ecomSeller.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
@Parcelize
data class Address(
    val __v: Int,
    val _id: String,
    val addresses: List<AddressItem>,
    val defaultAddressId: String,
    val userId: String
):Parcelable
@Parcelize
data class AddressItem(
    val _id: String,
    val addressType: String,
    val detailedAddress: String,
    val district: District,
    val province: Province,
    val receiverName: String,
    val receiverPhoneNumber: String,
    val ward: Ward
):Parcelable
@Parcelize
data class District(
    val _id: String,
    val code: String,
    val districtId: Int,
    val district_id: Int,
    val name: String,
    val provinceId: Int,
    val province_id: Int

):Parcelable
@Parcelize
data class Province(
    val _id: String,
    val code: String,
    val name: String,
    val provinceId: Int,
    val province_id: Int

):Parcelable
@Parcelize
data class Ward(
    val _id: String,
    val code: String,
    val districtId: Int,
    val district_id: Int,
    val name: String
):Parcelable

//@Parcelize
//data class Address(
//    var addressId: String = "",
//    var fName: String = "",
//    var lName: String = "",
//    var countryISOCode: String = "",
//    var streetAddress: String = "",
//    var streetAddress2: String = "",
//    var city: String = "",
//    var state: String = "",
//    var zipCode: String = "",
//    var phoneNumber: String = ""
//) : Parcelable {
//    fun toHashMap(): HashMap<String, String> {
//        return hashMapOf(
//            "addressId" to addressId,
//            "fName" to fName,
//            "lName" to lName,
//            "countryISOCode" to countryISOCode,
//            "streetAddress" to streetAddress,
//            "streetAddress2" to streetAddress2,
//            "city" to city,
//            "state" to state,
//            "zipCode" to zipCode,
//            "phoneNumber" to phoneNumber
//        )
//    }
//}