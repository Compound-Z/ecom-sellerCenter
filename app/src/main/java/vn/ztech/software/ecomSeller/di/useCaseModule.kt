package vn.ztech.software.ecomSeller.di

import org.koin.dsl.module
import vn.ztech.software.ecomSeller.domain.use_case.get_product_details.IProductDetailsUseCase
import vn.ztech.software.ecomSeller.domain.use_case.get_product_details.ProductDetailsUseCase
import vn.ztech.software.ecomSeller.ui.account.AccountUseCase
import vn.ztech.software.ecomSeller.ui.account.IAccountUseCase
import vn.ztech.software.ecomSeller.ui.account.review.IReviewUseCase
import vn.ztech.software.ecomSeller.ui.account.review.ReviewUseCase
import vn.ztech.software.ecomSeller.ui.address.AddressUseCase
import vn.ztech.software.ecomSeller.ui.address.IAddressUseCase
import vn.ztech.software.ecomSeller.ui.auth.forgot_password.IResetPasswordUseCase
import vn.ztech.software.ecomSeller.ui.auth.forgot_password.ResetPasswordUseCase
import vn.ztech.software.ecomSeller.ui.auth.login.ILogInUseCase
import vn.ztech.software.ecomSeller.ui.auth.login.LogInUseCase
import vn.ztech.software.ecomSeller.ui.auth.otp.IOtpUseCase
import vn.ztech.software.ecomSeller.ui.auth.otp.OtpUseCase
import vn.ztech.software.ecomSeller.ui.auth.signup.ISignUpUseCase
import vn.ztech.software.ecomSeller.ui.auth.signup.SignUpUseCase
import vn.ztech.software.ecomSeller.ui.cart.CartUseCase
import vn.ztech.software.ecomSeller.ui.cart.ICartUseCase
import vn.ztech.software.ecomSeller.ui.category.IListCategoriesUseCase
import vn.ztech.software.ecomSeller.ui.category.ListCategoriesUseCase
import vn.ztech.software.ecomSeller.ui.main.IMainUseCase
import vn.ztech.software.ecomSeller.ui.main.MainUseCase
import vn.ztech.software.ecomSeller.ui.order.IOrderUserCase
import vn.ztech.software.ecomSeller.ui.order.order.IShippingUserCase
import vn.ztech.software.ecomSeller.ui.order.OrderUseCase
import vn.ztech.software.ecomSeller.ui.order.order.ShippingUseCase
import vn.ztech.software.ecomSeller.ui.product.IListProductUseCase
import vn.ztech.software.ecomSeller.ui.product.ListProductsUseCase
import vn.ztech.software.ecomSeller.ui.splash.ISplashUseCase
import vn.ztech.software.ecomSeller.ui.splash.SplashUseCase

fun useCaseModule() = module {
    factory<ISplashUseCase> { SplashUseCase(get(), get()) }
    factory<ISignUpUseCase> { SignUpUseCase(get()) }
    factory<ILogInUseCase> { LogInUseCase(get(), get()) }
    factory<IResetPasswordUseCase> { ResetPasswordUseCase(get())}
    factory<IListProductUseCase> { ListProductsUseCase(get()) }
    factory<IProductDetailsUseCase> { ProductDetailsUseCase(get()) }
    factory<IOtpUseCase> { OtpUseCase(get()) }
    factory<IAccountUseCase> { AccountUseCase(get(), get()) }
    factory<IListCategoriesUseCase> { ListCategoriesUseCase(get()) }
    factory<ICartUseCase>{CartUseCase(get())}
    factory<IAddressUseCase> { AddressUseCase(get()) }
    factory<IShippingUserCase> { ShippingUseCase(get()) }
    factory<IOrderUserCase> { OrderUseCase(get()) }
    factory<IMainUseCase> { MainUseCase(get()) }
    factory<IReviewUseCase> { ReviewUseCase(get()) }

}