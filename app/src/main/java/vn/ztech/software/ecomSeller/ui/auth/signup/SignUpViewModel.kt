package vn.ztech.software.ecomSeller.ui.auth.signup

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import vn.ztech.software.ecomSeller.common.LoadState
import vn.ztech.software.ecomSeller.common.extension.toLoadState
import vn.ztech.software.ecomSeller.model.UserData
import vn.ztech.software.ecomSeller.ui.AddAddressViewErrors
import vn.ztech.software.ecomSeller.ui.AddProductViewErrors
import vn.ztech.software.ecomSeller.ui.SignUpViewErrors
import vn.ztech.software.ecomSeller.ui.UserType
import vn.ztech.software.ecomSeller.ui.address.AddressViewModel
import vn.ztech.software.ecomSeller.ui.product.ProductViewModel
import vn.ztech.software.ecomSeller.util.*
import vn.ztech.software.ecomSeller.util.isEmailValid
import vn.ztech.software.ecomSeller.util.isPhoneValid

class SignUpViewModel(val productViewModel: ProductViewModel, val addressViewModel: AddressViewModel, private val useCase: ISignUpUseCase): ViewModel() {
    init {
        Log.d("ERROR:","SignUpViewModel created")

    }
    private val _errorStatus = MutableLiveData<SignUpViewErrors?>()
    val errorStatus: LiveData<SignUpViewErrors?> get() = _errorStatus

    val signUpError = MutableLiveData<CustomError?>()

    val isSignUpSuccessfully = MutableLiveData<Boolean?>()

    private val _userData = MutableLiveData<UserData>()
    val userData: LiveData<UserData> get() = _userData

    val loading = MutableLiveData<Boolean>()

