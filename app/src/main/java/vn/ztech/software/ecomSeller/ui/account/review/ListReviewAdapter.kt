package vn.ztech.software.ecomSeller.ui.account.review

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.ztech.software.ecomSeller.databinding.ItemReviewSellerBinding
import vn.ztech.software.ecomSeller.model.Review
import vn.ztech.software.ecomSeller.util.extension.toDateTimeString
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ListReviewAdapter(val context: Context,
                       val onClickListener: OnClickListener
) : PagingDataAdapter<Review, ListReviewAdapter.ViewHolder>(ReviewComparator) {

    inner class ViewHolder(private val binding: ItemReviewSellerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review) {
            binding.tvUserName.text = review.userName
            binding.tvProductName.text = review.productName
            binding.tvReviewContent.text = review.content
            binding.tvDateTime.text = review.updatedAt.toDateTimeString()

            if (review.imageUrl.isNotEmpty()) {
                val imgUrl = review.imageUrl.toUri().buildUpon().scheme("https").build()
                Glide.with(context)
                    .asBitmap()
                    .load(imgUrl)
                    .into(binding.ivProduct)
                binding.ivProduct.clipToOutline = true
            }
            binding.ratingBar.rating = review.rating.toFloat()

            binding.ivProduct.setOnClickListener {
                onClickListener.onClick(review.productId)
            }
            binding.tvProductName.setOnClickListener {
                onClickListener.onClick(review.productId)
            }
            binding.tvReviewContent.setOnClickListener {
                onClickListener.onClick(review.productId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemReviewSellerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!) //todo: should be changed back to ?.let
    }

    interface OnClickListener{
        fun onClick(productId: String)
    }

    object ReviewComparator: DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            // Id is unique.
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.content == newItem.content
        }
    }
}