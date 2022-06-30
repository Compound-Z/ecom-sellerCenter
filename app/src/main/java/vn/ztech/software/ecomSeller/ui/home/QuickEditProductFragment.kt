package vn.ztech.software.ecomSeller.ui.home

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.common.StoreDataStatus
import vn.ztech.software.ecomSeller.databinding.FragmentQuickEditProductBinding
import vn.ztech.software.ecomSeller.ui.AddProductViewErrors
import vn.ztech.software.ecomSeller.ui.BaseFragment2


class QuickEditProductFragment : BaseFragment2<FragmentQuickEditProductBinding>() {
    private val viewModel: HomeViewModel by sharedViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.takeIf { it.containsKey("product") }?.let {
            viewModel.currentSelectedProduct.value = arguments?.getParcelable("product")
        }
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.addProAppBar.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.addProAppBar.topAppBar.title = "Quick update: ${viewModel.currentSelectedProduct.value?.name}"
        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE

        binding.etPrice.onFocusChangeListener = focusChangeListener
        binding.etStockNumber.onFocusChangeListener = focusChangeListener

        binding.btAddEditProduct.setOnClickListener {
            onEditProduct()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.currentSelectedProduct.value?.let {
            binding.etPrice.setText(it.price.toString())
            binding.etStockNumber.setText(it.stockNumber.toString())
        }
    }

    override fun observeView() {
        super.observeView()
        viewModel.storeDataStatus.observe(viewLifecycleOwner) { status ->
            if(status == StoreDataStatus.LOADING) {
                binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
                binding.loaderLayout.circularLoader.showAnimationBehavior
            }else{
                binding.loaderLayout.circularLoader.hideAnimationBehavior
                binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
            }
        }
        viewModel.updatedProduct.observe(viewLifecycleOwner){
            it?.let {
                viewModel.getProducts()
                toastCenter("Updated product ${it.name} successfully!")
            }
        }
        viewModel.error.observe(viewLifecycleOwner){
            it ?: return@observe
            handleError(it)
        }
        viewModel.errorUI.observe(viewLifecycleOwner){
            it ?: return@observe
            if(it == AddProductViewErrors.NONE){
                //call api add product
                viewModel.quickUpdate(viewModel.currentSelectedProduct.value?._id, viewModel.currentQuickProductInput.value)
            }else{
                modifyErrors(it)
            }
        }
    }

    private fun onEditProduct() {
        viewModel.checkQuickUpdateInputData(binding.etPrice.text.toString(), binding.etStockNumber.text.toString())
    }

    private fun modifyErrors(err: AddProductViewErrors) {
        binding.tvError.visibility = View.GONE
        when (err) {
            AddProductViewErrors.EMPTY -> {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = getString(R.string.add_product_error_string)
                Toast.makeText(requireContext(), "Please fill all the details!", Toast.LENGTH_LONG).apply {
                    setGravity(Gravity.CENTER, 0, 0)
                }.show()
            }
            AddProductViewErrors.PRICE->{
                binding.etPrice.error = "Price must be > 0"
                toastCenter("Price must be > 0")

            }
            AddProductViewErrors.STOCK->{
                binding.etStockNumber.error = "Stock must be > 0"
                toastCenter("Stock must be > 0")

            }
            else->{}
        }
    }

    override fun setViewBinding(): FragmentQuickEditProductBinding {
        return FragmentQuickEditProductBinding.inflate(layoutInflater)
    }

    override fun onStop() {
        super.onStop()
        binding.etPrice.onFocusChangeListener = null
        binding.etStockNumber.onFocusChangeListener = null
        viewModel.clearErrors()
    }
}