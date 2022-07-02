package vn.ztech.software.ecomSeller.ui.order.order_history

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.item_order_history.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.ztech.software.ecomSeller.databinding.FragmentListOrderBinding
import vn.ztech.software.ecomSeller.ui.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.common.Constants
import vn.ztech.software.ecomSeller.model.Order
import vn.ztech.software.ecomSeller.ui.BaseFragment2

const val TAG = "ListOrdersFragment"
class ListOrdersFragment() : BaseFragment2<FragmentListOrderBinding>() {
    private val viewModel: ListOrdersViewModel by viewModel()
    lateinit var statusFilter: String
    lateinit var adapter: ListOrderAdapter
    interface OnClickListener{
        fun onClickViewDetails(orderId: String)
    }
    private lateinit var callBack: OnClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.takeIf { it.containsKey("statusFilter") }?.apply {
            Log.d(TAG, getString("statusFilter").toString())
            statusFilter = getString("statusFilter").toString()
            viewModel.statusFilter.value = statusFilter
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getOrders(viewModel.statusFilter.value)
    }

    override fun setViewBinding(): FragmentListOrderBinding {
        return FragmentListOrderBinding.inflate(layoutInflater)
    }

    override fun setUpViews() {
        super.setUpViews()
        setupAdapter(viewModel.orders.value)
    }
    private fun setupAdapter(_orders: List<Order>?){
        val orders = _orders?: emptyList()
        adapter = ListOrderAdapter(requireContext(), orders, object : ListOrderAdapter.OnClickListener{
            override fun onClick(order: Order) {
                callBack.onClickViewDetails(order._id)
            }

            override fun onCopyClipBoardClicked(orderId: String) {
                toastCenter("Copied: ${orderId}")
            }
            override fun onClickButtonViewDetail(order: Order) {
                handleAction(order)
            }
        })
        binding.listOrders.adapter = adapter
    }

    private fun handleAction(order: Order) {
        viewModel.currentSelectedOrder.value = order
        when(order.status){
            "PENDING" -> {
                Constants.StatusFilterToAction["PENDING"]?.let{
                    showDialog(it) { p0, _ ->
                        p0.dismiss()
                        viewModel.startProcessing(order._id)
                    }
                }
            }
            "PROCESSING" -> {
                Constants.StatusFilterToAction["PROCESSING"]?.let{
                    showDialog(it){ p0, _ ->
                        p0.dismiss()
                        viewModel.confirm(order._id)
                    }
                }
            }
            "", "CANCELED", "CONFIRMED", "RECEIVED"-> {
                callBack.onClickViewDetails(order._id)
            }
        }
    }


    override fun observeView() {
        super.observeView()
        viewModel.loading.observe(viewLifecycleOwner){
            when (it) {
                true -> {
                    binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
                    binding.loaderLayout.circularLoader.showAnimationBehavior
                }
                false -> {
                    binding.loaderLayout.circularLoader.hideAnimationBehavior
                    binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                }
            }
        }
        viewModel.orders.observe(viewLifecycleOwner){
            it?.let {
                binding.listOrders.adapter?.apply {
                    adapter.orders = it
                    notifyDataSetChanged()
                }
            }
        }
        viewModel.order.observe(viewLifecycleOwner){
            it?.let {
                viewModel.getOrders(viewModel.statusFilter.value)
            }
        }
        viewModel.error.observe(viewLifecycleOwner){
            it?.let {
                binding.loaderLayout.circularLoader.hideAnimationBehavior
                binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                handleError(it)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        callBack = parentFragment as OnClickListener
    }

    private fun showDialog(action: String, onClick: DialogInterface.OnClickListener) {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle("$action action")
                .setMessage("Do you want to $action this order?")
                .setNeutralButton(getString(R.string.no)) { dialog, _ ->
                    dialog.cancel()
                }
                .setPositiveButton(getString(R.string.yes), onClick)
                .show()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.clearErrors()
    }

}