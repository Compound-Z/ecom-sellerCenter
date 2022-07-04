package vn.ztech.software.ecomSeller.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.distinctUntilChanged
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.custom_tab_important_info.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.common.Constants
import vn.ztech.software.ecomSeller.databinding.FragmentSaleReportBinding
import vn.ztech.software.ecomSeller.model.OrderWithTime
import vn.ztech.software.ecomSeller.ui.BaseFragment2
import vn.ztech.software.ecomSeller.util.extension.getAvgSale
import vn.ztech.software.ecomSeller.util.extension.getNumberOfOrder
import vn.ztech.software.ecomSeller.util.extension.getTotalSales
import java.time.LocalDate
import kotlin.properties.Delegates


class SaleReportFragment : BaseFragment2<FragmentSaleReportBinding>() {
    private lateinit var saleReportInformationInfoFragmentAdapter: SaleReportImportantInfoFragmentAdapter
    private val viewModel: SaleReportViewModel by sharedViewModel()
    private val homeViewModel: HomeViewModel by sharedViewModel()
    var numberOfDays by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.takeIf { it.containsKey("numberOfDays") }?.let {
            numberOfDays = it.getInt("numberOfDays")
            viewModel.getOrdersBaseOnTime(numberOfDays)
        }
    }

    override fun setUpViews() {
        super.setUpViews()

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
                setUpTabs()
            }
        }
        viewModel.error.distinctUntilChanged().observe(viewLifecycleOwner){
            it?.let {
                binding.loaderLayout.circularLoader.hideAnimationBehavior
                binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                homeViewModel.error.value = it
            }
        }
    }

    private fun setUpTabs() {
        saleReportInformationInfoFragmentAdapter = SaleReportImportantInfoFragmentAdapter(this@SaleReportFragment, numberOfDays)
        binding.pagerImportantInfo.adapter = saleReportInformationInfoFragmentAdapter
        TabLayoutMediator(binding.tabLayoutImportantInfo, binding.pagerImportantInfo) {tab, pos->
        }.attach()
        setupCustomTabs(binding.tabLayoutImportantInfo)
    }

    private fun setupCustomTabs(tabs: TabLayout) {
        Constants.SaleReportIndicator.forEachIndexed { index, string ->
            val customView = getCustomView(index, string)
            tabs.getTabAt(index)?.customView = customView
        }
    }

    private fun getCustomView(index: Int, string: String): View {
        val view = layoutInflater.inflate(R.layout.custom_tab_important_info, null)
        view.tvTitle.text = string
        var content = ""
        when(index){
            0 -> {
                viewModel.orders.value?.get(numberOfDays)?.let {
                    content = it.getNumberOfOrder().toString()
                }
            }
            1 -> {
                viewModel.orders.value?.get(numberOfDays)?.let {
                    content = it.getTotalSales().toString()
                }
            }
            2 -> {
                viewModel.orders.value?.get(numberOfDays)?.let {
                    content = it.getAvgSale().toString()
                }
            }
        }
        view.tvContent.text = content

        return view
    }
    class SaleReportImportantInfoFragmentAdapter(fragment: SaleReportFragment, val numberOfDays: Int) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int {
            return Constants.SaleReportIndicator.size
        }

        override fun createFragment(position: Int): Fragment {
            val fragment = ChartFragment()
            fragment.arguments = Bundle().apply {
                putString("indicator", Constants.SaleReportIndicator[position])
                putInt("numberOfDays", numberOfDays)
            }
            return fragment
        }
    }

    override fun setViewBinding(): FragmentSaleReportBinding {
        return FragmentSaleReportBinding.inflate(layoutInflater)
    }

    override fun onStop() {
        super.onStop()
        viewModel.clearErrors()
    }
}