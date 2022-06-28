package vn.ztech.software.ecomSeller.ui.auth.forgot_password

import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.databinding.FragmentForgotPasswordBinding
import vn.ztech.software.ecomSeller.ui.BaseFragment
import vn.ztech.software.ecomSeller.ui.LoginViewErrors
import vn.ztech.software.ecomSeller.ui.auth.login.ForgotPasswordViewModel
import vn.ztech.software.ecomSeller.util.MOB_ERROR_TEXT
import vn.ztech.software.ecomSeller.util.PASSWORD_ERROR_TEXT
import vn.ztech.software.ecomSeller.util.RETYPE_PASSWORD_ERROR_TEXT
import vn.ztech.software.ecomSeller.util.extension.showErrorDialog


class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>() {
    private val viewModel: ForgotPasswordViewModel by viewModel()

    override fun setViewBinding(): FragmentForgotPasswordBinding {
        return FragmentForgotPasswordBinding.inflate(layoutInflater)
    }

    override fun setUpViews() {
        super.setUpViews()

        binding.loginMobileEditText.onFocusChangeListener = focusChangeListener
        binding.loginPasswordEditText.onFocusChangeListener = focusChangeListener

        binding.resetBtn.setOnClickListener {
            onReset()
        }

        setUpClickableSignUpText()
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

        viewModel.isSentResetRequestSuccessfully.observe(viewLifecycleOwner) {
            if (it){
                val bundle = bundleOf(
                    "PHONE_NUMBER" to binding.loginMobileEditText.text.toString(),
                    "PASSWORD" to binding.loginPasswordEditText.text.toString()
                )
                launchOtpActivity(getString(R.string.forgot_password_fragment_label), bundle)
            }
        }
    }

    private fun goLogin() {
        findNavController().navigate(R.id.action_forgot_password_to_login)
    }

    private fun modifyErrors(err: LoginViewErrors) {
        when (err) {
            LoginViewErrors.NONE -> setEditTextErrors()
            LoginViewErrors.ERR_EMPTY -> setErrorText("Fill all details")
            LoginViewErrors.ERR_MOBILE -> setEditTextErrors(MOB_ERROR_TEXT, binding.loginMobileEditText)
            LoginViewErrors.ERR_PASSWORD -> setEditTextErrors(PASSWORD_ERROR_TEXT, binding.loginPasswordEditText)
            LoginViewErrors.ERR_RETYPE_PASSWORD -> setEditTextErrors(RETYPE_PASSWORD_ERROR_TEXT, binding.retypeResetPasswordEditText)

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
    private fun setUpClickableSignUpText() {
        val signUpText = getString(R.string.back_to_login)
        val ss = SpannableString(signUpText)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_forgot_password_to_login)
            }
        }

        ss.setSpan(clickableSpan, 0, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.loginSignupTextView.apply {
            text = ss
            movementMethod = LinkMovementMethod.getInstance()
        }
    }
    private fun onReset() {
        val mob = binding.loginMobileEditText.text.toString()
        val pwd = binding.loginPasswordEditText.text.toString()
        val pwd2 = binding.retypeResetPasswordEditText.text.toString()
        viewModel.resetPassword(mob, pwd, pwd2)
    }
    override fun onStop() {
        super.onStop()
        viewModel.clearErrors()
    }
}