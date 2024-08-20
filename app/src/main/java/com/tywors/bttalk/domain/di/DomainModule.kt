package com.tywors.bttalk.domain.di

import com.tywors.bttalk.domain.usecase.ContactUseCase
import com.tywors.bttalk.domain.usecase.ContactUseCaseImpl
import com.tywors.bttalk.domain.usecase.SecurityUseCase
import com.tywors.bttalk.domain.usecase.SecurityUseCaseImpl
import com.tywors.bttalk.domain.usecase.WalletUseCase
import com.tywors.bttalk.domain.usecase.WalletUseCaseImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val domainModule = module {
    single<WalletUseCase> { WalletUseCaseImpl(get(), androidApplication()) }
    single<ContactUseCase> { ContactUseCaseImpl(get(), androidApplication()) }
    single<SecurityUseCase> { SecurityUseCaseImpl(androidApplication()) }
}