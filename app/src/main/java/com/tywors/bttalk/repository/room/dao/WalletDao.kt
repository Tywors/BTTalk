package com.tywors.bttalk.repository.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tywors.bttalk.repository.room.entities.WalletEntity

@Dao
interface WalletDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewWallet(wallet: WalletEntity): Long

    @Query("SELECT address FROM wallet_table")
    suspend fun getAddressWallet(): String?

}