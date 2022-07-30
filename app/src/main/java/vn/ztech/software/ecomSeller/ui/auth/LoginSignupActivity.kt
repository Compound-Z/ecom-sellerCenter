package vn.ztech.software.ecomSeller.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.ui.splash.ISplashUseCase

class LoginSignupActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val page = intent.getSerializableExtra("PAGE")
		setContentView(R.layout.activity_signup)
		Log.d("ERROR:","LoginSignupActivity onCreate")
		page?.let{
			when(page){
				ISplashUseCase.PAGE.LOGIN -> {
					val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
					val navController = navHostFragment.navController
					if(navController.currentBackStackEntry?.destination?.label != "Login Fragment"){
						navController.navigate(R.id.action_signup_to_login)
					}
				}
				else -> {}
			}
		}
	}
}