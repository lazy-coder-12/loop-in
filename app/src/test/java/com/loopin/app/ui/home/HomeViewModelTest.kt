package com.loopin.app.ui.home

import com.loopin.app.data.repository.SampleDataProvider
import com.loopin.app.data.repository.SubscriptionRepository
import com.loopin.app.domain.model.Subscription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeSubscriptionRepository : SubscriptionRepository {
    private val _flow = MutableStateFlow<List<Subscription>>(emptyList())

    fun emit(subscriptions: List<Subscription>) {
        _flow.value = subscriptions
    }

    override fun getSubscriptionsFlow(): Flow<List<Subscription>> = _flow.asStateFlow()

    override fun getSubscriptionById(id: Long): Flow<Subscription?> =
        _flow.map { list -> list.find { it.id == id } }

    override suspend fun getSubscriptionByName(name: String): Subscription? =
        _flow.value.find { it.name.equals(name, ignoreCase = true) }

    override suspend fun insertSubscription(subscription: Subscription): Long {
        _flow.value = _flow.value + subscription
        return subscription.id
    }

    override suspend fun deleteSubscription(id: Long) {
        _flow.value = _flow.value.filter { it.id != id }
    }

    override suspend fun deleteSubscriptionByName(name: String) {
        _flow.value = _flow.value.filterNot { it.name.contains(name, ignoreCase = true) }
    }

    override suspend fun seedSampleData() {
        _flow.value = SampleDataProvider.getSampleSubscriptions()
    }

    override suspend fun clearAllData() {
        _flow.value = emptyList()
    }

    override suspend fun getSubscriptionCount(): Int = _flow.value.size
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeSubscriptionRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeSubscriptionRepository()
        viewModel = HomeViewModel(fakeRepository, autoSeed = false)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state reflects empty repository`() = runTest(testDispatcher) {
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertEquals(0, state.activeSubscriptionsCount)
        assertEquals(0L, state.monthlySpendMinor)
        assertEquals("₹ 0", state.formattedMonthlySpend)
        assertTrue(state.isEmpty)
    }

    @Test
    fun `when subscriptions are emitted, uiState calculates monthly spend and alerts`() = runTest(testDispatcher) {
        val sampleSubs = SampleDataProvider.getSampleSubscriptions()
        fakeRepository.emit(sampleSubs)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isEmpty)
        assertTrue(state.activeSubscriptionsCount > 0)
        assertTrue(state.monthlySpendMinor > 0L)
        assertTrue(state.formattedMonthlySpend.startsWith("₹ "))
        assertTrue(state.upcomingAlerts.isNotEmpty())
        assertEquals(3, state.recentSubscriptions.size)
    }

    @Test
    fun `toggleAlertsExpanded toggles isAlertsExpanded flag`() = runTest(testDispatcher) {
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isAlertsExpanded)

        viewModel.toggleAlertsExpanded()
        assertTrue(viewModel.uiState.value.isAlertsExpanded)

        viewModel.toggleAlertsExpanded()
        assertFalse(viewModel.uiState.value.isAlertsExpanded)
    }
}
