package vn.ztech.software.ecomSeller.ui.product_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.PagerSnapHelper
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.databinding.FragmentProductDetailsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.ecomSeller.common.StoreDataStatus
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.ui.BaseFragment2
import vn.ztech.software.ecomSeller.ui.cart.CartViewModel
import vn.ztech.software.ecomSeller.ui.cart.DialogAddToCartSuccessFragment
import vn.ztech.software.ecomSeller.ui.main.MainActivity
import vn.ztech.software.ecomSeller.util.extension.showErrorDialog

class ProductDetailsFragment : BaseFragment2<FragmentProductDetailsBinding>() {
    val TAG = "ProductDetailsFragment"
    private val viewModel: ProductDetailsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val productId = arguments?.getString("productId") as String?
        viewModel.getProduct(productId?:"")
        viewModel.getProductDetails(productId?:"")
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.proDetailsAddCartBtn.visibility = View.VISIBLE
        binding.layoutViewsGroup.visibility = View.GONE
        binding.proDetailsAddCartBtn.visibility = View.GONE
    }

    override fun observeView() {
        super.observeView()
        setObservers()

    }

    private fun setObservers() {
        viewModel.storeDataStatus.observe(viewLifecycleOwner) {
            when (it) {
                StoreDataStatus.DONE -> {
                    binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                    binding.proDetailsLayout.visibility = View.VISIBLE
                }
                else -> {
//                    binding.proDetailsLayout.visibility = View.GONE
                    binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
                }
            }
        }
        viewModel.product.observe(viewLifecycleOwner){
            if(viewModel.checkIsDataReady()){
                setViews()
            }
        }
        viewModel.productDetails.observe(viewLifecycleOwner){
            if(viewModel.checkIsDataReady()){
                setViews()
            }
        }
        viewModel.error.observe(viewLifecycleOwner){
            it?.let {
                handleError(it)
            }
        }
    }


    private fun setViews() {
        binding.layoutViewsGroup.visibility = View.VISIBLE
        binding.addProAppBar.topAppBar.title = viewModel.product.value?.name
        binding.addProAppBar.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setImagesView()

        binding.proDetailsTitleTv.text = viewModel.product.value?.name.toString()
        binding.proDetailsRatingBar.rating = (viewModel.product.value?.averageRating ?: 0.0f)
        binding.tvNumberOfReviews.text = "(${viewModel.product.value?.numberOfRating.toString()})"
        binding.tvSoldNumber.text = "Sold: ${viewModel.product.value?.saleNumber.toString()}"

        binding.proDetailsPriceTv.text = resources.getString(
            R.string.pro_details_price_value,
            viewModel.product.value?.price
        )
        binding.proDetailsSpecificsText.text = viewModel.productDetails.value?.description ?: ""
        binding.categoryValue.text = viewModel.product.value?.category
        binding.unitValue.text = viewModel.productDetails.value?.unit
        binding.brandValue.text = viewModel.productDetails.value?.brandName
        binding.originValue.text = viewModel.productDetails.value?.origin
    }

    private fun setImagesView() {
        if (context != null) {
            binding.proDetailsImagesRecyclerview.isNestedScrollingEnabled = false
            val adapter = ProductImagesAdapter(
                requireContext(),
                viewModel.productDetails.value?.imageUrls ?: emptyList()
            )
            binding.proDetailsImagesRecyclerview.adapter = adapter
            val rad = resources.getDimension(R.dimen.radius)
            val dotsHeight = resources.getDimensionPixelSize(R.dimen.dots_height)
            val inactiveColor = ContextCompat.getColor(requireContext(), R.color.gray)
            val activeColor = ContextCompat.getColor(requireContext(), R.color.blue_accent_300)
            val itemDecoration =
                DotsIndicatorDecoration(rad, rad * 4, dotsHeight, inactiveColor, activeColor)
            binding.proDetailsImagesRecyclerview.addItemDecoration(itemDecoration)
            binding.proDetailsImagesRecyclerview.adapter
            binding.proDetailsImagesRecyclerview.onFlingListener = null;
            PagerSnapHelper().attachToRecyclerView(binding.proDetailsImagesRecyclerview)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.clearErrors()
    }

    override fun setViewBinding(): FragmentProductDetailsBinding {
        return FragmentProductDetailsBinding.inflate(layoutInflater)
    }

}