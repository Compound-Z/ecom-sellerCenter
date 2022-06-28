package vn.ztech.software.ecomSeller

import android.app.Application
//import com.chibatching.kotpref.Kotpref
//import com.chibatching.kotpref.gsonpref.gson
//import com.google.gson.Gson
//import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import vn.ztech.software.ecomSeller.di.allModule


class App : Application() {
    override fun onCreate() {
        super.onCreate()
//        Kotpref.init(this)
//        Kotpref.gson = Gson()
        startKoin {
            androidContext(this@App)
            modules(allModule())
        }
    }
}