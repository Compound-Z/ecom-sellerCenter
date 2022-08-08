package vn.ztech.software.ecomSeller.ui.auth.otp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.databinding.ActivityOtpBinding
import vn.ztech.software.ecomSeller.model.UserData
import vn.ztech.software.ecomSeller.ui.auth.LoginSignupActivity
import vn.ztech.software.ecomSeller.ui.splash.ISplashUseCase
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.extension.showErrorDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.ecomSeller.common.Constants
import vn.ztech.software.ecomSeller.ui.OTPErrors
import vn.ztech.software.ecomSeller.ui.success.SuccessFragment
import vn.ztech.software.ecomSeller.ui.success.SuccessFragmentListener

class OtpActivity : AppCompatActivity(), SuccessFragmentListener {
	private lateinit var binding: ActivityOtpBinding

	private val viewModel: OtpViewModel by viewModel()
	private lateinit var successFragment : SuccessFragment
	var fromWhere: String? = null
	private var userData: UserData? = null
	var phoneNumber: String? = null
	var password: String? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityOtpBinding.inflate(layoutInflater)
		setContentView(binding.root)
		Log.d("ERROR:","OtpActivity onCreate")
		userData = intent.getParcelableExtra("USER_DATA")
		fromWhere = intent.getStringExtra("from").toString()
		when(fromWhere){
			getString(R.string.forgot_password_fragment_label)->{
				val bundle = intent.extras
				if(bundle!=null){
					phoneNumber = bundle.getString("PHONE_NUMBER").toString()
					password = bundle.getString("PASSWORD").toString()
				}else{
					/**if this type of error happen, return back to SignUpActivity*/
					showErrorDialog(CustomError()) { _, _ ->
						finish()
					}
				}
			}
			getString(R.string.signup_fragment_label)->{
				if (userData == null) {
					/**if this type of error happen, return back to SignUpActivity*/
					showErrorDialog(CustomError()) { _, _ ->
						finish()
					}
				}
			}
		}
		setViews()
		setObservers()

	}


	private fun setObservers() {
		viewModel.inputError.observe(this){
			modifyError(it)
		}
		viewModel.loading.observe(this){
			if(it){
				handleLoadingDialog(true, R.string.verifying_otp)
			}else{
				handleLoadingDialog(false, R.string.verifying_otp)
			}
		}
		viewModel.otpStatus.observe(this) {
			when (it.status) {
				Constants.VERIFY_APPROVED -> {
					successFragment = SuccessFragment(this, fromWhere?:"")
					successFragment.show(supportFragmentManager, "SuccessFragment")
				}
				Constants.VERIFY_FAILED ->  {
					val contextView = binding.loaderLayout.loaderCard
					Snackbar.make(contextView, R.string.otp_verify_failed, Snackbar.LENGTH_INDEFINITE).show()
				}
			}
		}

		viewModel.error.observe(this){
			it?.let {
				showErrorDialog(it)
			}
		}

		viewModel.otpResetPasswordStatus.observe(this){
			when(it.message){
				"approved"->{
					successFragment = SuccessFragment(this, fromWhere?:"")
					successFragment.show(supportFragmentManager, "SuccessFragment")
				}
				"pending"->{
					val contextView = binding.loaderLayout.loaderCard
					Snackbar.make(contextView, R.string.otp_verify_failed, Snackbar.LENGTH_INDEFINITE).show()				}
			}
		}
	}

	private fun modifyError(it: OTPErrors?) {
		when(it){
			OTPErrors.ERROR->{
				binding.otpOtpEditText.error = "Please enter a valid OTP code: 6 digits"
			}
			else -> {
				binding.otpOtpEditText.error = null
			}
		}
	}

	private fun setViews() {
		binding.otpVerifyBtn.setOnClickListener {
			when(fromWhere){
				getString(R.string.forgot_password_fragment_label)->{
					onVerifyResetPassword()
				}
				getString(R.string.signup_fragment_label)->{
					onVerify()
				}

			}
		}
	}

	private fun onVerifyResetPassword() {
		val otp = binding.otpOtpEditText.text.toString()
		if (otp.isNotEmpty() && phoneNumber!=null && password!=null){
			viewModel.verifyOTPResetPassword(phoneNumber!!, password!!, otp)
		}else{
			showErrorDialog(CustomError())
		}
	}

	private fun onVerify() {
		val otp = binding.otpOtpEditText.text.toString()
		viewModel.verifyOTP(userData?.phoneNumber!!, otp)
	}
	private fun handleLoadingDialog(show: Boolean, messageId: Int){
		val loaderLayout = findViewById<ConstraintLayout>(R.id.loader_layout)
		val loadingMessage = loaderLayout?.findViewById<TextView>(R.id.loading_message)
		loadingMessage?.text = getString(messageId)
		loaderLayout?.visibility =if(show) View.VISIBLE else View.GONE
	}

	private fun moveToLogin(){
		val logInIntent = Intent(this, LoginSignupActivity::class.java)
			.putExtra("PAGE", ISplashUseCase.PAGE.LOGIN)
			.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
		startActivity(logInIntent)
		finish()
	}

	override fun onDialogDismiss() {
		moveToLogin()
	}
	override fun onStop() {
		super.onStop()
		viewModel.clearErrors()
	}
}