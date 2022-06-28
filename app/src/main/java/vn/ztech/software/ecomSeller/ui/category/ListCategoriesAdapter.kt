package vn.ztech.software.ecomSeller.ui.category

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.ztech.software.ecomSeller.databinding.ItemCategoryListBinding
import vn.ztech.software.ecomSeller.databinding.LayoutHomeAdBinding
import vn.ztech.software.ecomSeller.model.Category

class ListCategoriesAdapter(categoryList: List<Any>, private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data = categoryList

    lateinit var onClickListener: OnClickListener

    inner class ItemViewHolder(binding: ItemCategoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val categoryName = binding.categoryNameTv
        private val productCard = binding.categoryCard
        private val categoryImage = binding.categoryImageView
        private val btEdit = binding.btEdit
        private val btDelete = binding.btDelete
        private val tvNumberOfProduct = binding.tvNumberOfProduct

        fun bind(categoryData: Category) {
            btEdit.setOnClickListener {
                onClickListener.onClickEdit(categoryData)
            }
            btDelete.setOnClickListener {
                onClickListener.onClickDelete(categoryData)
            }
            categoryName.text = categoryData.name
            tvNumberOfProduct.text = "${categoryData.numberOfProduct} products"
            if (categoryData.imageUrl.isNotEmpty()) {
                val imgUrl = categoryData.imageUrl.toUri().buildUpon().scheme("https").build()
                Glide.with(context)
                    .asBitmap()
                    .load(imgUrl)
                    .into(categoryImage)
                categoryImage.clipToOutline = true
            }

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
                ItemCategoryListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val categoryData = data[position]) {
            is Int -> (holder as AdViewHolder).adImageView.setImageResource(categoryData)
            is Category -> (holder as ItemViewHolder).bind(categoryData)
        }
    }

    override fun getItemCount(): Int = data.size

    companion object {
        const val VIEW_TYPE_CATEGORY = 1
        const val VIEW_TYPE_AD = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is Int -> VIEW_TYPE_AD
            is Category -> VIEW_TYPE_CATEGORY
            else -> VIEW_TYPE_CATEGORY
        }
    }
    

    interface OnClickListener {
        fun onClickEdit(categoryData: Category)
        fun onClickDelete(categoryData: Category)

    }
}