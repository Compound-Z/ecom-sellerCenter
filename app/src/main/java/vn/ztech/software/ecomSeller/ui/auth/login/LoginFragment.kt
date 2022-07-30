package vn.ztech.software.ecomSeller.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.databinding.FragmentLoginBinding
import vn.ztech.software.ecomSeller.ui.BaseFragment
import vn.ztech.software.ecomSeller.ui.LoginViewErrors
import vn.ztech.software.ecomSeller.ui.main.MainActivity
import vn.ztech.software.ecomSeller.util.MOB_ERROR_TEXT
import vn.ztech.software.ecomSeller.util.PASSWORD_ERROR_TEXT
import vn.ztech.software.ecomSeller.util.extension.showErrorDialog


class LoginFragment : BaseFragment<FragmentLoginBinding>() {
	private val viewModel: LogInViewModel by viewModel()

	override fun setViewBinding(): FragmentLoginBinding {
		return FragmentLoginBinding.inflate(layoutInflater)
	}

	override fun observeView() {
		super.observeView()
		viewModel.loading.observe(viewLifecycleOwner){
			if(it){
				handleLoadingDialog(true, R.string.logging_in)
			}else{
				handleLoadingDialog(false, R.string.logging_in)
			}
		}

		viewModel.error.observe(viewLifecycleOwner){
			it?.let {
				showErrorDialog(it)
			}
		}
		viewModel.errorInputData.observe(viewLifecycleOwner) { err ->
			modifyErrors(err)
		}

		viewModel.isLogInSuccessfully.observe(viewLifecycleOwner) {
			if (it){
				val isRemOn = binding.loginRemSwitch.isChecked
				val bundle = bundleOf(
//					"USER_DATA" to viewModel.userData,
//					"REMEMBER_ME" to isRemOn
				)
				goHome(bundle)
			}
		}
	}

	private fun goHome(extras: Bundle) {
		Log.d("LOGIN","Go HOME")
		val intent = Intent(context, MainActivity::class.java)
			.putExtras(extras)
		intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
		startActivity(intent)
		activity?.finish()
	}

	override fun setUpViews() {
		super.setUpViews()

		binding.loginMobileEditText.onFocusChangeListener = focusChangeListener
		binding.loginPasswordEditText.onFocusChangeListener = focusChangeListener

		binding.loginLoginBtn.setOnClickListener {
			onLogin()
		}

		binding.loginForgotTv.setOnClickListener {
			findNavController().navigate(R.id.action_login_to_forgot_password)
		}

		setUpClickableSignUpText()
	}
	private fun modifyErrors(err: LoginViewErrors) {
		when (err) {
			LoginViewErrors.NONE -> setEditTextErrors()
			LoginViewErrors.ERR_EMPTY -> setErrorText("Fill all details")
			LoginViewErrors.ERR_MOBILE -> setEditTextErrors(MOB_ERROR_TEXT, binding.loginMobileEditText)
			LoginViewErrors.ERR_PASSWORD -> setEditTextErrors(PASSWORD_ERROR_TEXT, binding.loginPasswordEditText)
			else -> setEditTextErrors()
		}
	}
	private fun setErrorText(errText: String?) {
		binding.loginErrorTextView.visibility = View.VISIBLE
		if (errText != null) {
			binding.loginErrorTextView.text = errText
		}
	}
	private fun setEditTextErrors(mobError: String? = null, view: TextInputEditText? = null) {
		view?.let{
			binding.loginErrorTextView.visibility = View.GONE
			view.error = mobError
		}

	}
//
	private fun setUpClickableSignUpText() {
		val signUpText = getString(R.string.login_signup_text)
		val ss = SpannableString(signUpText)
		val clickableSpan = object : ClickableSpan() {
			override fun onClick(widget: View) {
				findNavController().navigate(R.id.action_login_to_signup)
			}
		}

		ss.setSpan(clickableSpan, 10, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
		binding.loginSignupTextView.apply {
			text = ss
			movementMethod = LinkMovementMethod.getInstance()
		}
	}

	private fun onLogin() {
		val mob = binding.loginMobileEditText.text.toString()
		val pwd = binding.loginPasswordEditText.text.toString()

		viewModel.login(mob, pwd)
	}
	override fun onStop() {
		super.onStop()
		viewModel.clearErrors()
	}

}