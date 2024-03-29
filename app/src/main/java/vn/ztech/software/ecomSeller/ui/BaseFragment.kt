package vn.ztech.software.ecomSeller.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.exception.RefreshTokenExpiredException
import vn.ztech.software.ecomSeller.ui.auth.LoginSignupActivity
import vn.ztech.software.ecomSeller.ui.auth.otp.OtpActivity
import vn.ztech.software.ecomSeller.ui.splash.ISplashUseCase
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.extension.showErrorDialog

abstract class BaseFragment<VBinding : ViewBinding>: Fragment() {

    protected lateinit var binding: VBinding
    protected abstract fun setViewBinding(): VBinding
    protected val focusChangeListener = MyOnFocusChangeListener()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }
    private fun init() {
        binding = setViewBinding()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ERROR:","BaseFragment onVixxxxewCreated")

        setUpViews()
        observeView()
    }

    open fun setUpViews() {}

    open fun observeView() {}

    fun launchOtpActivity(from: String, extras: Bundle) {
        val intent = Intent(context, OtpActivity::class.java).putExtra(
            "from",
            from
        ).putExtras(extras)
        startActivity(intent)
    }

    fun handleLoadingDialog(show: Boolean, messageId: Int){
        val loaderLayout = activity?.findViewById<ConstraintLayout>(R.id.loader_layout)
        val loadingMessage = loaderLayout?.findViewById<TextView>(R.id.loading_message)

        loaderLayout?.visibility =if(show) View.VISIBLE else View.GONE
        loadingMessage?.text = context?.getString(messageId)
    }

    fun openLogInSignUpActivity(page: ISplashUseCase.PAGE){
        val intent = Intent(activity, LoginSignupActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("PAGE", page)
        startActivity(intent)
    }

    fun handleError(error: CustomError){
        if(error.e is RefreshTokenExpiredException){
            openLogInSignUpActivity(ISplashUseCase.PAGE.LOGIN)
        }else{
            showErrorDialog(error)
        }
    }
    fun toastCenter(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).apply {
            setGravity(Gravity.CENTER, 0, 0)
        }.show()
    }

}