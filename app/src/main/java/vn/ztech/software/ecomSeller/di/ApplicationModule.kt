package vn.ztech.software.ecomSeller.di

import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import vn.ztech.software.ecomSeller.database.local.user.UserManager

fun applicationModule() = module {

//    single {
//        FirebaseAnalytics.getInstance(androidApplication())
//    }

//    single {
//        AuthStateManager.getInstance(androidApplication())
//    }

    single {
        UserManager.getInstance(androidApplication())
    }

//    single {
//        AuthorizationService(androidApplication())
//    }
//
//    single<IAnalyticsClient> {
//        AnalyticsClient(get(), get())
//    }

}