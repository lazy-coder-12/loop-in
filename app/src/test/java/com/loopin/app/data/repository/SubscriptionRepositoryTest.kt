package com.loopin.app.data.repository

import com.loopin.app.domain.model.BillingCycle
import com.loopin.app.domain.model.Subscription
import com.loopin.app.domain.model.SubscriptionSource
import com.loopin.app.domain.model.SubscriptionStatus
import com.loopin.app.ui.home.FakeSubscriptionRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class SubscriptionRepositoryTest {

    private lateinit var repository: FakeSubscriptionRepository

    @Before
    fun setUp() {
        repository = FakeSubscriptionRepository()
    }

    @Test
    fun `deleteSubscription removes item by id`() = runTest {
        val sub1 = Subscription(
            id = 101L,
            name = "Netflix Standard",
            category = "Entertainment",
            amountMinor = 49900L,
            billingCycle = BillingCycle.MONTHLY,
            nextDueDate = LocalDate.now().plusMonths(1),
            status = SubscriptionStatus.ACTIVE,
            source = SubscriptionSource.MANUAL
        )
        val sub2 = Subscription(
            id = 102L,
            name = "Spotify Premium",
            category = "Music",
            amountMinor = 11900L,
            billingCycle = BillingCycle.MONTHLY,
            nextDueDate = LocalDate.now().plusMonths(1),
            status = SubscriptionStatus.ACTIVE,
            source = SubscriptionSource.MANUAL
        )

        repository.insertSubscription(sub1)
        repository.insertSubscription(sub2)

        val beforeList = repository.getSubscriptionsFlow().first()
        assertEquals(2, beforeList.size)

        repository.deleteSubscription(101L)

        val afterList = repository.getSubscriptionsFlow().first()
        assertEquals(1, afterList.size)
        assertEquals(102L, afterList.first().id)
    }

    @Test
    fun `deleteSubscriptionByName removes items matching name pattern`() = runTest {
        val netflix = Subscription(
            id = 201L,
            name = "Netflix Premium",
            category = "Entertainment",
            amountMinor = 64900L,
            billingCycle = BillingCycle.MONTHLY,
            nextDueDate = LocalDate.now().plusMonths(1),
            status = SubscriptionStatus.ACTIVE,
            source = SubscriptionSource.MANUAL
        )
        val hotstar = Subscription(
            id = 202L,
            name = "Jio Hotstar Super",
            category = "Entertainment",
            amountMinor = 89900L,
            billingCycle = BillingCycle.YEARLY,
            nextDueDate = LocalDate.now().plusYears(1),
            status = SubscriptionStatus.ACTIVE,
            source = SubscriptionSource.MANUAL
        )

        repository.insertSubscription(netflix)
        repository.insertSubscription(hotstar)

        repository.deleteSubscriptionByName("Netflix")

        val list = repository.getSubscriptionsFlow().first()
        assertEquals(1, list.size)
        assertEquals("Jio Hotstar Super", list.first().name)
    }
}
