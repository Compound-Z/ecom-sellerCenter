package vn.ztech.software.ecomSeller.ui.auth.signup

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.ecomSeller.databinding.FragmentSignupBinding
import vn.ztech.software.ecomSeller.ui.BaseFragment
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.api.response.UploadImageResponse
import vn.ztech.software.ecomSeller.common.StoreDataStatus
import vn.ztech.software.ecomSeller.model.District
import vn.ztech.software.ecomSeller.model.Province
import vn.ztech.software.ecomSeller.model.Ward
import vn.ztech.software.ecomSeller.ui.AddAddressViewErrors
import vn.ztech.software.ecomSeller.ui.AddProductViewErrors
import vn.ztech.software.ecomSeller.ui.SignUpViewErrors
import vn.ztech.software.ecomSeller.ui.category.AddCategoryImagesAdapter
import vn.ztech.software.ecomSeller.util.*

import vn.ztech.software.ecomSeller.util.extension.findPos
import vn.ztech.software.ecomSeller.util.extension.showErrorDialog
import vn.ztech.software.ecomSeller.util.getFullPath
import vn.ztech.software.ecomSeller.util.standardlizePhoneNumber
import java.io.File

class SignupFragment : BaseFragment<FragmentSignupBinding>() {
	private val viewModel: SignUpViewModel by viewModel()
	private var imgList = mutableListOf<Uri>()
	private lateinit var adapter: AddCategoryImagesAdapter
	private var isAddedSelectedAddressToProvinces = false
	private var isAddedSelectedAddressToDistricts = false
	private var isAddedSelectedAddressToWards = false
	override fun setViewBinding(): FragmentSignupBinding {
		return FragmentSignupBinding.inflate(layoutInflater)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if(viewModel.addressViewModel.provinces.value == null) viewModel.addressViewModel.getProvinces()
	}

