package vn.ztech.software.ecomSeller.ui.order.order

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import vn.ztech.software.ecomSeller.api.response.ShippingOption
import vn.ztech.software.ecomSeller.databinding.ItemShippingOptionBinding

class ShippingOptionsAdapter(
    private val context: Context,
    shippingOptions: List<ShippingOption>,
) :
    RecyclerView.Adapter<ShippingOptionsAdapter.ViewHolder>() {

    lateinit var onClickListener: OnClickListener
    var data: List<ShippingOption> = shippingOptions
    var lastCheckedShippingOption: ShippingOption? = null
    private var lastCheckedRadioButton: RadioButton? = null
    var selectedShippingOptionPos = -1

    inner class ViewHolder(private var binding: ItemShippingOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(shippingOption: ShippingOption, position: Int) {
            binding.radioButton.isChecked = position == selectedShippingOptionPos
            binding.tvName.text = shippingOption.name
            binding.tvTotalShippingFee.text = "${shippingOption.fee.total}đ"
            binding.tvServiceShippingFee.text = "${shippingOption.fee.service_fee}đ"
            binding.tvInsuranceShippingFee.text = "${shippingOption.fee.insurance_fee}đ"
            binding.layout.setOnClickListener {
                onCardClick(position, shippingOption, binding.radioButton)
                onClickListener.onClick(shippingOption)
            }
            binding.radioButton.setOnClickListener {
                onCardClick(position, shippingOption, binding.radioButton)
                onClickListener.onClick(shippingOption)
            }

            //choose the first shipping option as default
            if (position==0){
                binding.layout.performClick()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemShippingOptionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position], position)
    }

    override fun getItemCount(): Int = data.size

    interface OnClickListener {
        fun onClick(shippingOption: ShippingOption)
    }

    //todo: this may lead to some bugs when the list address update and change order of the data....
    private fun onCardClick(position: Int, shippingOption: ShippingOption, radioButton: RadioButton) {
        if (shippingOption.service_id != lastCheckedShippingOption?.service_id) {
           radioButton.isChecked = true
            lastCheckedRadioButton?.isChecked = false
            lastCheckedShippingOption = shippingOption
            lastCheckedRadioButton = radioButton
            selectedShippingOptionPos = position
        }
    }

}