package com.loopin.app.di

import android.content.Context
import com.loopin.app.data.db.LoopInDatabase
import com.loopin.app.data.repository.SubscriptionRepository
import com.loopin.app.data.repository.SubscriptionRepositoryImpl

interface AppContainer {
    val subscriptionRepository: SubscriptionRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val database: LoopInDatabase by lazy {
        LoopInDatabase.getInstance(context)
    }

    override val subscriptionRepository: SubscriptionRepository by lazy {
        SubscriptionRepositoryImpl(database.subscriptionDao())
    }
}
