package com.tywors.bttalk

import android.app.Application
import com.tywors.bttalk.domain.di.domainModule
import com.tywors.bttalk.repository.room.di.roomModule
import com.tywors.bttalk.viewmodel.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules (
                viewModelModule,
                domainModule,
                roomModule,
            )
        }
    }
}