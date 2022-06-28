package vn.ztech.software.ecomSeller.ui.address

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.os.bundleOf
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_add_edit_address.view.*
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.databinding.FragmentAddEditAddressBinding
import vn.ztech.software.ecomSeller.model.*
import vn.ztech.software.ecomSeller.ui.AddAddressViewErrors
import vn.ztech.software.ecomSeller.ui.BaseFragment
import vn.ztech.software.ecomSeller.util.extension.findPos

private const val TAG = "AddAddressFragment"

class AddEditAddressFragment : BaseFragment<FragmentAddEditAddressBinding>() {

	private val viewModel:AddressViewModel by viewModel()

	private  var isEdit = false
	private  var isDefaultAddress = false

	private var isAddedSelectedAddressToProvinces = false
	private var isAddedSelectedAddressToDistricts = false
	private var isAddedSelectedAddressToWards = false

	override fun setViewBinding(): FragmentAddEditAddressBinding {
		return FragmentAddEditAddressBinding.inflate(layoutInflater)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		isEdit = arguments?.getBoolean("isEdit")?:false
		isDefaultAddress = arguments?.getBoolean("IS_DEFAULT_ADDRESS")?:false
		if(isEdit){
			viewModel.isEdit.value = isEdit
			val addressItem = arguments?.getParcelable("ADDRESS_ITEM") as AddressItem?
			addressItem?.let {
				viewModel.currentSelectedAddressItem.value = addressItem
				fillDataInViews(addressItem)
			}
		}else{
			isAddedSelectedAddressToProvinces = true
			isAddedSelectedAddressToDistricts = true
			isAddedSelectedAddressToWards = true
		}
		if (!isEdit) {
			binding.addAddressTopAppBar.topAppBar.title = "Add Address"
			binding.addAddressSaveBtn.text = "Add Address"
		} else {
			binding.addAddressTopAppBar.topAppBar.title = "Edit Address"
			binding.addAddressSaveBtn.text = "Update Address"
		}

		if(viewModel.provinces.value == null) viewModel.getProvinces()

	}

	override fun setUpViews() {
		super.setUpViews()

		binding.addAddressTopAppBar.topAppBar.setNavigationOnClickListener {
			findNavController().navigateUp()
		}
		binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
		binding.etName.onFocusChangeListener = focusChangeListener
		binding.etPhoneNumber.onFocusChangeListener = focusChangeListener
		binding.etDetailedAddress.onFocusChangeListener = focusChangeListener

		binding.addAddressSaveBtn.setOnClickListener {
			onAddAddress()
		}
	}

