package com.loopin.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {

    @Query("SELECT * FROM subscriptions ORDER BY createdAt DESC")
    fun getAllSubscriptionsFlow(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions WHERE id = :id LIMIT 1")
    fun getSubscriptionByIdFlow(id: Long): Flow<SubscriptionEntity?>

    @Query("SELECT * FROM subscriptions WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun getSubscriptionByName(name: String): SubscriptionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(subscriptions: List<SubscriptionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subscription: SubscriptionEntity): Long

    @Query("DELETE FROM subscriptions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM subscriptions WHERE LOWER(name) LIKE LOWER(:namePattern)")
    suspend fun deleteByNamePattern(namePattern: String)

    @Query("DELETE FROM subscriptions")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM subscriptions")
    suspend fun getCount(): Int
}
