package vn.ztech.software.ecomSeller.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

//import com.vishalgaur.shoppingapp.data.ShoppingAppSessionManager

import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.databinding.ItemProductListBinding
import vn.ztech.software.ecomSeller.databinding.LayoutHomeAdBinding
import vn.ztech.software.ecomSeller.model.Product

class ListProductsAdapter(proList: List<Any>, private val context: Context) :
	RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	var data = proList

	lateinit var onClickListener: OnClickListener
	lateinit var bindImageButtons: BindImageButtons
//	private val sessionManager = ShoppingAppSessionManager(context)

	inner class ItemViewHolder(binding: ItemProductListBinding) :
		RecyclerView.ViewHolder(binding.root) {
		private val proName = binding.productNameTv
		private val proPrice = binding.productPriceTv
		private val productCard = binding.productCard
		private val productImage = binding.productImageView
		private val proDeleteButton = binding.productDeleteButton
		private val proEditBtn = binding.productEditButton
		private val proMrp = binding.productActualPriceTv
		private val proOffer = binding.productOfferValueTv
		private val proRatingBar = binding.productRatingBar
		private val proLikeButton = binding.productLikeCheckbox
		private val proCartButton = binding.productAddToCartButton

		fun bind(productData: Product) {
			productCard.setOnClickListener {
				onClickListener.onClick(productData)
			}
			proName.text = productData.name
			proPrice.text =
				context.getString(R.string.pro_details_price_value, productData.price.toString())
			proRatingBar.rating = productData.averageRating.toFloat()

			if (productData.imageUrl.isNotEmpty()) {
				val imgUrl = productData.imageUrl.toUri().buildUpon().scheme("https").build()
				Glide.with(context)
					.asBitmap()
					.load(imgUrl)
					.into(productImage)

				productImage.clipToOutline = true
			}

//feature: this should be uncommented when the app supports seller
//			if (sessionManager.isUserSeller()) {
//				proLikeButton.visibility = View.GONE
//				proCartButton.visibility = View.GONE
//				proEditBtn.setOnClickListener {
//					onClickListener.onEditClick(productData.productId)
//				}
//
//				proDeleteButton.setOnClickListener {
//					onClickListener.onDeleteClick(productData)
//				}
//			} else {
				proEditBtn.visibility = View.GONE
				proDeleteButton.visibility = View.GONE
				bindImageButtons.setCartButton(productData._id, proCartButton)
				proLikeButton.setOnCheckedChangeListener { _, _ ->


				}
				proCartButton.setOnClickListener {
					onClickListener.onAddToCartClick(productData)
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
		when (val proData = data[position]) {
			is Int -> (holder as AdViewHolder).adImageView.setImageResource(proData)
			is Product -> (holder as ItemViewHolder).bind(proData)
		}
	}

	override fun getItemCount(): Int = data.size

	companion object {
		const val VIEW_TYPE_PRODUCT = 1
		const val VIEW_TYPE_AD = 2
	}

	override fun getItemViewType(position: Int): Int {
		return when (data[position]) {
			is Int -> VIEW_TYPE_AD
			is Product -> VIEW_TYPE_PRODUCT
			else -> VIEW_TYPE_PRODUCT
		}
	}

	interface BindImageButtons {
		fun setCartButton(productId: String, imgView: ImageView)
	}

	interface OnClickListener {
		fun onClick(productData: Product)
		fun onDeleteClick(productData: Product)
		fun onEditClick(productId: String) {}
		fun onLikeClick(productId: String) {}
		fun onAddToCartClick(productData: Product) {}
	}
}