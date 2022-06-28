package vn.ztech.software.ecomSeller.ui.order.order

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.databinding.FragmentOrderSuccessBinding
import vn.ztech.software.ecomSeller.model.OrderDetails
import vn.ztech.software.ecomSeller.ui.BaseFragment

class OrderSuccessFragment : BaseFragment<FragmentOrderSuccessBinding>() {
    var orderDetails: OrderDetails? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderDetails = arguments?.getParcelable<OrderDetails>("orderDetails")
        if (orderDetails==null){
            findNavController().navigate(R.id.action_orderSuccessFragment_to_homeFragment)
        }
    }
    override fun setUpViews() {
        super.setUpViews()
        binding.backToHomeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_orderSuccessFragment_to_homeFragment)
        }
        binding.tvViewOrder.setOnClickListener {
            findNavController().navigate(R.id.action_orderSuccessFragment_to_orderDetailsFragment,
            bundleOf("orderDetails" to orderDetails,"fromWhere" to "OrderSuccessFragment"))
        }
    }

    override fun setViewBinding(): FragmentOrderSuccessBinding {
        return FragmentOrderSuccessBinding.inflate(layoutInflater)
    }
}