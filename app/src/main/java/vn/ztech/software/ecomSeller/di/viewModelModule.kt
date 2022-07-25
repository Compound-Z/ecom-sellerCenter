package vn.ztech.software.ecomSeller.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import vn.ztech.software.ecomSeller.ui.account.AccountViewModel
import vn.ztech.software.ecomSeller.ui.account.review.ListReviewViewModel
import vn.ztech.software.ecomSeller.ui.address.AddressViewModel
import vn.ztech.software.ecomSeller.ui.auth.login.ForgotPasswordViewModel
import vn.ztech.software.ecomSeller.ui.auth.login.LogInViewModel
import vn.ztech.software.ecomSeller.ui.auth.otp.OtpViewModel
import vn.ztech.software.ecomSeller.ui.auth.signup.SignUpViewModel
import vn.ztech.software.ecomSeller.ui.cart.CartViewModel
import vn.ztech.software.ecomSeller.ui.category.CategoryViewModel
import vn.ztech.software.ecomSeller.ui.home.ChartViewModel
import vn.ztech.software.ecomSeller.ui.home.SaleReportViewModel
import vn.ztech.software.ecomSeller.ui.home.HomeViewModel
import vn.ztech.software.ecomSeller.ui.main.MainViewModel
import vn.ztech.software.ecomSeller.ui.order.order.OrderViewModel
import vn.ztech.software.ecomSeller.ui.order.order_details.OrderDetailsViewModel
import vn.ztech.software.ecomSeller.ui.order.order_history.ListOrdersViewModel
import vn.ztech.software.ecomSeller.ui.product.ProductViewModel
import vn.ztech.software.ecomSeller.ui.product_details.ListReviewOfProductViewModel
import vn.ztech.software.ecomSeller.ui.product_details.ProductDetailsViewModel
import vn.ztech.software.ecomSeller.ui.splash.SplashViewModel

fun viewModelModule() = module {
    viewModel { SplashViewModel(get()) }
    viewModel { SignUpViewModel(get(), get(), get()) }
    viewModel { LogInViewModel(get()) }
    viewModel { ProductDetailsViewModel(get(), get(), get()) }
    viewModel { OtpViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { AccountViewModel(get()) }
    viewModel { CategoryViewModel(get(), get(), get()) }
    viewModel { CartViewModel(get()) }
    viewModel { AddressViewModel(get()) }
    viewModel { OrderViewModel(get(), get()) }
    viewModel { OrderDetailsViewModel(get())}
    viewModel { ListOrdersViewModel(get()) }
    viewModel { ProductViewModel(get(), get()) }
    viewModel { SaleReportViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { ChartViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { ListReviewViewModel(get()) }
    viewModel { ListReviewOfProductViewModel(get()) }

}