package vn.ztech.software.ecomSeller.di

import org.koin.dsl.module
import vn.ztech.software.ecomSeller.repository.*

fun repositoryModule() = module {
    single<IProductRepository> {ProductRepository(get())}
    single<IAuthRepository> {AuthRepository(get(), get())}
    single<ICategoryRepository> {CategoryRepository(get())}
    single<ICartRepository> {CartRepository(get())}
    single<IAddressRepository> { AddressRepository(get()) }
    single<IShippingRepository> { ShippingRepository(get()) }
    single<IOrderRepository> { OrderRepository(get()) }

}