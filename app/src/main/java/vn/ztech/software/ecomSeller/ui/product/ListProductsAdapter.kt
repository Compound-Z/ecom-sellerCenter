package vn.ztech.software.ecomSeller.ui.product

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

//import com.vishalgaur.shoppingapp.data.ShoppingAppSessionManager

import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.databinding.ItemProductListBinding
import vn.ztech.software.ecomSeller.databinding.LayoutHomeAdBinding
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.util.extension.toCurrency

class ListProductsAdapter(private val context: Context) :
	PagingDataAdapter<Product, RecyclerView.ViewHolder>(ProductComparator) {


	lateinit var onClickListener: OnClickListener

	inner class ItemViewHolder(binding: ItemProductListBinding) :
		RecyclerView.ViewHolder(binding.root) {
		private val proName = binding.tvProductName
		private val proPrice = binding.tvProductPrice
		private val productCard = binding.productCard
		private val productImage = binding.productImageView
		private val proEditBtn = binding.btEdit
		private val tvStockNumber = binding.tvStockNumber
		private val tvSoldNumber = binding.tvSoldNumber
		private val btAdvancedActions = binding.btAdvancedActions
//		private val proRatingBar = binding.productRatingBar

		fun bind(productData: Product) {
			proName.text = productData.name
			proPrice.text = productData.price.toCurrency()
//			proRatingBar.rating = productData.averageRating.toFloat()

			if (productData.imageUrl.isNotEmpty()) {
				val imgUrl = productData.imageUrl.toUri().buildUpon().scheme("https").build()
				Glide.with(context)
					.asBitmap()
					.load(imgUrl)
					.into(productImage)

				productImage.clipToOutline = true
			}
			tvStockNumber.text = productData.stockNumber.toString()
			tvSoldNumber.text = productData.saleNumber.toString()

			proEditBtn.visibility = View.VISIBLE
			proEditBtn.setOnClickListener {
				onClickListener.onEditClick(productData)
			}

			btAdvancedActions.setOnClickListener {
				onClickListener.onClickAdvancedActionsButton(it, productData)
			}

//			}
		}
	}

	inner class AdViewHolder(binding: LayoutHomeAdBinding) : RecyclerView.ViewHolder(binding.root) {
		val adImageView: ImageView = binding.adImageView
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return when (viewType) {
			VIEW_TYPE_AD -> AdViewHolder(
				LayoutHomeAdBinding.inflate(
					LayoutInflater.from(parent.context),
					parent,
					false
				)
			)
			else -> ItemViewHolder(
				ItemProductListBinding.inflate(
					LayoutInflater.from(parent.context),
					parent,
					false
				)
			)
		}
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		when (val proData = getItem(position)) {
//			is Int -> (holder as AdViewHolder).adImageView.setImageResource(proData)
			is Product -> (holder as ItemViewHolder).bind(proData)
		}
	}


	companion object {
		const val VIEW_TYPE_PRODUCT = 1
		const val VIEW_TYPE_AD = 2
	}

	override fun getItemViewType(position: Int): Int {
		return when (getItem(position)) {
//			is Int -> VIEW_TYPE_AD
			is Product -> VIEW_TYPE_PRODUCT
			else -> VIEW_TYPE_PRODUCT
		}
	}

	interface OnClickListener {
		fun onClickAdvancedActionsButton(view: View, productData: Product)
		fun onEditClick(productData: Product)
	}
	object ProductComparator: DiffUtil.ItemCallback<Product>() {
		override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
			// Id is unique.
			return oldItem._id == newItem._id
		}

		override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
			return oldItem.name == newItem.name
		}
	}
}