	override fun observeView() {
		super.observeView()
		viewModel.loading.observe(viewLifecycleOwner){
			when (it) {
				true -> {
					binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
					binding.loaderLayout.circularLoader.showAnimationBehavior
				}
				false -> {
					binding.loaderLayout.circularLoader.hideAnimationBehavior
					binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
				}
			}
		}

		viewModel.uiError.observe(viewLifecycleOwner) { errList ->
			if (errList.isEmpty()) {
				//clear errors from ui
				binding.addAddressErrorTextView.visibility = View.GONE
				binding.tfName.error = null
				binding.tfPhoneNumber.error = null
				binding.tvProvinceError.visibility = View.INVISIBLE
				binding.tvDistrictError.visibility = View.INVISIBLE
				binding.tvWardError.visibility = View.INVISIBLE
				binding.tfDetailedAddress.error = null
			} else {
				modifyErrors(errList)
			}
		}
		viewModel.error.observe(viewLifecycleOwner){
			it?.let {
				handleError(it)
			}
		}
		viewModel.provinces.observe(viewLifecycleOwner){
			it?.let {
				//populate data to spinner
				var preSelectedPos = -1
				populateProvinceToSpinner(binding.spinnerProvinces, it, object : AdapterView.OnItemSelectedListener {
					override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
						if(binding.spinnerProvinces.getItemAtPosition(p2).toString().isNotEmpty()
							&& p2!=preSelectedPos){
							preSelectedPos = p2
							viewModel.getDistricts(viewModel.provinces.value?.get(p2-1)?.province_id?:-1)
							//clear values in district, ward spinner
							binding.spinnerDistricts.adapter = null
							binding.spinnerWards.adapter = null
						}
					}

					override fun onNothingSelected(p0: AdapterView<*>?) {
						TODO("Not yet implemented")
					}
				})
			}
		}
		viewModel.districts.observe(viewLifecycleOwner){
			it?.let {
				//populate data to spinner
				var preSelectedPos = -1
				populateDistrictToSpinner(binding.spinnerDistricts, it, object : AdapterView.OnItemSelectedListener {
					override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
						if(binding.spinnerDistricts.getItemAtPosition(p2).toString().isNotEmpty()
							&& p2!=preSelectedPos){
							viewModel.getWards(viewModel.districts.value?.get(p2-1)?.district_id?:-1)
							//clear data in spinnerWard
							binding.spinnerWards.adapter = null
						}
					}

					override fun onNothingSelected(p0: AdapterView<*>?) {
						TODO("Not yet implemented")
					}
				})
			}
		}
		viewModel.wards.observe(viewLifecycleOwner){
			it?.let {
				//populate data to spinner
				populateWardToSpinner(binding.spinnerWards, it, object : AdapterView.OnItemSelectedListener {
					override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
						if(binding.spinnerWards.getItemAtPosition(p2).toString().isNotEmpty()){}
					}

					override fun onNothingSelected(p0: AdapterView<*>?) {
						TODO("Not yet implemented")
					}
				})
			}
		}
		viewModel.addAddressStatus.observe(viewLifecycleOwner){
			it?.let {
				if(it){
					binding.etName.onFocusChangeListener = null
					binding.etPhoneNumber.onFocusChangeListener = null
					binding.etDetailedAddress.onFocusChangeListener = null
					//navigate back to list address, send an Address obj with
					findNavController().navigate(
						R.id.action_addEditAddressFragment_to_addressFragment,
						bundleOf(
							"address" to viewModel.addresses.value
						))
				}
			}
		}
		viewModel.updateAddressStatus.observe(viewLifecycleOwner){
			it?.let {
				if(it){
					binding.etName.onFocusChangeListener = null
					binding.etPhoneNumber.onFocusChangeListener = null
					binding.etDetailedAddress.onFocusChangeListener = null
					//navigate back to list address, send an Address obj with
					findNavController().navigate(
						R.id.action_addEditAddressFragment_to_addressFragment,
						bundleOf(
							"address" to viewModel.addresses.value
						))
				}
			}
		}
	}

	private fun populateProvinceToSpinner(spinnerProvinces: Spinner, it: List<Province>, onItemSelectedListener: AdapterView.OnItemSelectedListener) {
		val listProvinces = mutableListOf<String>()
		it.forEach { listProvinces.add(it.name) }
		if (isAddedSelectedAddressToProvinces){
			listProvinces.add(0,"")
			val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, listProvinces.toTypedArray())
			adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
			spinnerProvinces.adapter = adapter
			spinnerProvinces.onItemSelectedListener = onItemSelectedListener
			spinnerProvinces.setSelection(0)
		}else{
			val currentProvincePos = it.findPos(viewModel.currentSelectedAddressItem.value?.province?.provinceId!!)
			listProvinces.add(0,"")
			val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, listProvinces.toTypedArray())
			adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
			spinnerProvinces.adapter = adapter
			spinnerProvinces.onItemSelectedListener = onItemSelectedListener
			if(!isAddedSelectedAddressToProvinces)spinnerProvinces.setSelection(currentProvincePos+1)
			isAddedSelectedAddressToProvinces = true
		}
	}
	private fun populateDistrictToSpinner(spinnerProvinces: Spinner, it: List<District>, onItemSelectedListener: AdapterView.OnItemSelectedListener) {
		val listDistricts = mutableListOf<String>()
		it.forEach { listDistricts.add(it.name) }
		if (isAddedSelectedAddressToDistricts){
			listDistricts.add(0,"")
			val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, listDistricts.toTypedArray())
			adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
			spinnerProvinces.adapter = adapter
			spinnerProvinces.onItemSelectedListener = onItemSelectedListener
			spinnerProvinces.setSelection(0)
		}else{
			val currentDistrictPos = it.findPos(viewModel.currentSelectedAddressItem.value?.district?.districtId!!)
			//this flag ensure that only one item is added to the list, otherwise this will lead to an infinite loop
			listDistricts.add(0,"")
			val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, listDistricts.toTypedArray())
			adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
			spinnerProvinces.adapter = adapter
			spinnerProvinces.onItemSelectedListener = onItemSelectedListener
			if(!isAddedSelectedAddressToDistricts) spinnerProvinces.setSelection(currentDistrictPos+1)
			isAddedSelectedAddressToDistricts = true
		}
	}
	private fun populateWardToSpinner(spinnerProvinces: Spinner, it: List<Ward>, onItemSelectedListener: AdapterView.OnItemSelectedListener) {
		val listWards = mutableListOf<String>()
		it.forEach { listWards.add(it.name) }
		if (isAddedSelectedAddressToWards){
			listWards.add(0,"")
			val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, listWards.toTypedArray())
			adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
			spinnerProvinces.adapter = adapter
			spinnerProvinces.onItemSelectedListener = onItemSelectedListener
			spinnerProvinces.setSelection(0)
		}else{
			val currentWardPos = it.findPos(viewModel.currentSelectedAddressItem.value?.ward?.code!!)
			//this flag ensure that only one item is added to the list, otherwise this will lead to an infinite loop
			listWards.add(0,"")
			val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, listWards.toTypedArray())
			adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
			spinnerProvinces.adapter = adapter
			spinnerProvinces.onItemSelectedListener = onItemSelectedListener
			if(!isAddedSelectedAddressToWards) spinnerProvinces.setSelection(currentWardPos+1)
			isAddedSelectedAddressToWards = true
		}
	}
	private fun fillDataInViews(addressItem: AddressItem?) {
		addressItem?.let {
			binding.addAddressTopAppBar.topAppBar.title = "Edit Address"
			binding.etName.setText(addressItem.receiverName)
			binding.etPhoneNumber.setText(addressItem.receiverPhoneNumber)
			binding.etDetailedAddress.setText(addressItem.detailedAddress)
			binding.addAddressSaveBtn.setText(R.string.save_address_btn_text)
			binding.cbIsDefaultAddress.isChecked = isDefaultAddress
		}

	}
