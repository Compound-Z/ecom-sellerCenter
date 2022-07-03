package vn.ztech.software.ecomSeller.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.custom_tab_important_info.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.common.Constants
import vn.ztech.software.ecomSeller.databinding.FragmentSaleReportBinding
import vn.ztech.software.ecomSeller.ui.BaseFragment2


class SaleReportFragment : BaseFragment2<FragmentSaleReportBinding>() {
    private lateinit var saleReportInformationInfoFragmentAdapter: SaleReportImportantInfoFragmentAdapter
    private val viewModel: SaleReportViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.takeIf { it.containsKey("numberOfDays") }?.let {
            viewModel.numberOfDays.value = it.getInt("numberOfDays")
            viewModel.getOrdersBaseOnTime(viewModel.numberOfDays.value)
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
        viewModel.error.observe(viewLifecycleOwner){
            it?.let {
                binding.loaderLayout.circularLoader.hideAnimationBehavior
                binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                handleError(it)
            }
        }
    }

    private fun setUpTabs() {
        saleReportInformationInfoFragmentAdapter = SaleReportImportantInfoFragmentAdapter(this@SaleReportFragment)
        binding.pagerImportantInfo.adapter = saleReportInformationInfoFragmentAdapter
        TabLayoutMediator(binding.tabLayoutImportantInfo, binding.pagerImportantInfo) {tab, pos->
            tab.text =  Constants.SaleReportIndicator[pos]
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
        view.tvContent.text = "Test content here ${index}"
        return view
    }
    class SaleReportImportantInfoFragmentAdapter(fragment: SaleReportFragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int {
            return Constants.SaleReportIndicator.size
        }

        override fun createFragment(position: Int): Fragment {
            val fragment = ChartFragment()
            fragment.arguments = Bundle().apply {
                putString("indicator", Constants.SaleReportIndicator[position])
            }
            return fragment
        }
    }

    override fun setViewBinding(): FragmentSaleReportBinding {
        return FragmentSaleReportBinding.inflate(layoutInflater)
    }
}