package com.tywors.bttalk.domain.usecase

interface WalletUseCase {

    suspend fun createWallet(password: String): Long
    suspend fun loadAddressWallet(): String?
}