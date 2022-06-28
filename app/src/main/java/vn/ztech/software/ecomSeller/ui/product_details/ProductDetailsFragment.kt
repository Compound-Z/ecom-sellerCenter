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
import vn.ztech.software.ecomSeller.ui.cart.CartViewModel
import vn.ztech.software.ecomSeller.ui.cart.DialogAddToCartSuccessFragment
import vn.ztech.software.ecomSeller.ui.main.MainActivity
import vn.ztech.software.ecomSeller.util.extension.showErrorDialog

class ProductDetailsFragment : Fragment(),
    DialogAddToCartSuccessFragment.OnClick {
    val TAG = "ProductDetailsFragment"
    private lateinit var binding: FragmentProductDetailsBinding
    private val viewModel: ProductDetailsViewModel by viewModel()
    private val cartViewModel: CartViewModel by viewModel()
    var isAddToCartButtonEnabled = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductDetailsBinding.inflate(layoutInflater)
//        if (viewModel.isSeller()) {
//            binding.proDetailsAddCartBtn.visibility = View.GONE
//        } else {
            binding.proDetailsAddCartBtn.visibility = View.VISIBLE
            binding.proDetailsAddCartBtn.setOnClickListener {
                    addToCart(viewModel.product.value?._id)
            }
//        }

        binding.layoutViewsGroup.visibility = View.GONE
        binding.proDetailsAddCartBtn.visibility = View.GONE
        setObservers()
        return binding.root
    }

    private fun addToCart(_id: String?) {
        cartViewModel.addProductToCart(_id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product = arguments?.getParcelable("product") as Product?
        isAddToCartButtonEnabled = arguments?.getBoolean("ADD_TO_CART_BUTTON_ENABLED")?:true
        if(!isAddToCartButtonEnabled) {
            binding.proDetailsAddCartBtn.visibility = View.GONE
        }
        viewModel.product.value = product
        viewModel.getProductDetails(product?._id?:"")
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
        viewModel.productDetails.observe(viewLifecycleOwner){
            setViews()
        }
        cartViewModel.loading.observe(viewLifecycleOwner){
            it?.let {
                if (it){
                    binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
                }else{
                    binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                }
            }
        }
        cartViewModel.addProductStatus.observe(viewLifecycleOwner){
            it?.let {
                if (it){
                    showBottomDialogSuccess()
                }
            }
        }
        cartViewModel.error.observe(viewLifecycleOwner){
            it?.let {
                showErrorDialog(it)
            }
        }
        viewModel.error.observe(viewLifecycleOwner){
            it?.let {
                showErrorDialog(it)
            }
        }
    }

    private fun showBottomDialogSuccess() {
        viewModel.product.value?.let {
            DialogAddToCartSuccessFragment(it, this@ProductDetailsFragment).show(parentFragmentManager,"DialogAddToCartSuccessFragment")
        }
    }

    private fun setViews() {
        binding.layoutViewsGroup.visibility = View.VISIBLE
        if(isAddToCartButtonEnabled)binding.proDetailsAddCartBtn.visibility = View.VISIBLE
        binding.addProAppBar.topAppBar.title = viewModel.product.value?.name
        binding.addProAppBar.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setImagesView()

        binding.proDetailsTitleTv.text = viewModel.product.value?.name.toString()
        binding.proDetailsRatingBar.rating = (viewModel.product.value?.averageRating ?: 0.0).toFloat()
        binding.tvNumberOfReviews.text = "(${viewModel.productDetails.value?.numOfReviews.toString()})"
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

    override fun onButtonGoToCartClicked() {
        cartViewModel.addProductStatus.value = false /**refresh live data value to the original, so that the bottom sheet will not show up when navigate back to this fragment*/
        (activity as MainActivity).binding.homeBottomNavigation.selectedItemId = R.id.cartFragment
    }
    override fun onStop() {
        super.onStop()
        viewModel.clearErrors()
    }
}