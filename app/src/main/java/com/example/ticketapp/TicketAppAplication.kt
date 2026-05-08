package com.example.ticketapp

import android.app.Application
import com.example.data.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


//uygulama başladığında activitylerden önce oluşturulur
//Singleton (tek bir instance olarak memoryde kalır)
//uygulama kapanana kadar yok edilmez
class TicketAppApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidLogger()
            androidContext(this@TicketAppApplication)
            modules(
                dataModule
            )
        }
    }
}