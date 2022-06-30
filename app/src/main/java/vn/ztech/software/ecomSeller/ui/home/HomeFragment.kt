package vn.ztech.software.ecomSeller.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.common.StoreDataStatus
import vn.ztech.software.ecomSeller.databinding.FragmentHomeBinding
import vn.ztech.software.ecomSeller.ui.common.ItemDecorationRecyclerViewPadding
import vn.ztech.software.ecomSeller.ui.home.ListProductsAdapter.OnClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.ecomSeller.exception.RefreshTokenExpiredException
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.ui.MyOnFocusChangeListener
import vn.ztech.software.ecomSeller.ui.auth.LoginSignupActivity
import vn.ztech.software.ecomSeller.ui.splash.ISplashUseCase
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.extension.showErrorDialog

private const val TAG = "HomeFragment"


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by sharedViewModel()
    private lateinit var listProductsAdapter: ListProductsAdapter
    protected val focusChangeListener = MyOnFocusChangeListener()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setViews()
        setObservers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getProducts()
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getProducts()
        }
    }

    private fun setViews() {
        setHomeTopAppBar()
        if (context != null) {
            setUpProductAdapter(viewModel.allProducts.value)
            binding.productsRecyclerView.adapter = listProductsAdapter
        }
        binding.homeFabAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addEditProductFragment)
        }
        binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
        binding.loaderLayout.circularLoader.showAnimationBehavior
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setObservers() {
        viewModel.storeDataStatus.observe(viewLifecycleOwner) { status ->
            if(status == StoreDataStatus.LOADING) {
                binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
                binding.loaderLayout.circularLoader.showAnimationBehavior
            }else{
                binding.loaderLayout.circularLoader.hideAnimationBehavior
                binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
            }
        }
        viewModel.allProducts.observe(viewLifecycleOwner) { listProducts->
            if (listProducts.isNotEmpty()) {
                binding.tvNoProductFound.visibility = View.GONE
                binding.loaderLayout.circularLoader.hideAnimationBehavior
                binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                binding.productsRecyclerView.visibility = View.VISIBLE
                binding.productsRecyclerView.adapter?.apply {
                    listProductsAdapter.data =
                        getMixedDataList(listProducts, getAdsList())
                    notifyDataSetChanged()
                }
            }else{
                binding.tvNoProductFound.visibility = View.VISIBLE
            }
        }
        viewModel.deletedProductStatus.observe(viewLifecycleOwner){
            it?.let {
                Toast.makeText(requireContext(), "Delete product successfully!", Toast.LENGTH_LONG).apply {
                    setGravity(Gravity.CENTER, 0, 0)
                }.show()
                viewModel.deletedProductStatus.value = null
                viewModel.getProducts()
            }
        }
        viewModel.error.observe(viewLifecycleOwner){
            it ?: return@observe
            handleError(it)
        }
    }
    fun handleError(error: CustomError){
        if(error is RefreshTokenExpiredException){
            openLogInSignUpActivity(ISplashUseCase.PAGE.LOGIN)
        }else{
            showErrorDialog(error)
        }
    }

    fun openLogInSignUpActivity(page: ISplashUseCase.PAGE){
        val intent = Intent(activity, LoginSignupActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("PAGE", page)
        startActivity(intent)
    }

    private fun setHomeTopAppBar() {
        val inputManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        var lastInput = ""
//        val debounceJob: Job? = null
//        val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
//        binding.homeTopAppBar.topAppBar.inflateMenu(R.menu.home_app_bar_menu)
        binding.homeTopAppBar.homeSearchEditText.onFocusChangeListener = focusChangeListener
//        binding.homeTopAppBar.homeSearchEditText.doAfterTextChanged { editable ->
//            if (editable != null) {
//                val newtInput = editable.toString()
//                debounceJob?.cancel()
//                if (lastInput != newtInput) {
//                    lastInput = newtInput
//                    uiScope.launch {
//                        delay(500)
//                        if (lastInput == newtInput) {
//                            performSearch(newtInput)
//                        }
//                    }
//                }
//            }
//        }
        binding.homeTopAppBar.homeSearchEditText.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                textView.clearFocus()
                val inputManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(textView.windowToken, 0)
                performSearch(textView.text.toString())
                true
            } else {
                false
            }
        }
        binding.homeTopAppBar.searchOutlinedTextLayout.setEndIconOnClickListener {
            Log.d("SEARCH", "setEndIconOnClickListener")
            it.clearFocus()
            binding.homeTopAppBar.homeSearchEditText.setText("")
            val inputManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(it.windowToken, 0)
//			viewModel.filterProducts("All")
        }
    }

    private fun performSearch(searchWords: String) {
        viewModel.search(searchWords)
    }

    private fun setUpProductAdapter(productsList: List<Product>?) {
        listProductsAdapter = ListProductsAdapter(productsList ?: emptyList(), requireContext())
        listProductsAdapter.onClickListener =  object : OnClickListener {

            override fun onClickAdvancedActionsButton(view: View, productData: Product) {
                showPopup(view, productData)
            }
//
//            override fun onDeleteClick(productData: Product) {
//                Toast.makeText(requireContext(), "Delete", Toast.LENGTH_LONG).show()
////                Log.d(TAG, "onDeleteProduct: initiated for ${productData.productId}")
////                showDeleteDialog(productData.name, productData.productId)
//            }

            override fun onEditClick(productData: Product) {
                findNavController().navigate(
                    R.id.action_homeFragment_to_quickEditProductFragment,
                    bundleOf(
                        "product" to productData
                    )
                )

            }
        }
    }

    private fun showPopup(view: View, productData: Product) {
        val popup = PopupMenu(requireContext(), view).apply {
            setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.actionFullEdit -> {
                        editProduct(productData)
                        true
                    }
                    R.id.actionDelete -> {
                        deleteProduct(productData)
                        true
                    }
                    else -> {false}
                }
            }
            inflate(R.menu.advanced_action_product_menu)
            show()
        }
    }

    private fun editProduct(productData: Product) {
        findNavController().navigate(
            R.id.action_homeFragment_to_addEditProductFragment,
            bundleOf(
                "product" to productData
            ))
    }

    private fun deleteProduct(productData: Product) {
        showDeleteDialog(productData._id)
    }

    private fun showDeleteDialog(productId: String) {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(getString(R.string.delete_dialog_title_text))
                .setMessage(getString(R.string.delete_product_message_text))
                .setNeutralButton(getString(R.string.pro_cat_dialog_cancel_btn)) { dialog, _ ->
                    dialog.cancel()
                }
                .setPositiveButton(getString(R.string.delete_dialog_delete_btn_text)) { dialog, _ ->
                    viewModel.deleteProduct(productId)
                    dialog.cancel()
                }
                .show()
        }
    }

    private fun getMixedDataList(data: List<Product>, adsList: List<Int>): List<Any> {
        val itemsList = mutableListOf<Any>()
        itemsList.addAll(data.sortedBy { it._id })
        var currPos = 0
        if (itemsList.size >= 4) {
            adsList.forEach label@{ ad ->
                if (itemsList.size > currPos + 1) {
                    itemsList.add(currPos, ad)
                } else {
                    return@label
                }
                currPos += 5
            }
        }
        return itemsList
    }
    private fun getAdsList(): List<Int> {
        //feature: for now, ad is not supported
        return listOf()
    }

    override fun onStop() {
        super.onStop()
        viewModel.clearErrors()
    }

}