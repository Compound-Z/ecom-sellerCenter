package vn.ztech.software.ecomSeller.ui.address

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.databinding.LayoutAddressCardBinding
import vn.ztech.software.ecomSeller.model.AddressItem
import vn.ztech.software.ecomSeller.util.extension.getFullAddress

private const val TAG = "AddressAdapter"

class AddressAdapter(
	private val context: Context,
	addressItems: List<AddressItem>,
	defaultAddressId: String,
	private val isSelect: Boolean
) :
	RecyclerView.Adapter<AddressAdapter.ViewHolder>() {

	lateinit var onClickListener: OnClickListener
	var data: List<AddressItem> = addressItems
	var defaultAddressId = defaultAddressId
	var lastCheckedAddressItem: AddressItem? = null
	private var lastCheckedCard: MaterialCardView? = null
	var selectedAddressPos = -1

	inner class ViewHolder(private var binding: LayoutAddressCardBinding) :
		RecyclerView.ViewHolder(binding.root) {
		fun bind(address: AddressItem, position: Int) {
			binding.addressCard.isChecked = position == selectedAddressPos
			binding.addressPersonNameTv.text =
				context.getString(R.string.person_name, address.receiverName)
			binding.addressCompleteAddressTv.text = address.getFullAddress()
			binding.addressMobileTv.text = address.receiverPhoneNumber
			if (isSelect) {
				binding.addressCard.setOnClickListener {
					onCardClick(position, address, it as MaterialCardView)
				}
			}
			if (defaultAddressId == address._id){
				binding.ivFlag.visibility = View.VISIBLE
				binding.tvDefaultAddressLabel.visibility = View.VISIBLE
			}else{
				binding.ivFlag.visibility = View.GONE
				binding.tvDefaultAddressLabel.visibility = View.GONE
			}
			binding.addressEditBtn.setOnClickListener {
				onClickListener.onEditClick(address)
			}
			binding.addressDeleteBtn.setOnClickListener {
				onClickListener.onDeleteClick(address._id)
				notifyDataSetChanged()
			}
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(
			LayoutAddressCardBinding.inflate(
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
		fun onEditClick(addressItem: AddressItem)
		fun onDeleteClick(addressId: String)
		fun onClick()
	}
	
	//todo: this may lead to some bugs when the list address update and change order of the data....
	private fun onCardClick(position: Int, addressItem: AddressItem, card: MaterialCardView) {
		if (addressItem._id != lastCheckedAddressItem?._id) {
			card.apply {
				strokeColor = context.getColor(R.color.blue_accent_300)
				isChecked = true
				strokeWidth = TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP,
					2F,
					resources.displayMetrics
				).toInt()
			}
			lastCheckedCard?.apply {
				strokeColor = context.getColor(R.color.light_gray)
				isChecked = false
				strokeWidth = TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP,
					1F,
					resources.displayMetrics
				).toInt()
			}

			lastCheckedAddressItem = addressItem
			lastCheckedCard = card
			selectedAddressPos = position
			Log.d(TAG, "onCardClick: selected address = ${addressItem._id}")
		}

		onClickListener.onClick()
	}
}