//
//		private fun makeToast(errText: String) {
//			Toast.makeText(context, errText, Toast.LENGTH_LONG).show()
//		}
//
//		private fun setLoaderState(isVisible: Int = View.GONE) {
//			binding.loaderLayout.loaderFrameLayout.visibility = isVisible
//			if (isVisible == View.GONE) {
//				binding.loaderLayout.circularLoader.hideAnimationBehavior
//			} else {
//				binding.loaderLayout.circularLoader.showAnimationBehavior
//			}
//		}

		private fun onAddAddress() {
			val receiverName = binding.etName.text.toString()
			val receiverPhoneNumber = binding.etPhoneNumber.text.toString()
			val provincePos = binding.spinnerProvinces.selectedItemPosition-1
			val provinceName = binding.spinnerProvinces.selectedItem?.toString()
			val districtPos = binding.spinnerDistricts.selectedItemPosition-1
			val districtName = binding.spinnerDistricts.selectedItem?.toString()
			val wardPos = binding.spinnerWards.selectedItemPosition-1
			val wardName = binding.spinnerWards.selectedItem?.toString()
			val detailedAddress = binding.etDetailedAddress.text.toString()
			val isSelectedAsDefaultAddress = binding.cbIsDefaultAddress.isChecked

			viewModel.submitAddress(
				receiverName,
				receiverPhoneNumber,
				provinceName,
				provincePos,
				districtName,
				districtPos,
				wardName,
				wardPos,
				detailedAddress,
				isSelectedAsDefaultAddress
			)
		}

		private fun modifyErrors(errList: List<AddAddressViewErrors>) {
			binding.tfName.error = null
			binding.tfPhoneNumber.error = null
			binding.tvProvinceError.visibility = View.INVISIBLE
			binding.tvDistrictError.visibility = View.INVISIBLE
			binding.tvWardError.visibility = View.INVISIBLE
			binding.tfDetailedAddress.error = null
			errList.forEach { err ->
				when (err) {
					AddAddressViewErrors.EMPTY -> setEditTextsError(true)
					AddAddressViewErrors.ERR_NAME_EMPTY ->
						setEditTextsError(true, binding.tfName)
					AddAddressViewErrors.ERR_PHONE_EMPTY ->
						setEditTextsError(true, binding.tfPhoneNumber)
					AddAddressViewErrors.ERR_PROVINCE_EMPTY ->
						setSpinnerError(binding.tvProvinceError)
					AddAddressViewErrors.ERR_DISTRICT_EMPTY ->
						setSpinnerError(binding.tvDistrictError)
					AddAddressViewErrors.ERR_WARD_EMPTY ->
						setSpinnerError(binding.tvWardError)
					AddAddressViewErrors.ERR_DETAILED_ADDRESS_EMPTY ->
						setEditTextsError(true, binding.tfDetailedAddress)
					else -> {}
				}
			}
		}

		private fun setEditTextsError(isEmpty: Boolean, editText: TextInputLayout? = null) {
			if (isEmpty) {
				binding.addAddressErrorTextView.visibility = View.VISIBLE
				if (editText != null) {
					editText.error = "Please Fill the Form with correct value"
					editText.errorIconDrawable = null
				}
			} else {
				binding.addAddressErrorTextView.visibility = View.GONE
				editText!!.error = "Invalid!"
				editText.errorIconDrawable = null
			}
		}
		private fun setSpinnerError(tvError: TextView){
			tvError.visibility = View.VISIBLE
		}

	override fun onStop() {
		super.onStop()
		isEdit = false
		isAddedSelectedAddressToProvinces = false
		isAddedSelectedAddressToDistricts = false
		isAddedSelectedAddressToWards = false
		viewModel.clearErrors()
	}
}