    fun signUpSubmitData(
        name: String,
        mobile: String,
        email: String,
        pwd1: String,
        pwd2: String,
        isAccepted: Boolean,
        shopName: String,
        shopDescription: String,
        imgList: List<Uri>,
        provinceName: String?,
        provincePos: Int,
        districtName: String?,
        districtPos: Int,
        wardName: String?,
        wardPos: Int,
        detailedAddress: String,
    ) {
        if (name.trim().isBlank() || mobile.trim().isBlank() || email.trim().isBlank() || pwd1.trim().isBlank() || pwd2.trim().isBlank()
            || shopName.trim().isBlank()|| shopDescription.trim().isBlank() || imgList.isEmpty()
        ){
            _errorStatus.value = SignUpViewErrors.ERR_EMPTY
            return
        }

        if (pwd1 != pwd2) {
            _errorStatus.value = SignUpViewErrors.ERR_PWD12NS
            return
        }
        if(!isPasswordValid(pwd1) || !isPasswordValid(pwd2)){
            _errorStatus.value = SignUpViewErrors.ERR_PW_INVALID
            return
        }
        if (!isAccepted) {
            _errorStatus.value = SignUpViewErrors.ERR_NOT_ACC
            return
        }

        var err = ERR_INIT
        if (!isEmailValid(email)) {
            err += ERR_EMAIL
        }
        if (!isPhoneNumberValid(mobile)) {
            err += ERR_MOBILE
        }
        when (err) {

            (ERR_INIT + ERR_EMAIL) -> {
                _errorStatus.value = SignUpViewErrors.ERR_EMAIL
                return
            }
            (ERR_INIT + ERR_MOBILE) -> {
                _errorStatus.value = SignUpViewErrors.ERR_MOBILE
                return
            }
            (ERR_INIT + ERR_EMAIL + ERR_MOBILE) -> {
                _errorStatus.value = SignUpViewErrors.ERR_EMAIL_MOBILE
                return
            }
        }
        if (name.length < 2 || name.length > 50){
            _errorStatus.value = SignUpViewErrors.NAME
            return
        }
        if (shopName.length < 2 || name.length > 40){
            _errorStatus.value = SignUpViewErrors.SHOP_NAME
            return
        }
        if (shopDescription.length < 25 || name.length > 500){
            _errorStatus.value = SignUpViewErrors.SHOP_DESCRIPTION
            return
        }
        /**address errors check*/
        if (provinceName==null || provinceName.isBlank()) {
            _errorStatus.value = SignUpViewErrors.ERR_PROVINCE_EMPTY
            return
        }
        if (districtName==null || districtName.isBlank()) {
            _errorStatus.value = SignUpViewErrors.ERR_DISTRICT_EMPTY
            return
        }
        if (wardName==null || wardName.isBlank()) {
            _errorStatus.value = SignUpViewErrors.ERR_WARD_EMPTY
            return
        }
        if (detailedAddress.isBlank() || detailedAddress.length < 5 || detailedAddress.length > 150 ) {
            _errorStatus.value = SignUpViewErrors.ERR_DETAILED_ADDRESS_EMPTY
            return
        }
        //final
        _errorStatus.value = SignUpViewErrors.NONE
        val uId = getRandomString(32, mobile.trim(), 6)
        val newData =
            UserData(
                uId,
                name.trim(),
                mobile.trim(),
                email.trim(),
                pwd1.trim(),
                UserType.seller.name,
                ArrayList(),
                ArrayList(),
                shopName,
                shopDescription,
                "",
                addressViewModel.provinces.value?.get(provincePos)?.province_id?:-1,
                addressViewModel.districts.value?.get(districtPos)?.district_id?:-1,
                addressViewModel.wards.value?.get(wardPos)?.code?:"-1",
                detailedAddress
            )
        _userData.value = newData
        return


        /**old*/
//        if (name.isBlank() || mobile.isBlank() || email.isBlank() || pwd1.isBlank() || pwd2.isBlank()) {
//            _errorStatus.value = SignUpViewErrors.ERR_EMPTY
//        } else {
//            if (pwd1 != pwd2) {
//                _errorStatus.value = SignUpViewErrors.ERR_PWD12NS
//            } else {
//                if(!isPasswordValid(pwd1) || !isPasswordValid(pwd2)){
//                    _errorStatus.value = SignUpViewErrors.ERR_PW_INVALID
//                }else{
//                    if (!isAccepted) {
//                        _errorStatus.value = SignUpViewErrors.ERR_NOT_ACC
//                    } else {
//                        var err = ERR_INIT
//                        if (!isEmailValid(email)) {
//                            err += ERR_EMAIL
//                        }
//                        if (!isPhoneValid(mobile)) {
//                            err += ERR_MOBILE
//                        }
//                        when (err) {
//                            ERR_INIT -> {
//                                _errorStatus.value = SignUpViewErrors.NONE
//                                val uId = getRandomString(32, "84" + mobile.trim(), 6)
//                                val newData =
//                                    UserData(
//                                        uId,
//                                        name.trim(),
//                                        "+84" + mobile.trim(),
//                                        email.trim(),
//                                        pwd1.trim(),
//                                        UserType.CUSTOMER.name,
//                                        ArrayList(),
//                                        ArrayList(),
////                                        ArrayList(),
//                                        /**if (isSeller) UserType.SELLER.name else */
//                                        /**if (isSeller) UserType.SELLER.name else */
//                                    )
//                                _userData.value = newData
//                            }
//                            (ERR_INIT + ERR_EMAIL) -> _errorStatus.value = SignUpViewErrors.ERR_EMAIL
//                            (ERR_INIT + ERR_MOBILE) -> _errorStatus.value = SignUpViewErrors.ERR_MOBILE
//                            (ERR_INIT + ERR_EMAIL + ERR_MOBILE) -> _errorStatus.value = SignUpViewErrors.ERR_EMAIL_MOBILE
//                        }
//                    }
//                }
//            }
//        }

    }

    fun sendSignUpRequest(user: UserData?) {
        if (user == null) {
            signUpError.value = errorMessage(CustomError(customMessage = "System error"))
            return
        }
        viewModelScope.launch {
            useCase.sendSignUpRequest(user).flowOn(Dispatchers.IO).toLoadState().collect{
                when (it) {
                    is LoadState.Loading -> {
                        loading.value = true
                    }
                    is LoadState.Loaded -> {
                        loading.value = false
                        Log.d("ERROR:", "LoadState.Loaded $it, id: ${this@launch}")
                        isSignUpSuccessfully.value = true

                    }
                    is LoadState.Error -> {
//                        if (it.e is TokenRefreshing) {
//                            return@collect
//                        }xxx
                        Log.d("ERROR:", "LoadState.Error ${it.e.message}")
                        loading.value = false
                        signUpError.value = errorMessage(it.e)

                    }
                }
            }
        }
    }
    fun clearError(){
        _errorStatus.value = null
        isSignUpSuccessfully.value = null
        signUpError.value = null
    }
}