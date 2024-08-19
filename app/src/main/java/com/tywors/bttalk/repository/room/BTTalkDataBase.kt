package com.tywors.bttalk.repository.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tywors.bttalk.repository.room.dao.WalletDao
import com.tywors.bttalk.repository.room.entities.WalletEntity

@Database(
    entities = [
        WalletEntity::class
    ],
    version = 1,
    exportSchema = true,
)

@TypeConverters()
abstract class BTTalkDataBase : RoomDatabase() {
    abstract fun getWalletDao(): WalletDao
}
