package com.tywors.bttalk.repository.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet_table")
data class WalletEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int? = null,
    @ColumnInfo(name = "address") val address: String? = null,
    @ColumnInfo(name = "password") val password: String? = null,
    @ColumnInfo(name = "privateKey") val privateKey: String? = null,
)