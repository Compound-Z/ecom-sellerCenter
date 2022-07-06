package vn.ztech.software.ecomSeller.ui.category

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.ecomSeller.common.StoreDataStatus
import vn.ztech.software.ecomSeller.databinding.FragmentListProductsInCategoryBinding
import vn.ztech.software.ecomSeller.exception.RefreshTokenExpiredException
import vn.ztech.software.ecomSeller.model.Category
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.ui.MyOnFocusChangeListener
import vn.ztech.software.ecomSeller.ui.auth.LoginSignupActivity
import vn.ztech.software.ecomSeller.ui.common.ItemDecorationRecyclerViewPadding
import vn.ztech.software.ecomSeller.ui.product.ListProductsAdapter
import vn.ztech.software.ecomSeller.ui.splash.ISplashUseCase
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.extension.showErrorDialog

open class ListProductsInCategoryFragment : Fragment() {
    private lateinit var binding: FragmentListProductsInCategoryBinding
    private val viewModel: CategoryViewModel by viewModel()
    private lateinit var listProductsAdapter: ListProductsAdapter
    protected val focusChangeListener = MyOnFocusChangeListener()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentListProductsInCategoryBinding.inflate(layoutInflater)
        setViews()
        setObservers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val category = arguments?.getParcelable("category") as Category?
        Log.d("LIST", category.toString())
        viewModel.currentSelectedCategory.value = category

        viewModel.getProductsInCategory()
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getProductsInCategory()
        }
    }

    private fun setViews() {
        setHomeTopAppBar()
        if (context != null) {
            setUpProductAdapter()
            binding.productsRecyclerView.apply {
                val gridLayoutManager = GridLayoutManager(context, 2)
                gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (listProductsAdapter.getItemViewType(position)) {
                            2 -> 2 //ad
                            else -> {
                               1
                            }
                        }
                    }
                }
                layoutManager = gridLayoutManager
                adapter = listProductsAdapter
                val itemDecoration = ItemDecorationRecyclerViewPadding()
                if (itemDecorationCount == 0) {
                    addItemDecoration(itemDecoration)
                }
            }
        }
        //feature: this will be add when the app supports seller.
//        if (!viewModel.isUserASeller) {
//            binding.homeFabAddProduct.visibility = View.GONE
//        }
//        binding.homeFabAddProduct.setOnClickListener {
//            showDialogWithItems(ProductCategories, 0, false)
//        }
        binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
        binding.loaderLayout.circularLoader.showAnimationBehavior
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setObservers() {
        viewModel.storeDataStatus.observe(viewLifecycleOwner) { status ->
            if(status == StoreDataStatus.LOADING) {
                binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
                binding.loaderLayout.circularLoader.showAnimationBehavior
                binding.productsRecyclerView.visibility = View.GONE
            }else{
                binding.loaderLayout.circularLoader.hideAnimationBehavior
                binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
            }
        }
        viewModel.error.observe(viewLifecycleOwner) {
            it?.let {
                Log.d("ERROR:","viewModel.searchError: $it")
                showErrorDialog(it)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.products.observe(viewLifecycleOwner) { products ->
                products?.let {
                    binding.productsRecyclerView.adapter?.apply {
                        listProductsAdapter.submitData(lifecycle, products)
                    }
                }
            }
        }
//        viewModel.products.observe(viewLifecycleOwner) { listProducts->
//            if (listProducts.isNotEmpty()) {
//                binding.tvNoProductFound.visibility = View.GONE
//                binding.loaderLayout.circularLoader.hideAnimationBehavior
//                binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
//                binding.productsRecyclerView.visibility = View.VISIBLE
//                binding.productsRecyclerView.adapter?.apply {
//                    listProductsAdapter.data =
//                        getMixedDataList(listProducts, getAdsList())
//                    notifyDataSetChanged()
//                }
//            }else{
//                binding.tvNoProductFound.visibility = View.VISIBLE
//            }
//        }
    }

    private fun setHomeTopAppBar() {
        var lastInput = ""
        binding.homeTopAppBar.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
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
                val inputManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
//        binding.homeTopAppBar.topAppBar.setOnMenuItemClickListener { menuItem ->
//            setAppBarItemClicks(menuItem)
//        }
    }

    private fun performSearch(searchWordsProduct: String) {
        viewModel.searchProductsInCategory(searchWordsProduct)
    }

    private fun setAppBarItemClicks(menuItem: MenuItem): Boolean {
        return true //this line should be deleted when the codes below is uncommented
//        return when (menuItem.itemId) {
//            R.id.home_filter -> {
//                val extraFilters = arrayOf("All", "None")
//                val categoryList = ProductCategories.plus(extraFilters)
//                val checkedItem = categoryList.indexOf(viewModel.filterCategory.value)
//                showDialogWithItems(categoryList, checkedItem, true)
//                true
//            }
//            else -> false
//        }
    }

    private fun setUpProductAdapter() {
        listProductsAdapter = ListProductsAdapter(requireContext())
        listProductsAdapter.onClickListener =  object : ListProductsAdapter.OnClickListener {

            override fun onClickAdvancedActionsButton(view: View, productData: Product) {
                TODO("Not yet implemented")
            }

//            override fun onDeleteClick(productData: Product) {
////                Log.d(TAG, "onDeleteProduct: initiated for ${productData.productId}")
////                showDeleteDialog(productData.name, productData.productId)
//            }

            override fun onEditClick(productData: Product) {
//                Log.d(TAG, "onEditProduct: initiated for $productId")
//                navigateToAddEditProductFragment(isEdit = true, productId = productId)
            }

        }

        listProductsAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.productsRecyclerView.adapter = listProductsAdapter
        listProductsAdapter.addLoadStateListener {loadState->
            // show empty list
            if (loadState.refresh is androidx.paging.LoadState.Loading ||
                loadState.append is androidx.paging.LoadState.Loading){
                binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
                binding.loaderLayout.circularLoader.showAnimationBehavior
            }
            else {
                binding.loaderLayout.circularLoader.hideAnimationBehavior
                binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                // If we have an error, show a toast
                val errorState = when {
                    loadState.append is androidx.paging.LoadState.Error -> loadState.append as androidx.paging.LoadState.Error
                    loadState.prepend is androidx.paging.LoadState.Error ->  loadState.prepend as androidx.paging.LoadState.Error
                    loadState.refresh is androidx.paging.LoadState.Error -> loadState.refresh as androidx.paging.LoadState.Error
                    else -> null
                }
                errorState?.let {
                    handleError(CustomError(it.error, it.error.message?:"System error"))
                }
            }
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