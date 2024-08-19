package com.tywors.bttalk.domain.usecase

import android.content.Context
import com.centerprime.tronsdk.sdk.TronWalletManager
import com.tywors.bttalk.repository.room.dao.WalletDao
import com.tywors.bttalk.repository.room.entities.WalletEntity
import kotlinx.coroutines.rx2.await

class WalletUseCaseImpl(
    private val walletDao: WalletDao,
    private val context: Context,
) : WalletUseCase {

    override suspend fun createWallet(password: String): Long {
        val tronWalletManager = TronWalletManager.getInstance()
        val wallet = tronWalletManager.createWallet(password, context).await()

        val walletAddress = wallet.address.toString()
        val walletPrivateKey = tronWalletManager.exportPrivateKey(walletAddress, password, context).await()

        return walletDao.insertNewWallet(
            WalletEntity(
                id = 1,
                address = walletAddress,
                password = password,
                privateKey = walletPrivateKey
            )
        )
    }
}