package vn.ztech.software.ecomSeller.ui.auth

import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class LoginSignupBaseFragment<VBinding : ViewBinding> : Fragment() {

//	protected val viewModel: AuthViewModel by activityViewModels()
//
//	protected lateinit var binding: VBinding
//	protected abstract fun setViewBinding(): VBinding
//
//	protected val focusChangeListener = MyOnFocusChangeListener()
//
//	override fun onCreate(savedInstanceState: Bundle?) {
//		super.onCreate(savedInstanceState)
//		init()
//	}
//
//	override fun onCreateView(
//		inflater: LayoutInflater,
//		container: ViewGroup?,
//		savedInstanceState: Bundle?
//	): View? {
//		setUpViews()
//		observeView()
//		return binding.root
//	}
//
//	fun launchOtpActivity(from: String, extras: Bundle) {
//		val intent = Intent(context, OtpActivity::class.java).putExtra(
//			"from",
//			from
//		).putExtras(extras)
//		startActivity(intent)
//	}
//
//	open fun setUpViews() {}
//
//	open fun observeView() {}
//
//	private fun init() {
//		binding = setViewBinding()
//	}
//
//	interface OnClickListener : View.OnClickListener
}