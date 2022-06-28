package vn.ztech.software.ecomSeller.repository

import vn.ztech.software.ecomSeller.api.IAddressApi
import vn.ztech.software.ecomSeller.api.request.AddAddressRequest
import vn.ztech.software.ecomSeller.model.Address
import vn.ztech.software.ecomSeller.model.District
import vn.ztech.software.ecomSeller.model.Province
import vn.ztech.software.ecomSeller.model.Ward

interface IAddressRepository{
    suspend fun getAddresses(): Address?
    suspend fun getProvinces(): List<Province>
    suspend fun getDistricts(provinceId: Int): List<District>
    suspend fun getWards(districtId: Int): List<Ward>
    suspend fun addAddress(addAddressRequest: AddAddressRequest): Address
    suspend fun updateAddress(addressItemId: String,addAddressRequest: AddAddressRequest): Address
    suspend fun deleteAddress(addressItemId: String): Address
}

class AddressRepository(private val addAddressApi: IAddressApi): IAddressRepository{
    override suspend fun getAddresses(): Address? {
        return addAddressApi.getAddresses()
    }
    override suspend fun getProvinces(): List<Province> {
        return addAddressApi.getProvinces()
    }
    override suspend fun getDistricts(provinceId: Int): List<District> {
        return addAddressApi.getDistricts(provinceId)
    }
    override suspend fun getWards(districtId: Int): List<Ward> {
        return addAddressApi.getWards(districtId)
    }

    override suspend fun addAddress(addAddressRequest: AddAddressRequest): Address {
        return addAddressApi.addAddress(addAddressRequest)
    }

    override suspend fun updateAddress(
        addressItemId: String,
       addAddressRequest: AddAddressRequest
    ): Address {
        return addAddressApi.updateAddress(addressItemId, addAddressRequest)
    }

    override suspend fun deleteAddress(addressItemId: String): Address {
        return addAddressApi.deleteAddress(addressItemId)
    }

}