package vn.ztech.software.ecomSeller.ui.account.info

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.ecomSeller.databinding.FragmentShopInfoBinding
import vn.ztech.software.ecomSeller.ui.BaseFragment2
import vn.ztech.software.ecomSeller.util.extension.getFullAddress
import vn.ztech.software.ecomSeller.util.extension.removeUnderline
import vn.ztech.software.ecomSeller.util.extension.toYear


class ShopInfoFragment : BaseFragment2<FragmentShopInfoBinding>() {
    val viewModel: ShopInfoViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.shop.value == null){
            viewModel.getShopInfo()
        }else{
            viewModel.getShopInfo(false)
        }
    }
    override fun setViewBinding(): FragmentShopInfoBinding {
        return FragmentShopInfoBinding.inflate(layoutInflater)
    }

    override fun setUpViews() {
        super.setUpViews()
        viewModel.shop.value?.let {
            updateUI()
        }
    }

    private fun updateUI() {
        if (viewModel.shop.value?.imageUrl?.isNotEmpty() == true) {
            val imgUrl = viewModel.shop.value?.imageUrl?.toUri()?.buildUpon()?.scheme("https")?.build()
            Glide.with(requireContext())
                .asBitmap()
                .load(imgUrl)
                .into(binding.ivShop)
        }
        binding.topAppBar.topAppBar.title = viewModel.shop.value?.name?.removeUnderline()
        binding.topAppBar.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.tvPhoneNumberContent.text = viewModel.shop.value?.userId?.phoneNumber?:""
        binding.tvFromWhenContent.text = viewModel.shop.value?.createdAt?.toYear()
        binding.tvAddressContent.text = viewModel.shop.value?.addressItem?.getFullAddress()
        binding.tvNumberOfProductContent.text = viewModel.shop.value?.numberOfProduct?.toString()
        binding.tvDescriptionContent.text = viewModel.shop.value?.description
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
        viewModel.shop.observe(viewLifecycleOwner){
            it?.let { updateUI() }
        }
        viewModel.error.observe(viewLifecycleOwner){
            it?.let {
                binding.loaderLayout.circularLoader.hideAnimationBehavior
                binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                handleError(it)
            }
        }
    }

}