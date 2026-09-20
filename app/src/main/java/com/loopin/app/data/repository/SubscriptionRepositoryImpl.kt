package com.loopin.app.data.repository

import com.loopin.app.data.db.SubscriptionDao
import com.loopin.app.data.db.toDomainModel
import com.loopin.app.data.db.toEntity
import com.loopin.app.domain.model.Subscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SubscriptionRepositoryImpl(
    private val subscriptionDao: SubscriptionDao
) : SubscriptionRepository {

    override fun getSubscriptionsFlow(): Flow<List<Subscription>> {
        return subscriptionDao.getAllSubscriptionsFlow().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getSubscriptionById(id: Long): Flow<Subscription?> {
        return subscriptionDao.getSubscriptionByIdFlow(id).map { entity ->
            entity?.toDomainModel()
        }
    }

    override suspend fun insertSubscription(subscription: Subscription): Long {
        return subscriptionDao.insert(subscription.toEntity())
    }

    override suspend fun deleteSubscription(id: Long) {
        subscriptionDao.deleteById(id)
    }

    override suspend fun deleteSubscriptionByName(name: String) {
        subscriptionDao.deleteByNamePattern("%$name%")
    }

    override suspend fun seedSampleData() {
        subscriptionDao.deleteAll()
        val sampleSubs = SampleDataProvider.getSampleSubscriptions()
        subscriptionDao.insertAll(sampleSubs.map { it.toEntity() })
    }

    override suspend fun clearAllData() {
        subscriptionDao.deleteAll()
    }

    override suspend fun getSubscriptionCount(): Int {
        return subscriptionDao.getCount()
    }
}
