package vn.ztech.software.ecomSeller.ui.category

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.common.StoreDataStatus
import vn.ztech.software.ecomSeller.databinding.FragmentCategoryBinding
import vn.ztech.software.ecomSeller.model.Category
import vn.ztech.software.ecomSeller.ui.BaseFragment
import vn.ztech.software.ecomSeller.ui.common.ItemDecorationRecyclerViewPadding

class CategoryFragment : BaseFragment<FragmentCategoryBinding>() {
    override fun setViewBinding(): FragmentCategoryBinding {
        return FragmentCategoryBinding.inflate(layoutInflater)
    }
    
    private val viewModel: CategoryViewModel by viewModel()
    private lateinit var listCategoriesAdapter: ListCategoriesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(viewModel.originalCategories.value.isNullOrEmpty()) {
            viewModel.getCategories()
        }
    }
    override fun setUpViews() {
        super.setUpViews()
        if (context != null) {
            setUpCategoryAdapter(viewModel.allCategories.value)
            binding.categoriesRecyclerView.apply {
                adapter = listCategoriesAdapter
                val itemDecoration = ItemDecorationRecyclerViewPadding()
                if (itemDecorationCount == 0) {
                    addItemDecoration(itemDecoration)
                }
            }
        }
        binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
        binding.loaderLayout.circularLoader.showAnimationBehavior

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getCategories()
        }

        binding.btAddEditCategory.setOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_addEditCategoryFragment)
        }
    }
//
    @SuppressLint("NotifyDataSetChanged")
    override fun observeView() {
        super.observeView()
        setTopAppBar()
        viewModel.storeDataStatus.observe(viewLifecycleOwner) { status ->
            if(status == StoreDataStatus.LOADING) {
                binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
                binding.loaderLayout.circularLoader.showAnimationBehavior
                binding.categoriesRecyclerView.visibility = View.GONE
            }else{
                binding.loaderLayout.circularLoader.hideAnimationBehavior
                binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
            }
        }
        viewModel.allCategories.observe(viewLifecycleOwner) { listCategories->
            if (!listCategories.isNullOrEmpty()) {
                binding.loaderLayout.circularLoader.hideAnimationBehavior
                binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                binding.categoriesRecyclerView.visibility = View.VISIBLE
                binding.categoriesRecyclerView.adapter?.apply {
                    listCategoriesAdapter.data = listCategories
                    notifyDataSetChanged()
                }
            }
        }
        viewModel.isSearchCategoriesResultEmpty.observe(viewLifecycleOwner){
                it?.let {
                    if (it)
                        Toast.makeText(requireContext(), "No category found", Toast.LENGTH_LONG)
                        .apply { setGravity(Gravity.CENTER, 0, 0) }
                        .show()
                }
        }
        viewModel.error.observe(viewLifecycleOwner){
            it ?: return@observe
            handleError(it)
        }
    }

    private fun setTopAppBar() {
        binding.categoryTopAppBar.homeSearchEditText.onFocusChangeListener = focusChangeListener
        binding.categoryTopAppBar.homeSearchEditText.setOnEditorActionListener { textView, actionId, _ ->
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
        binding.categoryTopAppBar.searchOutlinedTextLayout.setEndIconOnClickListener {
            Log.d("SEARCH", "setEndIconOnClickListener")
            it.clearFocus()
            binding.categoryTopAppBar.homeSearchEditText.setText("")
            val inputManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(it.windowToken, 0)
//			viewModel.filterProducts("All")
        }
    }

    private fun performSearch(searchWords: String) {
        viewModel.searchCategoriesLocal(searchWords)
    }
    

    private fun setUpCategoryAdapter(categoriesList: List<Category>?) {
        listCategoriesAdapter = ListCategoriesAdapter(categoriesList ?: emptyList(), requireContext())
        listCategoriesAdapter.onClickListener =  object : ListCategoriesAdapter.OnClickListener {

            override fun onClickEdit(categoryData: Category) {
                Toast.makeText(requireContext(), "Edit", Toast.LENGTH_LONG).show()
            }

            override fun onClickDelete(categoryData: Category) {
                Toast.makeText(requireContext(), "Delete", Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun onStop() {
        super.onStop()
        viewModel.clearErrors()
    }
}