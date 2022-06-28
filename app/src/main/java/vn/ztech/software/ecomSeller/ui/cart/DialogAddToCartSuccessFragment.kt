package vn.ztech.software.ecomSeller.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import vn.ztech.software.ecomSeller.databinding.FragmentDialogAddToCartSuccessBinding
import vn.ztech.software.ecomSeller.model.Product

class DialogAddToCartSuccessFragment(val product: Product, val onClickListener: OnClick) : BottomSheetDialogFragment() {
    interface OnClick{
        fun onButtonGoToCartClicked()
    }
    private lateinit var binding: FragmentDialogAddToCartSuccessBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogAddToCartSuccessBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvName.text = product.name
        binding.tvPrice.text = product.price.toString()
        val imgUrl = product.imageUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(requireContext())
            .asBitmap()
            .load(imgUrl)
            .into(binding.ivImage)
        binding.ivImage.clipToOutline = true
        binding.btGoToCart.setOnClickListener {
            onClickListener.onButtonGoToCartClicked()
            dismiss()
        }
    }
}