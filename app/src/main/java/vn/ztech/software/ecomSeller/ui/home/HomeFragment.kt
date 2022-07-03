package vn.ztech.software.ecomSeller.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import vn.ztech.software.ecomSeller.databinding.FragmentHomeBinding
import vn.ztech.software.ecomSeller.common.Constants
import vn.ztech.software.ecomSeller.ui.BaseFragment2

private const val TAG = "HomeFragment"


class HomeFragment : BaseFragment2<FragmentHomeBinding>() {
    private lateinit var saleReportFragmentAdapter: SaleReportFragmentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI()
    }
    override fun setUpViews() {
        super.setUpViews()
        binding.swipeRefresh.setOnRefreshListener {
            setUpUI()
        }
    }

    private fun setUpUI() {
        saleReportFragmentAdapter =
            SaleReportFragmentAdapter(this@HomeFragment)
        binding.pager.adapter = saleReportFragmentAdapter
        TabLayoutMediator(binding.tabLayout, binding.pager) {tab, pos->
            tab.text =  Constants.SaleReportTimeOptions[pos].first
        }.attach()

        binding.swipeRefresh.isRefreshing = false
    }

    override fun observeView() {
        super.observeView()
        binding.homeTopAppBar.topAppBar.title = "Sale statistics"
    }

    class SaleReportFragmentAdapter(fragment: HomeFragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int {
            return Constants.SaleReportTimeOptions.size
        }

        override fun createFragment(position: Int): Fragment {
            val fragment = SaleReportFragment()
            fragment.arguments = Bundle().apply {
                putInt("numberOfDays", Constants.SaleReportTimeOptions[position].second)
            }
            return fragment
        }
    }

    override fun setViewBinding(): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }
}