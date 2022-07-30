package vn.ztech.software.ecomSeller.ui.account

import android.content.Intent
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.databinding.FragmentAccountBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.ecomSeller.ui.BaseFragment
import vn.ztech.software.ecomSeller.ui.auth.LoginSignupActivity
import vn.ztech.software.ecomSeller.ui.main.MainActivity
import vn.ztech.software.ecomSeller.ui.splash.ISplashUseCase

private const val TAG = "AccountFragment"

class AccountFragment : BaseFragment<FragmentAccountBinding>() {

	private val viewModel: AccountViewModel by viewModel()
	override fun setViewBinding(): FragmentAccountBinding {
		return FragmentAccountBinding.inflate(layoutInflater)
	}

	override fun setUpViews() {
		super.setUpViews()
		binding.accountTopAppBar.topAppBar.title = getString(R.string.account_fragment_title)
//		binding.accountProfileTv.setOnClickListener {
//			Log.d(TAG, "Profile Selected")
//			findNavController().navigate(R.id.action_accountFragment_to_profileFragment)
//		}
		binding.accountOrdersTv.setOnClickListener {
			(activity as MainActivity).binding.homeBottomNavigation.selectedItemId = R.id.orderHistoryFragment
		}
		binding.accountReviewTv.setOnClickListener {
			findNavController().navigate(
				R.id.action_accountFragment_to_listReviewFragment,
			)
		}
//		binding.accountAddressTv.setOnClickListener {
//			findNavController().navigate(
//				R.id.action_accountFragment_to_addressFragment,
//				bundleOf("fromWhere" to "AccountFragment"))
//		}
		binding.accountSignOutTv.setOnClickListener {
			showSignOutDialog()
		}
	}

	override fun observeView() {
		super.observeView()
		viewModel.loading.observe(viewLifecycleOwner){
			if(it){
				handleLoadingDialog(true, R.string.logging_out)
			}else{
				handleLoadingDialog(false, R.string.logging_out)
			}
		}
		viewModel.error.observe(viewLifecycleOwner){
			it ?: return@observe
			handleError(it)
		}
		viewModel.isLogOutSuccessfully.observe(viewLifecycleOwner){
			if (it){
				goLogIn()
			}
		}
	}

	private fun showSignOutDialog() {
		context?.let {
			MaterialAlertDialogBuilder(it)
				.setTitle(getString(R.string.sign_out_dialog_title_text))
				.setMessage(getString(R.string.sign_out_dialog_message_text))
				.setNegativeButton(getString(R.string.pro_cat_dialog_cancel_btn)) { dialog, _ ->
					dialog.cancel()
				}
				.setPositiveButton(getString(R.string.dialog_sign_out_btn_text)) { dialog, _ ->
					viewModel.logOut()
				}
				.show()
		}
	}

	private fun goLogIn() {
		val logInIntent = Intent(context, LoginSignupActivity::class.java)
			.putExtra("PAGE", ISplashUseCase.PAGE.LOGIN)
			.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
		startActivity(logInIntent)
		activity?.finish()
	}

	override fun onStop() {
		super.onStop()
		viewModel.clearErrors()
	}


}