package vn.ztech.software.ecomSeller.di

fun allModule() = listOf(
    applicationModule(),
    viewModelModule(),
    useCaseModule(),
    repositoryModule(),
    networkModule(),
    apiModule()
)