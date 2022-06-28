package vn.ztech.software.ecomSeller.ui.address

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.ztech.software.ecomSeller.api.request.AddAddressRequest
import vn.ztech.software.ecomSeller.model.Address
import vn.ztech.software.ecomSeller.model.District
import vn.ztech.software.ecomSeller.model.Province
import vn.ztech.software.ecomSeller.model.Ward
import vn.ztech.software.ecomSeller.repository.IAddressRepository

interface IAddressUseCase{
    suspend fun getAddresses(): Flow<Address?>
    suspend fun getProvinces(): Flow<List<Province>>
    suspend fun getDistricts(provinceId: Int): Flow<List<District>>
    suspend fun getWards(districtId: Int): Flow<List<Ward>>
    suspend fun addAddress(addAddressRequest: AddAddressRequest): Flow<Address>
    suspend fun updateAddress(addressItemId: String, addAddressRequest: AddAddressRequest): Flow<Address>
    suspend fun deleteAddress(addressItemId: String): Flow<Address>
}

class AddressUseCase(private val addressRepository: IAddressRepository): IAddressUseCase{
    override suspend fun getAddresses(): Flow<Address?> = flow{
        val addresses = addressRepository.getAddresses()
        emit(addresses)
    }
    override suspend fun getProvinces(): Flow<List<Province>> = flow{
        val provinces = addressRepository.getProvinces()
        emit(provinces)
    }

    override suspend fun getDistricts(provinceId: Int): Flow<List<District>> = flow{
        val districts = addressRepository.getDistricts(provinceId)
        emit(districts)
    }
    override suspend fun getWards(districtId: Int): Flow<List<Ward>> = flow{
        val districts = addressRepository.getWards(districtId)
        emit(districts)
    }

    override suspend fun addAddress(addAddressRequest: AddAddressRequest): Flow<Address> = flow {
        emit(addressRepository.addAddress(addAddressRequest))
    }
//
    override suspend fun updateAddress(addressItemId: String, addAddressRequest: AddAddressRequest): Flow<Address> = flow {
        emit(addressRepository.updateAddress(addressItemId, addAddressRequest))
    }

    override suspend fun deleteAddress(addressItemId: String): Flow<Address> = flow {
        emit(addressRepository.deleteAddress(addressItemId))
    }

}