package vn.ztech.software.ecomSeller.ui.address

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import vn.ztech.software.ecomSeller.api.request.AddAddressRequest
import vn.ztech.software.ecomSeller.common.LoadState
import vn.ztech.software.ecomSeller.common.extension.toLoadState
import vn.ztech.software.ecomSeller.model.*
import vn.ztech.software.ecomSeller.ui.AddAddressViewErrors
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.errorMessage
import vn.ztech.software.ecomSeller.util.isPhoneNumberValid

class AddressViewModel(private val addressUseCase: IAddressUseCase): ViewModel() {
    val loading = MutableLiveData<Boolean>()
    val addresses = MutableLiveData<Address?>()
    val addAddressStatus = MutableLiveData<Boolean>()
    val updateAddressStatus = MutableLiveData<Boolean>()
    val error = MutableLiveData<CustomError>()
    val uiError = MutableLiveData<List<AddAddressViewErrors>>()
    val isEdit = MutableLiveData<Boolean>()
    val currentSelectedAddressItem = MutableLiveData<AddressItem>()
    val provinces = MutableLiveData<List<Province>>()
    val districts = MutableLiveData<List<District>>()
    val wards = MutableLiveData<List<Ward>>()
    val fromWhere = MutableLiveData<String>()

    fun getAddresses(isLoadingEnabled: Boolean = true){
        viewModelScope.launch {
            addressUseCase.getAddresses().flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        if(isLoadingEnabled) loading.value = true
                    }
                    is LoadState.Loaded -> {
                        loading.value = false
                        addresses.value = it.data
                        Log.d("getAddresses", addresses.value.toString())
                    }
                    is LoadState.Error -> {
                        loading.value = false
                        error.value = errorMessage(it.e)
                        Log.d("getAddresses: error", it.e.message.toString())

                    }
                }
            }
        }
    }
    fun getProvinces(isLoadingEnabled: Boolean = true){
        viewModelScope.launch {
            addressUseCase.getProvinces().flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        if(isLoadingEnabled) loading.value = true
                    }
                    is LoadState.Loaded -> {
                        loading.value = false
                        provinces.value = it.data
                        Log.d("getProvinces", addresses.value.toString())
                    }
                    is LoadState.Error -> {
                        loading.value = false
                        error.value = errorMessage(it.e)
                        Log.d("getProvinces: error", it.e.message.toString())

                    }
                }
            }
        }
    }
    fun getDistricts(provinceId: Int, isLoadingEnabled: Boolean = true){
        viewModelScope.launch {
            addressUseCase.getDistricts(provinceId).flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        if(isLoadingEnabled) loading.value = true
                    }
                    is LoadState.Loaded -> {
                        loading.value = false
                        districts.value = it.data
                        Log.d("getProvinces", addresses.value.toString())
                    }
                    is LoadState.Error -> {
                        loading.value = false
                        error.value = errorMessage(it.e)
                        Log.d("getProvinces: error", it.e.message.toString())

                    }
                }
            }
        }
    }
    fun getWards(districtId: Int, isLoadingEnabled: Boolean = true){
        viewModelScope.launch {
            addressUseCase.getWards(districtId).flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        if(isLoadingEnabled) loading.value = true
                    }
                    is LoadState.Loaded -> {
                        loading.value = false
                        wards.value = it.data
                        Log.d("getProvinces", addresses.value.toString())
                    }
                    is LoadState.Error -> {
                        loading.value = false
                        error.value = errorMessage(it.e)
                        Log.d("getProvinces: error", it.e.message.toString())

                    }
                }
            }
        }
    }

    fun submitAddress(
        receiverName: String,
        receiverPhoneNumber: String,
        provinceName: String?,
        provincePos: Int,
        districtName: String?,
        districtPos: Int,
        wardName: String?,
        wardPos: Int,
        detailedAddress: String,
        isSelectedAsDefaultAddress: Boolean
    ) {
        val errorsList = mutableListOf<AddAddressViewErrors>()
        if (receiverName.isBlank() || receiverPhoneNumber.isBlank() || provinceName==null || provinceName.isBlank() || districtName == null || districtName.isBlank() || wardName==null || wardName.isBlank() || detailedAddress.isBlank())
            errorsList.add(AddAddressViewErrors.EMPTY)
        if (receiverName.isBlank()) errorsList.add(AddAddressViewErrors.ERR_NAME_EMPTY)
        if (receiverPhoneNumber.isBlank() || !isPhoneNumberValid(receiverPhoneNumber)) errorsList.add(AddAddressViewErrors.ERR_PHONE_EMPTY)
        if (provinceName==null || provinceName.isBlank()) errorsList.add(AddAddressViewErrors.ERR_PROVINCE_EMPTY)
        if (districtName==null || districtName.isBlank()) errorsList.add(AddAddressViewErrors.ERR_DISTRICT_EMPTY)
        if (wardName==null || wardName.isBlank()) errorsList.add(AddAddressViewErrors.ERR_WARD_EMPTY)
        if (detailedAddress.isBlank()) errorsList.add(AddAddressViewErrors.ERR_DETAILED_ADDRESS_EMPTY)

        uiError.value = errorsList

        if (errorsList.isEmpty()) {
            Log.d("ERROR", "NO error checking address" )
//            val addressId = if (_isEdit.value == true) _addressId.value!! else
//                getAddressId(currentUser!!)
            val addressRequest = AddAddressRequest(
                receiverName = receiverName,
                receiverPhoneNumber = receiverPhoneNumber,
                provinceId = provinces.value?.get(provincePos)?.province_id?:-1,
                districtId = districts.value?.get(districtPos)?.district_id?:-1,
                wardCode = wards.value?.get(wardPos)?.code?:"-1",
                detailedAddress = detailedAddress,
                isDefaultAddress = isSelectedAsDefaultAddress
                )
            if (isEdit.value == true) {
                updateAddress(currentSelectedAddressItem.value?._id?:"x", addressRequest)
            } else {
                addAddress(addressRequest)
            }
        }
    }
    private fun addAddress(addressRequest: AddAddressRequest, isLoadingEnabled: Boolean = true){
        viewModelScope.launch {
            addressUseCase.addAddress(addressRequest).flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        if(isLoadingEnabled) loading.value = true
                    }
                    is LoadState.Loaded -> {
                        loading.value = false
                        addAddressStatus.value = true
                        addresses.value = it.data
                    }
                    is LoadState.Error -> {
                        loading.value = false
                        addAddressStatus.value = false
                        error.value = errorMessage(it.e)
                    }
                }
            }
        }
    }
    private fun updateAddress(addressItemId: String, addressRequest: AddAddressRequest, isLoadingEnabled: Boolean = true){
        viewModelScope.launch {
            addressUseCase.updateAddress(addressItemId, addressRequest).flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        if(isLoadingEnabled) loading.value = true
                    }
                    is LoadState.Loaded -> {
                        loading.value = false
                        updateAddressStatus.value = true
                        addresses.value = it.data
                    }
                    is LoadState.Error -> {
                        loading.value = false
                        updateAddressStatus.value = false
                        error.value = errorMessage(it.e)
                    }
                }
            }
        }
    }
    fun deleteAddress(addressItemId: String, isLoadingEnabled: Boolean = true){
        viewModelScope.launch {
            addressUseCase.deleteAddress(addressItemId).flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        if(isLoadingEnabled) loading.value = true
                    }
                    is LoadState.Loaded -> {
                        loading.value = false
                        addresses.value = it.data
                    }
                    is LoadState.Error -> {
                        loading.value = false
                        error.value = errorMessage(it.e)
                    }
                }
            }
        }
    }
    fun clearErrors() {
        error.value = null
    }
}