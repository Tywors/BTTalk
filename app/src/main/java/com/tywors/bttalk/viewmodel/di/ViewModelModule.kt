package com.tywors.bttalk.viewmodel.di

import com.tywors.bttalk.viewmodel.CreateWalletViewModel
import com.tywors.bttalk.viewmodel.HomeViewModel
import com.tywors.bttalk.viewmodel.MainActivityViewModel
import com.tywors.bttalk.viewmodel.SplashViewModel
import com.tywors.bttalk.viewmodel.WelcomeViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { WelcomeViewModel(androidApplication()) }
    viewModel { MainActivityViewModel(androidApplication()) }
    viewModel { CreateWalletViewModel(get(), androidApplication()) }
    viewModel { HomeViewModel(get(), androidApplication()) }
    viewModel { SplashViewModel(get(), androidApplication()) }
}