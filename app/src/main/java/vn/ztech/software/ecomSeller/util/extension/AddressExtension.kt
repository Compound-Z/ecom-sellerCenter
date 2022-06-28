package vn.ztech.software.ecomSeller.util.extension

import vn.ztech.software.ecomSeller.model.*

fun AddressItem.getFullAddress(): String{
    return "${this.detailedAddress}, ${this.ward.name}, ${this.district.name}, ${this.province.name}"
}
fun List<Province>.findPos(provinceId: Int): Int{
    this.forEachIndexed { index, province ->
        if (province.province_id == provinceId) return index
    }
    return 0
}
@JvmName("findPosDistrict")
fun List<District>.findPos(districtId: Int): Int{
    this.forEachIndexed { index, district ->
        if (district.district_id == districtId) return index
    }
    return 0
}

fun List<Ward>.findPos(wardCode: String): Int{
    this.forEachIndexed { index, ward ->
        if (ward.code == wardCode) return index
    }
    return 0
}
fun Address.checkIsDefaultAddress(addressItem: AddressItem?): Boolean {
    if (addressItem==null) return false
    return this.defaultAddressId == addressItem._id
}