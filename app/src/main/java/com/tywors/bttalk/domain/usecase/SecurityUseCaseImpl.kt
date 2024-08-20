package com.tywors.bttalk.domain.usecase

import android.content.Context
import com.centerprime.tronsdk.sdk.TronWalletManager
import com.tywors.bttalk.repository.room.dao.WalletDao
import com.tywors.bttalk.repository.room.entities.WalletEntity
import kotlinx.coroutines.rx2.await
import java.security.SecureRandom

class SecurityUseCaseImpl(
    private val context: Context,
) : SecurityUseCase {

    override suspend fun generateRandomKey(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val secureRandom = SecureRandom()
        val stringBuilder = StringBuilder(length)

        for (i in 0 until length) {
            val randomIndex = secureRandom.nextInt(chars.length)
            stringBuilder.append(chars[randomIndex])
        }

        return stringBuilder.toString()
    }
}