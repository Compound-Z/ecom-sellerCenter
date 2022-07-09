package vn.ztech.software.ecomSeller.ui.main

import android.app.PendingIntent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.database.local.user.UserManager
import vn.ztech.software.ecomSeller.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var userManager: UserManager
    val viewModel: MainViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        notiPermission() //todo; for android oreo on oppo, xiaomi, vivo.. cant receive noti when app is killed. fix this later, time consumming
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpNav()

        observer()
        userManager = UserManager.getInstance(applicationContext)
        handleFCMToken()

        val launchFromNoti = intent.getBooleanExtra("launchFromNoti", false)
        Log.d("xxx launch", intent.getBooleanExtra("launchFromNoti",false).toString())

        val orderId = intent.getStringExtra("orderId")
        Log.d("xxx orderId", orderId.toString())

        if (launchFromNoti){
            val orderId = intent.getStringExtra("orderId")
            Log.d("xxx orderId", orderId.toString())
            if(orderId != "null") {
                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.home_nav_host_fragment) as NavHostFragment
                navHostFragment.navController.navigate(
                    R.id.action_orderHistoryFragment_to_orderDetailsFragment,
                    bundleOf(
                        "fromWhere" to "MainActivity",
                        "orderId" to orderId
                    )
                )
            }
        }
    }

    private fun observer() {
        viewModel.updateFCMTokenStatus.observe(this){
            it?.let {
                if (it){
                    /**after uploading this new token to server, mark this fcmtoken at local as old, so that
                     * app wont try to upload it again. Uploading it to server once is enough*/
                    userManager.updateIsFCMTokenNew(false)
                }
            }
        }
    }


    private fun handleFCMToken() {
        val localFCMToken =  userManager.getFCMToken()
        val isFCMTokenNew = userManager.getIsFCMTokenNew()
        Log.d(TAG, "Local token ${localFCMToken}")

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                Log.d(TAG, "remote token ${token}")

                if (localFCMToken == token.toString()){
                    if(isFCMTokenNew){
                        viewModel.updateFCMToken(token.toString())
                        FirebaseMessaging.getInstance().subscribeToTopic("admin").addOnCompleteListener {task->
                            if(!task.isSuccessful){
                                Log.d("FCM", "Subscribe topic failed!")
                            }else{
                                Log.d("FCM", "Subscribe topic successfully!" + task.toString())
                            }

                        }
                    }else{
                        return@OnCompleteListener
                    }
                }else{
                    /**update localFCMToken with new token*/
                    Log.d(TAG, "update token ${token}")
                    viewModel.updateFCMToken(token.toString())
                    FirebaseMessaging.getInstance().subscribeToTopic("admin").addOnCompleteListener {task->
                        if(!task.isSuccessful){
                            Log.d("FCM", "Subscribe topic failed!")
                        }else{
                            Log.d("FCM", "Subscribe topic successfully!" + task.toString())
                        }
                    }
                    userManager.saveNewFCMToken(token.toString())
                    //todo: warning: no error checking here
                }
            })
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
                R.id.accountFragment -> setBottomNavVisibility(View.VISIBLE)
                R.id.orderHistoryFragment -> setBottomNavVisibility(View.VISIBLE)
                R.id.categoryFragment -> setBottomNavVisibility(View.VISIBLE)
                R.id.productFragment -> setBottomNavVisibility(View.VISIBLE)
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
    //    private fun notiPermission() {
//        val intent = Intent()
//
//        when (Build.MANUFACTURER.lowercase()) {
//            "xiaomi" -> intent.component = ComponentName(
//                "com.miui.securitycenter",
//                "com.miui.permcenter.autostart.AutoStartManagementActivity"
//            )
//            "oppo" -> {autoStart()}
////                intent.component = ComponentName(
////                "com.coloros.safecenter",
////                "com.coloros.safecenter.permission.startup.StartupAppListActivity"
////            )
//            "vivo" -> intent.component = ComponentName(
//                "com.vivo.permissionmanager",
//                "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
//            )
//        }
//
//        val arrayList = packageManager.queryIntentActivities(
//            intent,
//            PackageManager.MATCH_DEFAULT_ONLY
//        )
//
//        if (arrayList.size > 0) {
//            startActivity(intent)
//        }
//    }

//    private fun autoStart() {
//        try {
//            val intent = Intent()
//            intent.setClassName(
//                "com.coloros.safecenter",
//                "com.coloros.safecenter.permission.startup.StartupAppListActivity"
//            )
//            startActivity(intent)
//        } catch (e: Exception) {
//            try {
//                val intent = Intent()
//                intent.setClassName(
//                    "com.oppo.safe",
//                    "com.oppo.safe.permission.startup.StartupAppListActivity"
//                )
//                startActivity(intent)
//            } catch (ex: Exception) {
//                try {
//                    val intent = Intent()
//                    intent.setClassName(
//                        "com.coloros.safecenter",
//                        "com.coloros.safecenter.startupapp.StartupAppListActivity"
//                    )
//                    startActivity(intent)
//                } catch (exx: Exception) {
//                }
//            }
//        }
//    }
}