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
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.common.Constants
import vn.ztech.software.ecomSeller.databinding.FragmentSaleReportBinding
import vn.ztech.software.ecomSeller.ui.BaseFragment2


class SaleReportFragment : BaseFragment2<FragmentSaleReportBinding>() {
    private lateinit var saleReportInformationInfoFragmentAdapter: SaleReportInformationInfoFragmentAdapter

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

    override fun setUpViews() {
        super.setUpViews()
        saleReportInformationInfoFragmentAdapter = SaleReportInformationInfoFragmentAdapter(this@SaleReportFragment)
        binding.pagerImportantInfo.adapter = saleReportInformationInfoFragmentAdapter
        TabLayoutMediator(binding.tabLayoutImportantInfo, binding.pagerImportantInfo) {tab, pos->
            tab.text =  Constants.SaleReportIndicator[pos]
        }.attach()
        setupCustomTabs(binding.tabLayoutImportantInfo)
    }

    override fun observeView() {
        super.observeView()
    }
    class SaleReportInformationInfoFragmentAdapter(fragment: SaleReportFragment) : FragmentStateAdapter(fragment) {

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