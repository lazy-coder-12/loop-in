package com.loopin.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.loopin.app.domain.model.BillingCycle
import com.loopin.app.domain.model.Subscription
import com.loopin.app.domain.model.SubscriptionSource
import com.loopin.app.domain.model.SubscriptionStatus
import java.time.LocalDate

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val amountMinor: Long,
    val billingCycle: BillingCycle,
    val nextDueDate: LocalDate,
    val status: SubscriptionStatus,
    val source: SubscriptionSource,
    val category: String,
    val isTrial: Boolean = false,
    val trialEndDate: LocalDate? = null,
    val amountAfterTrialMinor: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

fun SubscriptionEntity.toDomainModel(): Subscription {
    return Subscription(
        id = id,
        name = name,
        amountMinor = amountMinor,
        billingCycle = billingCycle,
        nextDueDate = nextDueDate,
        status = status,
        source = source,
        category = category,
        isTrial = isTrial,
        trialEndDate = trialEndDate,
        amountAfterTrialMinor = amountAfterTrialMinor,
        createdAt = createdAt
    )
}

fun Subscription.toEntity(): SubscriptionEntity {
    return SubscriptionEntity(
        id = id,
        name = name,
        amountMinor = amountMinor,
        billingCycle = billingCycle,
        nextDueDate = nextDueDate,
        status = status,
        source = source,
        category = category,
        isTrial = isTrial,
        trialEndDate = trialEndDate,
        amountAfterTrialMinor = amountAfterTrialMinor,
        createdAt = createdAt
    )
}
