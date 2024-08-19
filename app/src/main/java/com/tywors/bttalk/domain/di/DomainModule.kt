package com.tywors.bttalk.domain.di

import com.tywors.bttalk.domain.usecase.WalletUseCase
import com.tywors.bttalk.domain.usecase.WalletUseCaseImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val domainModule = module {
    single<WalletUseCase> { WalletUseCaseImpl(get(), androidApplication()) }
}