package vn.ztech.software.ecomSeller.ui.order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.databinding.OrderListItemBinding
import vn.ztech.software.ecomSeller.model.OrderItem
import vn.ztech.software.ecomSeller.util.extension.toCurrency

class OrderProductsAdapter(
    private val context: Context, productsArg: List<OrderItem>,
) : RecyclerView.Adapter<OrderProductsAdapter.ViewHolder>() {
    var products: List<OrderItem> = productsArg

    inner class ViewHolder(private val binding: OrderListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: OrderItem) {
            binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
            binding.cartProductTitleTv.text = product.name
            binding.cartProductQuantityTv.text = "Quantity: x${product.quantity}"
            binding.cartProductPriceTv.text = product.price.toCurrency()
            if (product.imageUrl.isNotEmpty()) {
                val imgUrl = product.imageUrl.toUri().buildUpon().scheme("https").build()
                Glide.with(context)
                    .asBitmap()
                    .load(imgUrl)
                    .into(binding.productImageView)
                binding.productImageView.clipToOutline = true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            OrderListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size
}