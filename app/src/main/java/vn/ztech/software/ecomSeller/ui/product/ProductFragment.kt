package vn.ztech.software.ecomSeller.ui.product

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.common.StoreDataStatus
import vn.ztech.software.ecomSeller.databinding.FragmentProductBinding
import vn.ztech.software.ecomSeller.exception.RefreshTokenExpiredException
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.ui.MyOnFocusChangeListener
import vn.ztech.software.ecomSeller.ui.auth.LoginSignupActivity
import vn.ztech.software.ecomSeller.ui.splash.ISplashUseCase
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.extension.showErrorDialog

private const val TAG = "ProductFragment"


class ProductFragment : Fragment() {
    private lateinit var binding: FragmentProductBinding
    private val viewModel: ProductViewModel by sharedViewModel()
    private lateinit var listProductsAdapter: ListProductsAdapter
    protected val focusChangeListener = MyOnFocusChangeListener()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProductBinding.inflate(layoutInflater)
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
            setUpProductAdapter()
        }
        binding.homeFabAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_productFragment_to_addEditProductFragment)
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allProducts.observe(viewLifecycleOwner) { listProducts ->
                listProducts?.let {
                    binding.productsRecyclerView.adapter?.apply {
                        listProductsAdapter.submitData(lifecycle, listProducts)
                    }
                }
            }
        }
//        viewModel.allProducts.observe(viewLifecycleOwner) { listProducts->
//            listProducts?.let{
//                if (listProducts.isEmpty()) {
//                    binding.tvNoProductFound.visibility = View.VISIBLE
//                    binding.productsRecyclerView.visibility = View.GONE
//                }else {
//                    binding.tvNoProductFound.visibility = View.GONE
//                    binding.loaderLayout.circularLoader.hideAnimationBehavior
//                    binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
//                    binding.productsRecyclerView.visibility = View.VISIBLE
//                    binding.productsRecyclerView.adapter?.apply {
//                        listProductsAdapter.data =
//                            getMixedDataList(listProducts, getAdsList())
//                        notifyDataSetChanged()
//                    }
//                }
//            }
//            if (listProducts==null){
//                binding.loaderLayout.circularLoader.hideAnimationBehavior
//                binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
//                binding.productsRecyclerView.visibility = View.GONE
//                binding.tvNoProductFound.visibility = View.VISIBLE
//            }
//        }
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

    private fun setUpProductAdapter() {
        listProductsAdapter = ListProductsAdapter(requireContext())
        listProductsAdapter.onClickListener =  object : ListProductsAdapter.OnClickListener {

            override fun onClickAdvancedActionsButton(view: View, productData: Product) {
                showPopup(view, productData)
            }

            override fun onEditClick(productData: Product) {
                findNavController().navigate(
                    R.id.action_productFragment_to_quickEditProductFragment,
                    bundleOf(
                        "product" to productData
                    )
                )

            }
        }

        listProductsAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.productsRecyclerView.adapter = listProductsAdapter
        listProductsAdapter.addLoadStateListener {loadState->
            // show empty list
            if (loadState.refresh is androidx.paging.LoadState.Loading ||
                loadState.append is androidx.paging.LoadState.Loading){
                binding.loaderLayout.circularLoader.showAnimationBehavior
                binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
            }
            else {
                binding.loaderLayout.circularLoader.hideAnimationBehavior
                binding.loaderLayout.loaderFrameLayout.visibility = View.GONE

                if (listProductsAdapter.itemCount == 0){
                    binding.tvNoProductFound.visibility = View.VISIBLE
                    binding.productsRecyclerView.visibility = View.GONE
                }else{
                    binding.tvNoProductFound.visibility = View.GONE
                    binding.productsRecyclerView.visibility = View.VISIBLE
                }
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

    private fun showPopup(view: View, productData: Product) {
        val popup = PopupMenu(requireContext(), view).apply {
            setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.actionFullEdit -> {
                        editProduct(productData)
                        true
                    }
                    R.id.actionForkProduct -> {
                        forkProduct(productData)
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

    private fun forkProduct(productData: Product) {
        findNavController().navigate(
            R.id.action_productFragment_to_addEditProductFragment,
            bundleOf(
                "product" to productData,
                "editType" to "forkProduct"

            ))
    }

    private fun editProduct(productData: Product) {
        findNavController().navigate(
            R.id.action_productFragment_to_addEditProductFragment,
            bundleOf(
                "product" to productData,
                "editType" to "fullEdit"
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

    override fun onResume() {
        super.onResume()
        viewModel.clearSelected()
    }
    override fun onStop() {
        super.onStop()
        viewModel.clearErrors()
    }

}