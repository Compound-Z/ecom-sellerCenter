package vn.ztech.software.ecomSeller.ui.address

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.databinding.FragmentAddressBinding
import vn.ztech.software.ecomSeller.ui.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.ecomSeller.model.Address
import vn.ztech.software.ecomSeller.model.AddressItem
import vn.ztech.software.ecomSeller.util.extension.checkIsDefaultAddress


private const val TAG = "AddressFragment"

class AddressFragment : BaseFragment<FragmentAddressBinding>() {
    private lateinit var addressAdapter: AddressAdapter
    private val viewModel: AddressViewModel by viewModel()
    override fun setViewBinding(): FragmentAddressBinding {
        return FragmentAddressBinding.inflate(layoutInflater)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val address = arguments?.getParcelable<Address?>("address")
        if (address != null){
            viewModel.addresses.value = address
        }else{
            viewModel.getAddresses()
        }
        arguments?.takeIf { it.containsKey("fromWhere") }?.let {
            val fromWhere = it.getString("fromWhere")
            when(fromWhere){
                "AccountFragment"-> {
                    viewModel.fromWhere.value = "AccountFragment"
                }
            }
        }
    }


    override fun setUpViews() {
        super.setUpViews()
        binding.addressAppBar.topAppBar.title = "Address"
        binding.addressAppBar.topAppBar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
        binding.addressAddBtn.visibility = View.GONE
        binding.addressAddBtn.setOnClickListener {
            navigateToAddEditAddress(false)
        }
        binding.addressEmptyTextView.visibility = View.GONE
        binding.btChooseAddress.setOnClickListener {
            navigateToOrderFragment()
        }
        if (context != null) {
            val addressItems = viewModel.addresses.value?.addresses ?: emptyList<AddressItem>()
            val defaultAddressId = viewModel.addresses.value?.defaultAddressId?:""
            addressAdapter = AddressAdapter(requireContext(), addressItems, defaultAddressId,true)
            addressAdapter.onClickListener = object : AddressAdapter.OnClickListener {

                override fun onEditClick(addressItem: AddressItem) {
                    navigateToAddEditAddress(true, addressItem)
                }

                override fun onDeleteClick(addressId: String) {
                    Log.d(TAG, "onDeleteAddress: initiated")
                    showDeleteDialog(addressId)
                }

                override fun onClick() {
                    binding.btChooseAddress.isEnabled = true
                }
            }
            binding.addressAddressesRecyclerView.adapter = addressAdapter
        }
    }

    private fun navigateToOrderFragment() {
        findNavController().navigate(R.id.action_addressFragment_to_orderFragment,
            bundleOf("ADDRESS_ITEM" to addressAdapter.lastCheckedAddressItem)
            )
    }


    override fun observeView() {
        super.observeView()
        viewModel.loading.observe(viewLifecycleOwner) { status ->
            when (status) {
                true -> {
                    binding.addressEmptyTextView.visibility = View.GONE
                    binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
                    binding.loaderLayout.circularLoader.showAnimationBehavior
                }
                false -> {
                    binding.addressAddBtn.visibility = View.VISIBLE
                    binding.loaderLayout.circularLoader.hideAnimationBehavior
                    binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                }
            }
        }

        viewModel.addresses.observe(viewLifecycleOwner){
            it?.let {
                if(!it.addresses.isNullOrEmpty()){
                    addressAdapter.data = it.addresses
                    addressAdapter.defaultAddressId = it.defaultAddressId
                    binding.addressAddressesRecyclerView.adapter = addressAdapter
                    binding.addressAddressesRecyclerView.adapter?.notifyDataSetChanged()
                    //re-constraint the button so that it display at the bottom of the recycler list
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(binding.constraintLayout)
                    constraintSet.connect(R.id.address_add_btn, ConstraintSet.TOP, R.id.address_addresses_recycler_view, ConstraintSet.BOTTOM )
                    constraintSet.applyTo(binding.constraintLayout)
                }
                else {
                    binding.addressAddressesRecyclerView.visibility = View.INVISIBLE
                    binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                    binding.loaderLayout.circularLoader.hideAnimationBehavior
                    binding.addressEmptyTextView.visibility = View.VISIBLE
                    //re-constraint the button so that it display at the center of the screen
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(binding.constraintLayout)
                    constraintSet.connect(R.id.address_add_btn, ConstraintSet.TOP, R.id.address_empty_text_view, ConstraintSet.BOTTOM )
                    constraintSet.applyTo(binding.constraintLayout)
                }
            }
            binding.addressAddBtn.visibility = View.VISIBLE
        }
        viewModel.fromWhere.observe(viewLifecycleOwner){
            if (it == "AccountFragment"){
                binding.btChooseAddress.visibility = View.GONE
            }
        }

        viewModel.error.observe(viewLifecycleOwner){
            it?.let {
                handleError(it)
            }
        }
    }

    private fun showDeleteDialog(addressId: String) {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(getString(R.string.delete_dialog_title_text))
                .setMessage(getString(R.string.delete_address_message_text))
                .setNeutralButton(getString(R.string.pro_cat_dialog_cancel_btn)) { dialog, _ ->
                    dialog.cancel()
                }
                .setPositiveButton(getString(R.string.delete_dialog_delete_btn_text)) { dialog, _ ->
                    viewModel.deleteAddress(addressId)
                    dialog.cancel()
                }
                .show()
        }
    }

    private fun navigateToAddEditAddress(isEdit: Boolean, addressItem: AddressItem? = null) {
        findNavController().navigate(
            R.id.action_addressFragment_to_addEditAddressFragment,
            bundleOf("isEdit" to isEdit,
                "ADDRESS_ITEM" to addressItem,
                "IS_DEFAULT_ADDRESS" to viewModel.addresses.value?.checkIsDefaultAddress(addressItem))
        )
        android.util.Patterns.PHONE
    }

    override fun onStop() {
        super.onStop()
        viewModel.clearErrors()
    }
}