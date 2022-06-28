package vn.ztech.software.ecomSeller.ui.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpNav()
    }

    private fun setUpNav() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.home_nav_host_fragment) as NavHostFragment
        NavigationUI.setupWithNavController(binding.homeBottomNavigation, navHostFragment.navController)
        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("MENU destination", destination.id.toString())
            Log.d("MENU:QUEUE", navHostFragment.navController.backQueue.toString())
            when (destination.id) {
                R.id.homeFragment -> setBottomNavVisibility(View.VISIBLE)
                R.id.cartFragment -> setBottomNavVisibility(View.VISIBLE)
                R.id.accountFragment -> setBottomNavVisibility(View.VISIBLE)
                R.id.orderHistoryFragment -> setBottomNavVisibility(View.VISIBLE)
                R.id.categoryFragment -> setBottomNavVisibility(View.VISIBLE)
                //feature: enable later
//                R.id.orderSuccessFragment -> setBottomNavVisibility(View.VISIBLE)
                else -> setBottomNavVisibility(View.GONE)
            }
        }
        //later: for seller
//        val sessionManager = ShoppingAppSessionManager(this.applicationContext)
//        if (sessionManager.isUserSeller()) {
//            binding.homeBottomNavigation.menu.removeItem(R.id.cartFragment)
//        }else {
//            binding.homeBottomNavigation.menu.removeItem(R.id.ordersFragment)
//        }
    }

    private fun setBottomNavVisibility(visibility: Int) {
        binding.homeBottomNavigation.visibility = visibility
    }
}