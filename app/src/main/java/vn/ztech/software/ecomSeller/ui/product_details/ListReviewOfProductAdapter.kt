package vn.ztech.software.ecomSeller.ui.product_details

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import vn.ztech.software.ecomSeller.databinding.ItemPreviewReviewSellerBinding
import vn.ztech.software.ecomSeller.model.Review
import vn.ztech.software.ecomSeller.util.extension.toDateTimeString

class ListReviewOfProductAdapter(val context: Context) : PagingDataAdapter<Review, ListReviewOfProductAdapter.ViewHolder>(ReviewComparator) {

    inner class ViewHolder(private val binding: ItemPreviewReviewSellerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review) {
            binding.tvUserName.text = review.userName
            binding.tvReviewContent.text = review.content
            binding.tvDateTime.text = review.updatedAt.toDateTimeString()
            binding.ratingBar.rating = review.rating.toFloat()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPreviewReviewSellerBinding.inflate(
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