	override fun observeView() {
		super.observeView()

		viewModel.loading.observe(viewLifecycleOwner){
			if(it){
				handleLoadingDialog(true, R.string.signing_up)
			}else{
				handleLoadingDialog(false, R.string.signing_up)
			}
		}

		viewModel.errorStatus.observe(viewLifecycleOwner) {
			it ?: return@observe
			if(it == SignUpViewErrors.NONE){
				//call api add product
				binding.signupErrorTextView.visibility = View.GONE
				if(  !  imgList[0].toString().startsWith("http")) {
					uploadImage()
				}else {
					viewModel.productViewModel.uploadedImage.value = UploadImageResponse(imgList[0].toString())
				}
			}else{
				modifyErrors(it)
			}
		}

		viewModel.isSignUpSuccessfully.observe(viewLifecycleOwner) {
			Log.d("ERROR:","viewModel.isSignUpSuccessfully: $it")
			it?.let {
				if (it) {
					val bundle = bundleOf("USER_DATA" to viewModel.userData.value)
					launchOtpActivity(getString(R.string.signup_fragment_label), bundle)
				}
			}

		}
		viewModel.signUpError.observe(viewLifecycleOwner) {
			it?.let {
				Log.d("ERROR:","viewModel.signUpError: $it")
				showErrorDialog(it)
			}
		}

		/**Product viewModel*/
		viewModel.productViewModel.storeDataStatus.observe(viewLifecycleOwner) { status ->
			if(status == StoreDataStatus.LOADING) {
				handleLoadingDialog(true, R.string.signing_up)
			}else{
				handleLoadingDialog(false, R.string.signing_up)
			}
		}
		viewModel.productViewModel.uploadedImage.observe(viewLifecycleOwner){
			it?.let {
				viewModel.userData.value?.imageUrl = it.url
				viewModel.sendSignUpRequest(viewModel.userData.value)
			}
		}
		viewModel.productViewModel.error.observe(viewLifecycleOwner){
			it ?: return@observe
			handleError(it)
		}

		/**address viewModel*/
		viewModel.addressViewModel.provinces.observe(viewLifecycleOwner){
			it?.let {
				//populate data to spinner
				var preSelectedPos = -1
				populateProvinceToSpinner(binding.spinnerProvinces, it, object : AdapterView.OnItemSelectedListener {
					override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
						if(binding.spinnerProvinces.getItemAtPosition(p2).toString().isNotEmpty()
							&& p2!=preSelectedPos){
							preSelectedPos = p2
							viewModel.addressViewModel.getDistricts(viewModel.addressViewModel.provinces.value?.get(p2-1)?.province_id?:-1)
							//clear values in district, ward spinner
							binding.spinnerDistricts.adapter = null
							binding.spinnerWards.adapter = null
						}
					}

					override fun onNothingSelected(p0: AdapterView<*>?) {
					}
				})
			}
		}
		viewModel.addressViewModel.districts.observe(viewLifecycleOwner){
			it?.let {
				//populate data to spinner
				var preSelectedPos = -1
				populateDistrictToSpinner(binding.spinnerDistricts, it, object : AdapterView.OnItemSelectedListener {
					override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
						if(binding.spinnerDistricts.getItemAtPosition(p2).toString().isNotEmpty()
							&& p2!=preSelectedPos){
							viewModel.addressViewModel.getWards(viewModel.addressViewModel.districts.value?.get(p2-1)?.district_id?:-1)
							//clear data in spinnerWard
							binding.spinnerWards.adapter = null
						}
					}

					override fun onNothingSelected(p0: AdapterView<*>?) {
					}
				})
			}
		}
		viewModel.addressViewModel.wards.observe(viewLifecycleOwner){
			it?.let {
				//populate data to spinner
				populateWardToSpinner(binding.spinnerWards, it, object : AdapterView.OnItemSelectedListener {
					override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
						if(binding.spinnerWards.getItemAtPosition(p2).toString().isNotEmpty()){}
					}

					override fun onNothingSelected(p0: AdapterView<*>?) {
					}
				})
			}
		}
		viewModel.addressViewModel.error.observe(viewLifecycleOwner){
			it?.let {
				handleError(it)
			}
		}
	}
	override fun setUpViews() {
		super.setUpViews()
		Log.d("ERROR:","SignupFragment setUpViews ${this@SignupFragment}")

		binding.signupErrorTextView.visibility = View.GONE

		binding.signupNameEditText.onFocusChangeListener = focusChangeListener
		binding.signupMobileEditText.onFocusChangeListener = focusChangeListener
		binding.signupEmailEditText.onFocusChangeListener = focusChangeListener
		binding.signupPasswordEditText.onFocusChangeListener = focusChangeListener
		binding.signupCnfPasswordEditText.onFocusChangeListener = focusChangeListener
		binding.etShopName.onFocusChangeListener = focusChangeListener
		binding.etShopDescription.onFocusChangeListener = focusChangeListener
		binding.etDetailedAddress.onFocusChangeListener = focusChangeListener

		adapter = AddCategoryImagesAdapter(requireContext(), imgList)
		binding.recyclerViewAddedImage.adapter = adapter
		binding.btAddImg.setOnClickListener {
			getImages.launch("image/*")
		}
		binding.signupSignupBtn.setOnClickListener {
			onSignUp()

		}
		setUpClickableLoginText()
	}

	private fun setUpClickableLoginText() {
		val loginText = getString(R.string.signup_login_text)
		val ss = SpannableString(loginText)

		val clickableSpan = object : ClickableSpan() {
			override fun onClick(widget: View) {
				findNavController().navigate(R.id.action_signup_to_login)
			}
		}

		ss.setSpan(clickableSpan, 25, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
		binding.signupLoginTextView.apply {
			text = ss
			movementMethod = LinkMovementMethod.getInstance()
		}
	}

	private fun onSignUp() {
		val name = binding.signupNameEditText.text.toString()
		val mobile = standardlizePhoneNumber(binding.signupMobileEditText.text.toString())
		val email = binding.signupEmailEditText.text.toString()
		val password1 = binding.signupPasswordEditText.text.toString()
		val password2 = binding.signupCnfPasswordEditText.text.toString()
		val isAccepted = binding.signupPolicySwitch.isChecked

		/**shop info*/
		val shopName = binding.etShopName.text.toString()
		val shopDescription = binding.etShopDescription.text.toString()

		val provincePos = binding.spinnerProvinces.selectedItemPosition-1
		val provinceName = binding.spinnerProvinces.selectedItem?.toString()
		val districtPos = binding.spinnerDistricts.selectedItemPosition-1
		val districtName = binding.spinnerDistricts.selectedItem?.toString()
		val wardPos = binding.spinnerWards.selectedItemPosition-1
		val wardName = binding.spinnerWards.selectedItem?.toString()
		val detailedAddress = binding.etDetailedAddress.text.toString()

		viewModel.signUpSubmitData(name, mobile, email, password1, password2, isAccepted,
			shopName, shopDescription, imgList,
			provinceName, provincePos,districtName,districtPos,wardName,wardPos,detailedAddress
			)
	}
	private fun modifyErrors(err: SignUpViewErrors) {
		/**clear error text*/
		clearErrors()

		/**display new errors*/
		when (err) {
			SignUpViewErrors.NONE -> setEditTextsError()
			SignUpViewErrors.ERR_EMAIL -> {
				setEditTextsError(emailError = EMAIL_ERROR_TEXT)
				toastCenter("Error at fields: mail")
				setErrorText("Error at fields: mail")

			}
			SignUpViewErrors.ERR_MOBILE -> {
				setEditTextsError(mobError = MOB_ERROR_TEXT)
				toastCenter("Error at fields: phone number")
				setErrorText("Error at fields: phone number")

			}
			SignUpViewErrors.ERR_EMAIL_MOBILE -> {
				setEditTextsError(EMAIL_ERROR_TEXT, MOB_ERROR_TEXT)
				toastCenter("Error at fields: mail, phone number")
				setErrorText("Error at fields: mail, phone number")
			}
			SignUpViewErrors.ERR_EMPTY -> setErrorText("Fill all details.")
			SignUpViewErrors.ERR_NOT_ACC -> setErrorText("Accept the Terms.")
			SignUpViewErrors.ERR_PWD12NS -> setErrorText("Both passwords are not same!")
			SignUpViewErrors.ERR_PW_INVALID -> setErrorText(PASSWORD_ERROR_TEXT)
			SignUpViewErrors.NAME -> {
				binding.signupNameEditText.error = "Name  must be 2-50"
				toastCenter("User name  must be 2-50")
				setErrorText("User name  must be 2-50")
			}
			SignUpViewErrors.SHOP_NAME -> {
				binding.etShopName.error = "Name  must be 2-40"
				toastCenter("Shop name  must be 2-40")
			}
			SignUpViewErrors.SHOP_DESCRIPTION -> {
				binding.etShopDescription.error = "Shop description must be 25-500"
				toastCenter("Shop description  must be 25-500")
			}
			SignUpViewErrors.ERR_PROVINCE_EMPTY->{
				setSpinnerError(binding.tvProvinceError)
				setErrorText("Province must not be empty")

			}
			SignUpViewErrors.ERR_DISTRICT_EMPTY->{
				setSpinnerError(binding.tvDistrictError)
				setErrorText("District must not be empty")

			}
			SignUpViewErrors.ERR_WARD_EMPTY->{
				setSpinnerError(binding.tvWardError)
				setErrorText("Ward must not be empty")

			}
			SignUpViewErrors.ERR_DETAILED_ADDRESS_EMPTY -> {
				setErrorText("Detailed address must be 5-150 in length")
				toastCenter("Detailed address must be 5-150 in length")
				binding.tfDetailedAddress.error = "Detailed address must be 5-150 in length"
			}
		}
	}

	private fun clearErrors() {
		setEditTextsError()
		clearSpinnerError()
		binding.etShopName.error = null
		binding.signupNameEditText.error = null
		binding.etDetailedAddress.error = null
	}

	private fun clearSpinnerError() {
		binding.tvProvinceError.visibility = View.INVISIBLE
		binding.tvDistrictError.visibility = View.INVISIBLE
		binding.tvWardError.visibility = View.INVISIBLE
	}

	private fun setSpinnerError(tvError: TextView){
		tvError.visibility = View.VISIBLE
	}
//
	private fun setErrorText(errText: String?) {
		binding.signupErrorTextView.visibility = View.VISIBLE
		if (errText != null) {
			binding.signupErrorTextView.text = errText
		}
	}
	private fun setEditTextsError(emailError: String? = null, mobError: String? = null) {
		binding.signupEmailEditText.error = emailError
		binding.signupMobileEditText.error = mobError
		binding.signupErrorTextView.visibility = View.GONE
	}

	private fun uploadImage() {
		when {
			ContextCompat.checkSelfPermission(
				requireContext(),
				Manifest.permission.READ_EXTERNAL_STORAGE
			) == PackageManager.PERMISSION_GRANTED -> {
				viewModel.productViewModel.uploadImage(File(getFullPath(requireContext(), imgList[0])))
			}
//            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
//                requestPermissionLauncher.launch(
//                    Manifest.permission.READ_EXTERNAL_STORAGE)
//            }
			else -> {
				// You can directly ask for the permission.
				// The registered ActivityResultCallback gets the result of this request.
				requestPermissionLauncher.launch(
					Manifest.permission.READ_EXTERNAL_STORAGE)
			}
		}
	}
	private val getImages =
		registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
			result?.let {
				imgList.clear()
				imgList.add(result)
				adapter.data = imgList
				binding.recyclerViewAddedImage.adapter?.notifyDataSetChanged()
			}
		}
	private val requestPermissionLauncher =
		registerForActivityResult(
			ActivityResultContracts.RequestPermission()
		) { isGranted: Boolean ->
			if (isGranted) {
				viewModel.productViewModel.uploadImage(File(getFullPath(requireContext(), imgList[0])))
			} else {
				Toast.makeText(requireContext(), "Please grant permission to upload image", Toast.LENGTH_LONG).show()
			}
		}
	private fun populateProvinceToSpinner(spinnerProvinces: Spinner, it: List<Province>, onItemSelectedListener: AdapterView.OnItemSelectedListener) {
		val listProvinces = mutableListOf<String>()
		it.forEach { listProvinces.add(it.name) }
			listProvinces.add(0,"")
			val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, listProvinces.toTypedArray())
			adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
			spinnerProvinces.adapter = adapter
			spinnerProvinces.onItemSelectedListener = onItemSelectedListener
			spinnerProvinces.setSelection(0)
	}
	private fun populateDistrictToSpinner(spinnerProvinces: Spinner, it: List<District>, onItemSelectedListener: AdapterView.OnItemSelectedListener) {
		val listDistricts = mutableListOf<String>()
		it.forEach { listDistricts.add(it.name) }
			listDistricts.add(0,"")
			val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, listDistricts.toTypedArray())
			adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
			spinnerProvinces.adapter = adapter
			spinnerProvinces.onItemSelectedListener = onItemSelectedListener
			spinnerProvinces.setSelection(0)

	}
	private fun populateWardToSpinner(spinnerProvinces: Spinner, it: List<Ward>, onItemSelectedListener: AdapterView.OnItemSelectedListener) {
		val listWards = mutableListOf<String>()
		it.forEach { listWards.add(it.name) }
			listWards.add(0,"")
			val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, listWards.toTypedArray())
			adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
			spinnerProvinces.adapter = adapter
			spinnerProvinces.onItemSelectedListener = onItemSelectedListener
			spinnerProvinces.setSelection(0)

	}
	override fun onPause() {
		super.onPause()
		viewModel.clearError()
	}
}