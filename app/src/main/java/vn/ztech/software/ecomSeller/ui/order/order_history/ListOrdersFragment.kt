package vn.ztech.software.ecomSeller.ui.order.order_history

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.item_order_history.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.ztech.software.ecomSeller.databinding.FragmentListOrderBinding
import vn.ztech.software.ecomSeller.ui.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.common.Constants
import vn.ztech.software.ecomSeller.common.LoadState
import vn.ztech.software.ecomSeller.model.Order
import vn.ztech.software.ecomSeller.ui.BaseFragment2
import vn.ztech.software.ecomSeller.util.CustomError

const val TAG = "ListOrdersFragment"
class ListOrdersFragment() : BaseFragment2<FragmentListOrderBinding>() {
    private val viewModel: ListOrdersViewModel by viewModel()
    lateinit var statusFilter: String
    lateinit var adapter: ListOrderAdapter
    lateinit var spinnerAdapter: ArrayAdapter<String>
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
        Log.d("xxx", viewModel.orders.value.toString())
        if(viewModel.orders.value == null){
            viewModel.getOrders(viewModel.statusFilter.value)
        }
        setupAdapter()
        setupSpinner()
        setUpSearchView()
    }

    override fun onResume() {
        super.onResume()
        adapter.refresh()
    }

    override fun setViewBinding(): FragmentListOrderBinding {
        return FragmentListOrderBinding.inflate(layoutInflater)
    }

    override fun setUpViews() {
        super.setUpViews()
//        setupAdapter()
//        setupSpinner()
//        setUpSearchView()
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.orders.observe(viewLifecycleOwner){
                it?.let {
                    binding.listOrders.adapter?.apply {
                        adapter.submitData(viewLifecycleOwner.lifecycle,it)
//                        notifyDataSetChanged()
                    }
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

    private fun setUpSearchView() {
        val inputManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)

        binding.topAppBar.homeSearchEditText.onFocusChangeListener = focusChangeListener
        binding.topAppBar.homeSearchEditText.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                textView.clearFocus()
                val inputManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(textView.windowToken, 0)

                performSearch(textView.text.toString())
                true
            } else {
                false
            }
        }
        binding.topAppBar.searchOutlinedTextLayout.setEndIconOnClickListener {
            Log.d("SEARCH", "setEndIconOnClickListener")
            it.clearFocus()
            binding.topAppBar.homeSearchEditText.setText("")
            val inputManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
        binding.topAppBar.searchOutlinedTextLayout.setStartIconOnClickListener {
            Log.d("SEARCH", "setEndIconOnClickListener")
            it.clearFocus()
            val inputManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(it.windowToken, 0)
            performSearch(binding.topAppBar.homeSearchEditText.text.toString())
        }
    }

    private fun performSearch(searchWords: String) {
        viewModel.search(searchWords)
    }

    private fun setupSpinner() {
        val searchCriteria = resources.getStringArray(R.array.search_criteria)
        viewModel.currentSelectedSearchCriteria.value = searchCriteria[0]
        viewModel.listSearchCriteria.value = searchCriteria.toList()

        spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, searchCriteria)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        binding.topAppBar.spinnerSearchCriteria.adapter = spinnerAdapter
        binding.topAppBar.spinnerSearchCriteria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                viewModel.currentSelectedSearchCriteria.value = viewModel.listSearchCriteria.value?.get(p2)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun setupAdapter(){
        adapter = ListOrderAdapter(requireContext(), object : ListOrderAdapter.OnClickListener{
            override fun onClick(order: Order) {
                callBack.onClickViewDetails(order._id)
            }

            override fun onCopyClipBoardClicked(orderId: String) {
                copyToClipBoard(orderId)
            }
            override fun onClickButtonViewDetail(order: Order) {
                handleAction(order)
            }
        })
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.listOrders.adapter = adapter
        adapter.addLoadStateListener {loadState->
            // show empty list
            if (loadState.refresh is androidx.paging.LoadState.Loading ||
                loadState.append is androidx.paging.LoadState.Loading){
                binding.loaderLayout.circularLoader.showAnimationBehavior
                binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
            }
            else {
                binding.loaderLayout.circularLoader.hideAnimationBehavior
                binding.loaderLayout.loaderFrameLayout.visibility = View.GONE

                // If we have an error, show a toast
                val errorState = when {
                    loadState.append is androidx.paging.LoadState.Error -> loadState.append as androidx.paging.LoadState.Error
                    loadState.prepend is androidx.paging.LoadState.Error ->  loadState.prepend as androidx.paging.LoadState.Error
                    loadState.refresh is androidx.paging.LoadState.Error -> loadState.refresh as androidx.paging.LoadState.Error
                    else -> null
                }
                errorState?.let {
                    handleError(CustomError(it.error, it.error.message?:"System error"))
                }

            }
        }
    }

    private fun copyToClipBoard(orderId: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("orderId", orderId)
        clipboard.setPrimaryClip(clip)
        toastCenter("Copied $orderId")
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