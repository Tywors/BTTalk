package com.tywors.bttalk.repository.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tywors.bttalk.repository.room.entities.ContactEntity
import com.tywors.bttalk.repository.room.entities.WalletEntity

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemporalContact(contact: ContactEntity): Long
}