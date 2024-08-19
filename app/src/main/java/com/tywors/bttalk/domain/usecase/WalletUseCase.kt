package com.tywors.bttalk.domain.usecase

import android.content.Context

interface WalletUseCase {

    suspend fun createWallet(password: String): Long
}