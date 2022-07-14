package vn.ztech.software.ecomSeller.ui.product_details

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.databinding.FragmentAllReviewOfProductBinding
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.ui.BaseFragment2
import vn.ztech.software.ecomSeller.util.CustomError

import vn.ztech.software.ecomSeller.util.extension.getFirstNumber


class AllReviewOfProductFragment : BaseFragment2<FragmentAllReviewOfProductBinding>() {
    private val viewModel: ListReviewOfProductViewModel by viewModel()
    lateinit var adapter: ListReviewOfProductAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.takeIf { it.containsKey("product") }?.let { it ->
            viewModel.product.value = it.getParcelable<Product>("product")
            viewModel.product.value?.let {
                viewModel.getReviewsOfProduct(productId = it._id, null)
            }
        }
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.topAppBarListReview.topAppBar.title = viewModel.product.value?.name?:"List reviews"
        binding.topAppBarListReview.topAppBar.setNavigationOnClickListener{
            findNavController().navigateUp()
        }


        val imageUrl = viewModel.product.value?.imageUrl?:""
        if (imageUrl.isNotEmpty()) {
            val imgUrl = imageUrl.toUri().buildUpon().scheme("https").build()
            Glide.with(requireContext())
                .asBitmap()
                .load(imgUrl)
                .into(binding.ivProductImage)
            binding.ivProductImage.clipToOutline = true
        }


        binding.starFilters.chipGroup.setOnCheckedStateChangeListener(object: ChipGroup.OnCheckedStateChangeListener{
            override fun onCheckedChanged(group: ChipGroup, checkedIds: MutableList<Int>) {
                if(checkedIds.size>0){
                    val selectedStarFilter = group.findViewById<Chip>(checkedIds[0]).text.toString().getFirstNumber()
                    viewModel.getReviewsOfProduct(viewModel.product.value?._id, selectedStarFilter)
                }
            }

        })
        setupAdapter()
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.reviews.observe(viewLifecycleOwner){
                it?.let {
                    binding.listReviews.adapter?.apply {
                        adapter.submitData(viewLifecycleOwner.lifecycle,it)
                    }
                }
            }
        }
        viewModel.error.observe(viewLifecycleOwner){
            it?.let {
                binding.loaderLayout.circularLoader.hideAnimationBehavior
                binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                handleError(it)
            }
        }
    }

    private fun setupAdapter() {
        adapter = ListReviewOfProductAdapter(requireContext(), object : ListReviewOfProductAdapter.OnClickListener{
            override fun onClick(productId: String) {
                findNavController().navigate(
                    R.id.action_listReviewFragment_to_productDetailsFragment,
                    bundleOf(
                        "productId" to productId
                    )
                )
            }
        })
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.listReviews.adapter = adapter
        adapter.addLoadStateListener {loadState->
            // show empty list
            if (loadState.refresh is androidx.paging.LoadState.Loading ||
                loadState.append is androidx.paging.LoadState.Loading){
                binding.loaderLayout.circularLoader.showAnimationBehavior
                binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
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

    override fun setViewBinding(): FragmentAllReviewOfProductBinding {
        return FragmentAllReviewOfProductBinding.inflate(layoutInflater)
    }
}