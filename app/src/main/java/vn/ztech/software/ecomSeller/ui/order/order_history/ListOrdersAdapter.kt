package vn.ztech.software.ecomSeller.ui.order.order_history

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.common.Constants
import vn.ztech.software.ecomSeller.databinding.ItemOrderHistoryBinding
import vn.ztech.software.ecomSeller.model.Order
import vn.ztech.software.ecomSeller.util.extension.removeUnderline
import vn.ztech.software.ecomSeller.util.extension.toCurrency
import vn.ztech.software.ecomSeller.util.extension.toDateTimeString

class ListOrderAdapter( val context: Context,
                        val onClickListener: OnClickListener
) : PagingDataAdapter<Order, ListOrderAdapter.ViewHolder>(OrderComparator) {

    inner class ViewHolder(private val binding: ItemOrderHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.tvUserName.text = order.user.name
            binding.cartProductTitleTv.text = "${order.orderItems[0].name}..."
            binding.tvSubTotalAndShipping.text = "Subtotal: ${order.billing.subTotal.toCurrency()} + ${order.billing.shippingFee.toCurrency()}(ship)"
            binding.tvTotal.text = (order.billing.subTotal + order.billing.shippingFee).toCurrency()
            binding.tvOrderId.text = order.orderId
            if (order.orderItems[0].imageUrl.isNotEmpty()) {
                val imgUrl = order.orderItems[0].imageUrl.toUri().buildUpon().scheme("https").build()
                Glide.with(context)
                    .asBitmap()
                    .load(imgUrl)
                    .into(binding.productImageView)
                binding.productImageView.clipToOutline = true
            }
            setUIBaseOnOrderStatus(binding.tvOrderStatus, order.status)

            if (order.status == "CONFIRMED"){
                /**only show expected delivery time if the order has been CONFIRMED*/
                binding.tvExpectedDeliveryTime.apply {
                    order.shippingDetails?.let {
                        if(!it.expectedDeliveryTime.isNullOrEmpty()){
                            visibility = View.VISIBLE
                            text = "Expected delivery time: ${it.expectedDeliveryTime?.toDateTimeString()}"
                        }
                    }
                }
                if(order.shippingDetails?.status != "none") {
                    binding.tvShippingStatus.visibility = View.VISIBLE
                    if (!order.shippingDetails?.log.isNullOrEmpty())
                        binding.tvShippingStatus.text = "Shipping status: ${order.shippingDetails?.log?.last()?.status?.removeUnderline()}"
                    else
                        binding.tvShippingStatus.text = "Shipping status: ${order.shippingDetails?.status?.removeUnderline()}"
                }
            }else{
                binding.btViewDetails.text = "View details"
                binding.btViewDetails.setBackgroundColor(context.resources.getColor(R.color.blue_accent_300))

            }

            binding.btViewDetails.text = getAppropriateActionLabel(order.status)
            binding.productCard.setOnClickListener {
                onClickListener.onClick(order)
            }
            binding.tvOrderId.setOnClickListener {
                onClickListener.onCopyClipBoardClicked(order.orderId)
            }
            binding.btViewDetails.setOnClickListener {
                onClickListener.onClickButtonViewDetail(order)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemOrderHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!) //todo: should be changed back to ?.let
    }

//    override fun getItemCount() = orders.size
    private fun setUIBaseOnOrderStatus(tv: TextView, status: String){
        tv.apply {
            text = status
            when(status){
                "PENDING"->{
                    setTextColor(Color.BLUE)
                    background = resources.getDrawable(R.drawable.rounded_bg_blue)
                }
                "CANCELED"->{
                    setTextColor(Color.RED)
                    background = resources.getDrawable(R.drawable.rounded_bg_red)
                }
                "PROCESSING"->{
                    setTextColor(resources.getColor(R.color.dark_yellow))
                    background = resources.getDrawable(R.drawable.rounded_bg_yellow)
                }
                "CONFIRMED"->{
                    setTextColor(Color.GREEN)
                    background = resources.getDrawable(R.drawable.rounded_bg_green)
                }
                "RECEIVED"->{
                    setTextColor(Color.GREEN)
                    background = resources.getDrawable(R.drawable.rounded_bg_green)
                }
            }
        }
    }
    private fun getAppropriateActionLabel(statusFilter: String): String{
        return Constants.StatusFilterToAction[statusFilter]?:"View Details"
    }
    interface OnClickListener{
        fun onClick(order: Order)
        fun onClickButtonViewDetail(order: Order)
        fun onCopyClipBoardClicked(orderId: String) {
        }
    }

    object OrderComparator: DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            // Id is unique.
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.status == newItem.status
        }
    }
}