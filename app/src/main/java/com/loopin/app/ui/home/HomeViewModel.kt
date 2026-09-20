package com.loopin.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.loopin.app.BuildConfig
import com.loopin.app.data.repository.SubscriptionRepository
import com.loopin.app.domain.logic.AlertSelector
import com.loopin.app.domain.logic.IndianCurrencyFormatter
import com.loopin.app.domain.logic.MonthlySpendCalculator
import com.loopin.app.domain.logic.RecentSubscriptionsSelector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime

class HomeViewModel(
    private val repository: SubscriptionRepository,
    private val autoSeed: Boolean = BuildConfig.DEBUG
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // In debug mode, seed realistic sample data on first start if repository is empty
            if (autoSeed && repository.getSubscriptionCount() == 0) {
                repository.seedSampleData()
            }

            repository.getSubscriptionsFlow().collectLatest { subscriptions ->
                val spendSummary = MonthlySpendCalculator.calculateSummary(subscriptions)
                val alerts = AlertSelector.selectAlerts(subscriptions)
                val recent = RecentSubscriptionsSelector.selectRecent(subscriptions, limit = 3)

                val formattedMonthly = formatAmountWithSpace(spendSummary.monthlySpendMinor)
                val formattedAnnual = "${formatAmountWithSpace(spendSummary.annualSpendMinor)} per year"

                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        greeting = getGreetingForTime(),
                        monthlySpendMinor = spendSummary.monthlySpendMinor,
                        formattedMonthlySpend = formattedMonthly,
                        activeSubscriptionsCount = spendSummary.activeSubscriptionCount,
                        annualProjectedMinor = spendSummary.annualSpendMinor,
                        formattedAnnualSpend = formattedAnnual,
                        upcomingAlerts = alerts,
                        recentSubscriptions = recent,
                        totalSubscriptionsCount = subscriptions.size
                    )
                }
            }
        }
    }

    fun toggleAlertsExpanded() {
        _uiState.update { it.copy(isAlertsExpanded = !it.isAlertsExpanded) }
    }

    fun seedSampleData() {
        viewModelScope.launch {
            repository.seedSampleData()
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    private fun formatAmountWithSpace(amountMinor: Long): String {
        val formatted = IndianCurrencyFormatter.format(amountMinor)
        return if (formatted.startsWith("₹")) {
            "₹ " + formatted.substring(1)
        } else {
            formatted
        }
    }

    private fun getGreetingForTime(): String {
        return when (LocalTime.now().hour) {
            in 5..11 -> "Good Morning!"
            in 12..16 -> "Good Afternoon!"
            in 17..21 -> "Good Evening!"
            else -> "Good Night!"
        }
    }

    companion object {
        fun provideFactory(
            repository: SubscriptionRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(repository) as T
            }
        }
    }
}
