package vn.ztech.software.ecomSeller.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.databinding.FragmentAddEditAddressBinding
import vn.ztech.software.ecomSeller.databinding.FragmentAddEditProductBinding
import vn.ztech.software.ecomSeller.ui.BaseFragment2


class AddEditProductFragment : BaseFragment2<FragmentAddEditProductBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun setUpViews() {
        super.setUpViews()
    }

    override fun observeView() {
        super.observeView()
    }
    override fun setViewBinding(): FragmentAddEditProductBinding {
        return FragmentAddEditProductBinding.inflate(layoutInflater)
    }

}