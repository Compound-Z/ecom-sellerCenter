package vn.ztech.software.ecomSeller.api.request

data class AddAddressRequest(
    val detailedAddress: String,
    val districtId: Int,
    val isDefaultAddress: Boolean,
    val provinceId: Int,
    val receiverName: String,
    val receiverPhoneNumber: String,
    val wardCode: String
)