package com.loopin.app

import android.app.Application
import com.loopin.app.di.AppContainer
import com.loopin.app.di.DefaultAppContainer

class LoopInApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
