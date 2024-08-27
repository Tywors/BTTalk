package com.tywors.bttalk.repository.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact_table")
data class ContactEntity (
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String = "",
    @ColumnInfo(name = "ownKey") val ownKey: String? = null,
    @ColumnInfo(name = "address") val address: String? = null,
    @ColumnInfo(name = "remoteKey") val remoteKey: String? = null,
    @ColumnInfo(name = "completed") val completed: Boolean? = null,
)