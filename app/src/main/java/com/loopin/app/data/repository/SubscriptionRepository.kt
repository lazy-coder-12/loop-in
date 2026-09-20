package com.loopin.app.data.repository

import com.loopin.app.domain.model.Subscription
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    fun getSubscriptionsFlow(): Flow<List<Subscription>>
    fun getSubscriptionById(id: Long): Flow<Subscription?>
    suspend fun insertSubscription(subscription: Subscription): Long
    suspend fun deleteSubscription(id: Long)
    suspend fun deleteSubscriptionByName(name: String)
    suspend fun seedSampleData()
    suspend fun clearAllData()
    suspend fun getSubscriptionCount(): Int
}
