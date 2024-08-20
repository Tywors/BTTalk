package com.tywors.bttalk.repository.room.di

import android.content.Context
import androidx.room.Room
import com.tywors.bttalk.repository.room.BTTalkDataBase
import com.tywors.bttalk.repository.room.dao.ContactDao
import com.tywors.bttalk.repository.room.dao.WalletDao
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

const val nameDataBase = "BTTalkDataBase"

val roomModule = module {
    fun provideDataBase(context: Context): BTTalkDataBase {
        return Room.databaseBuilder(context, BTTalkDataBase::class.java, nameDataBase)
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideWalletDao(database: BTTalkDataBase): WalletDao {
        return database.getWalletDao()
    }

    fun provideContactDao(database: BTTalkDataBase): ContactDao {
        return database.getContactDao()
    }

    single { provideDataBase(androidApplication()) }
    single { provideWalletDao(get()) }
    single { provideContactDao(get()) }
}