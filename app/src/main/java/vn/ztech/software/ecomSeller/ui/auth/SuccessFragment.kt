package vn.ztech.software.ecomSeller.ui.success

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.databinding.FragmentSuccessBinding

interface SuccessFragmentListener{
	fun onDialogDismiss()
}
class SuccessFragment(val listener: SuccessFragmentListener, val fromWhere: String) : DialogFragment() {

	private lateinit var binding: FragmentSuccessBinding
	var redirectStringId: Int = -1
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = FragmentSuccessBinding.inflate(layoutInflater)
		return binding.root
	}

	override fun onStart() {
		super.onStart()
		/**set full screen*/
		val params = dialog?.window?.attributes
		params?.width = WindowManager.LayoutParams.MATCH_PARENT
		params?.height = WindowManager.LayoutParams.MATCH_PARENT
		dialog?.window?.attributes = params
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		when(fromWhere){
			""->{
				binding.orderSuccessLabelTv.text = getString(R.string.verify_successfully)
				binding.redirectHomeTimerTv.text = getString(R.string.back_to_login)
			}
			getString(R.string.forgot_password_fragment_label)->{
				binding.orderSuccessLabelTv.text = getString(R.string.reset_password_successfully)
			}
			getString(R.string.signup_fragment_label)->{
				binding.orderSuccessLabelTv.text = getString(R.string.signup_successfully)
			}
		}

		binding.redirectHomeTimerTv.text =
			getString(R.string.redirect_login_timer_text, "5")
		countDownTimer.start()

		binding.backToHomeBtn.setOnClickListener {
			countDownTimer.cancel()
			listener.onDialogDismiss()
		}
	}

	private val countDownTimer = object : CountDownTimer(5000, 1000) {
		override fun onTick(millisUntilFinished: Long) {
			Log.d("SUCCESS","onTick")
			val sec = millisUntilFinished / 1000
			binding.redirectHomeTimerTv.text =
				getString(R.string.redirect_login_timer_text, sec.toString())
		}

		override fun onFinish() {
			Log.d("SUCCESS","onFinish")
			listener.onDialogDismiss()
		}
	}
}