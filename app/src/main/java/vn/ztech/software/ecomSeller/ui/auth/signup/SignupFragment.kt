package vn.ztech.software.ecomSeller.ui.auth.signup

import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.ecomSeller.databinding.FragmentSignupBinding
import vn.ztech.software.ecomSeller.ui.BaseFragment
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.ui.SignUpViewErrors

import vn.ztech.software.ecomSeller.util.EMAIL_ERROR_TEXT
import vn.ztech.software.ecomSeller.util.MOB_ERROR_TEXT
import vn.ztech.software.ecomSeller.util.PASSWORD_ERROR_TEXT
import vn.ztech.software.ecomSeller.util.extension.showErrorDialog

class SignupFragment : BaseFragment<FragmentSignupBinding>() {
	private val viewModel: SignUpViewModel by viewModel()

	override fun setViewBinding(): FragmentSignupBinding {
		return FragmentSignupBinding.inflate(layoutInflater)
	}

	override fun observeView() {
		super.observeView()

		viewModel.loading.observe(viewLifecycleOwner){
			if(it){
				handleLoadingDialog(true, R.string.signing_up)
			}else{
				handleLoadingDialog(false, R.string.signing_up)
			}
		}

		viewModel.errorStatus.observe(viewLifecycleOwner) { err ->
			err?.let{modifyErrors(it)}
		}

		viewModel.isSignUpSuccessfully.observe(viewLifecycleOwner) {
			Log.d("ERROR:","viewModel.isSignUpSuccessfully: $it")
			it?.let {
				if (it) {
					val bundle = bundleOf("USER_DATA" to viewModel.userData.value)
					launchOtpActivity(getString(R.string.signup_fragment_label), bundle)
				}
			}

		}
		viewModel.signUpError.observe(viewLifecycleOwner) {
			it?.let {
				Log.d("ERROR:","viewModel.signUpError: $it")
				showErrorDialog(it)
			}
		}
	}
	override fun setUpViews() {
		super.setUpViews()
		Log.d("ERROR:","SignupFragment setUpViews ${this@SignupFragment}")

		binding.signupErrorTextView.visibility = View.GONE

		binding.signupNameEditText.onFocusChangeListener = focusChangeListener
		binding.signupMobileEditText.onFocusChangeListener = focusChangeListener
		binding.signupEmailEditText.onFocusChangeListener = focusChangeListener
		binding.signupPasswordEditText.onFocusChangeListener = focusChangeListener
		binding.signupCnfPasswordEditText.onFocusChangeListener = focusChangeListener

		binding.signupSignupBtn.setOnClickListener {
			onSignUp()

		}
		setUpClickableLoginText()
	}

	private fun setUpClickableLoginText() {
		val loginText = getString(R.string.signup_login_text)
		val ss = SpannableString(loginText)

		val clickableSpan = object : ClickableSpan() {
			override fun onClick(widget: View) {
				findNavController().navigate(R.id.action_signup_to_login)
			}
		}

		ss.setSpan(clickableSpan, 25, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
		binding.signupLoginTextView.apply {
			text = ss
			movementMethod = LinkMovementMethod.getInstance()
		}
	}

	private fun onSignUp() {
		val name = binding.signupNameEditText.text.toString()
		val mobile = binding.signupMobileEditText.text.toString()
		val email = binding.signupEmailEditText.text.toString()
		val password1 = binding.signupPasswordEditText.text.toString()
		val password2 = binding.signupCnfPasswordEditText.text.toString()
		val isAccepted = binding.signupPolicySwitch.isChecked
//		val isSeller = binding.signupSellerSwitch.isChecked /**v2*/

		viewModel.signUpSubmitData(name, mobile, email, password1, password2, isAccepted/*, isSeller*/)
	}
	private fun modifyErrors(err: SignUpViewErrors) {
		when (err) {
			SignUpViewErrors.NONE -> setEditTextsError()
			SignUpViewErrors.ERR_EMAIL -> setEditTextsError(emailError = EMAIL_ERROR_TEXT)
			SignUpViewErrors.ERR_MOBILE -> setEditTextsError(mobError = MOB_ERROR_TEXT)
			SignUpViewErrors.ERR_EMAIL_MOBILE -> setEditTextsError(EMAIL_ERROR_TEXT, MOB_ERROR_TEXT)
			SignUpViewErrors.ERR_EMPTY -> setErrorText("Fill all details.")
			SignUpViewErrors.ERR_NOT_ACC -> setErrorText("Accept the Terms.")
			SignUpViewErrors.ERR_PWD12NS -> setErrorText("Both passwords are not same!")
			SignUpViewErrors.ERR_PW_INVALID -> setErrorText(PASSWORD_ERROR_TEXT)
		}
	}
//
	private fun setErrorText(errText: String?) {
		binding.signupErrorTextView.visibility = View.VISIBLE
		if (errText != null) {
			binding.signupErrorTextView.text = errText
		}
	}
	private fun setEditTextsError(emailError: String? = null, mobError: String? = null) {
		binding.signupEmailEditText.error = emailError
		binding.signupMobileEditText.error = mobError
		binding.signupErrorTextView.visibility = View.GONE
	}

	override fun onPause() {
		super.onPause()
		viewModel.clearError()
	}
}