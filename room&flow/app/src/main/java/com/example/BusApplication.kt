package com.example

import android.app.Application
import com.example.busschedule.database.AppDatabase

class BusApplication: Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}
