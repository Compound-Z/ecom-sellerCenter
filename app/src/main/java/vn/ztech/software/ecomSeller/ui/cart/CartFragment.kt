package vn.ztech.software.ecomSeller.ui.cart

import androidx.recyclerview.widget.ConcatAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.api.response.CartProductResponse
import vn.ztech.software.ecomSeller.databinding.FragmentCartBinding
import vn.ztech.software.ecomSeller.databinding.LayoutCircularLoaderBinding
import vn.ztech.software.ecomSeller.databinding.LayoutPriceCardBinding
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.ui.BaseFragment
import vn.ztech.software.ecomSeller.ui.product_details.ProductDetailsViewModel

private const val TAG = "CartFragment"

class CartFragment : BaseFragment<FragmentCartBinding>() {

    private val viewModel: CartViewModel by viewModel()
    private val productDetailsViewModel: ProductDetailsViewModel by viewModel()
    private lateinit var itemsAdapter: CartItemAdapter
    private lateinit var concatAdapter: ConcatAdapter

    override fun setViewBinding(): FragmentCartBinding {
        return FragmentCartBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getListProductsInCart()
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
        binding.loaderLayout.circularLoader.showAnimationBehavior
        binding.cartAppBar.topAppBar.title = getString(R.string.cart_fragment_label)
        binding.cartEmptyTextView.visibility = View.GONE
        binding.cartCheckOutBtn.setOnClickListener {
            navigateToOrderFragment(viewModel.products.value?: emptyList())
        }
        if (context != null) {
            setItemsAdapter(viewModel.products.value?: emptyList())
            concatAdapter = ConcatAdapter(itemsAdapter, PriceCardAdapter(viewModel.priceData.value))
            binding.cartProductsRecyclerView.adapter = concatAdapter
        }
    }

    private fun navigateToOrderFragment(products: List<CartProductResponse>) {
        if (products.isEmpty()){
            Toast.makeText(context, "Cart is empty, please go shopping!", Toast.LENGTH_LONG).show()
        }else{
            Log.d("xxxxx", ArrayList(products).toString())
            findNavController().navigate(
                R.id.action_cartFragment_to_orderFragment,
                bundleOf("products" to ArrayList(products))
            )
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

        viewModel.products.observe(viewLifecycleOwner) { products ->
                updateAdapter(products)
        }

        viewModel.deleteProductStatus.observe(viewLifecycleOwner) {
            it?.let {
                if (it){
                    viewModel.getListProductsInCart(false)
                }
            }
        }
        viewModel.addProductStatus.observe(viewLifecycleOwner){
            it?.let {
                if (it){
                    viewModel.getListProductsInCart(false)
                }
            }
        }
        viewModel.adjustProductStatus.observe(viewLifecycleOwner){
            it?.let {
                if (it){
                    viewModel.getListProductsInCart(false)
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner){
            it?.let {
                handleError(it)
            }
        }
        productDetailsViewModel.error.observe(viewLifecycleOwner){
            it?.let {
                handleError(it)
            }
        }
    }
    private fun setItemsAdapter(products: List<CartProductResponse>) {
        itemsAdapter = CartItemAdapter(requireContext(), products)
        itemsAdapter.onClickListener = object : CartItemAdapter.OnClickListener {

            override fun onItemClick(product: CartProductResponse) {

                        findNavController().navigate(
                            R.id.action_cartFragment_to_productDetailsFragment,
                            bundleOf("product" to Product(_id = product.productId,product.name,"",true, product.price, "", "", 0, product.weight, 0 , 0),
                            "ADD_TO_CART_BUTTON_ENABLED" to false)
                        )
            }

            override fun onDeleteClick(itemId: String, itemBinding: LayoutCircularLoaderBinding) {
                Log.d(TAG, "onDelete: initiated")
                showDeleteDialog(itemId, itemBinding)
            }

            override fun onPlusClick(itemId: String) {
                Log.d(TAG, "onPlus: Increasing quantity ${itemId}")
                viewModel.addProductToCart(itemId, false)
            }

            override fun onMinusClick(itemId: String, currQuantity: Int,itemBinding: LayoutCircularLoaderBinding) {
                Log.d(TAG, "onMinus: decreasing quantity ${itemId} ${currQuantity}")
                if (currQuantity == 1) {
                    showDeleteDialog(itemId, itemBinding)
                } else {
                    viewModel.adjustProductQuantity(itemId, currQuantity - 1)
                }
            }
        }
    }

    private fun updateAdapter(products: List<CartProductResponse>) {
        Log.d(TAG, "update"+ products.size.toString())
        if(products.isEmpty()){
            binding.cartProductsRecyclerView.visibility = View.GONE
            binding.cartEmptyTextView.visibility = View.VISIBLE

        }else{
            binding.cartProductsRecyclerView.visibility = View.VISIBLE
            binding.cartEmptyTextView.visibility = View.GONE
        }
        itemsAdapter.apply {
            this.products = products
        }
        concatAdapter = ConcatAdapter(itemsAdapter, PriceCardAdapter(viewModel.priceData.value))
        binding.cartProductsRecyclerView.adapter = concatAdapter
        binding.cartProductsRecyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onStop() {
        super.onStop()
        viewModel.deleteProductStatus.value = false
        viewModel.adjustProductStatus.value = false
        viewModel.addProductStatus.value = false
        viewModel.clearErrors()
    }

    private fun showDeleteDialog(itemId: String, itemBinding: LayoutCircularLoaderBinding) {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(getString(R.string.delete_dialog_title_text))
                .setMessage(getString(R.string.delete_cart_item_message_text))
                .setNegativeButton(getString(R.string.pro_cat_dialog_cancel_btn)) { dialog, _ ->
                    dialog.cancel()
                    itemBinding.loaderFrameLayout.visibility = View.GONE
                }
                .setPositiveButton(getString(R.string.delete_dialog_delete_btn_text)) { dialog, _ ->
                    itemBinding.loaderFrameLayout.visibility = View.VISIBLE
                    viewModel.deleteProductFromCart(itemId)
                    dialog.dismiss()
                }.setOnCancelListener {
                    itemBinding.loaderFrameLayout.visibility = View.GONE
                }
                .show()
        }
    }

    inner class PriceCardAdapter(val priceData: CartViewModel.PriceData?) : RecyclerView.Adapter<PriceCardAdapter.ViewHolder>() {

        inner class ViewHolder(private val priceCardBinding: LayoutPriceCardBinding) :
            RecyclerView.ViewHolder(priceCardBinding.root) {
            fun bind() {
                priceCardBinding.tvNumberItems.text = getString(
                    R.string.price_card_items_string,
                    priceData?.numberOfItem.toString()
                )
                priceCardBinding.priceItemsAmountTv.text =
                    getString(R.string.price_text, priceData?.subTotal.toString())
                priceCardBinding.priceTotalAmountTv.text = getString(R.string.price_text, viewModel.priceData.value?.subTotal.toString())
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutPriceCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind()
        }

        override fun getItemCount() = 1
    }

}