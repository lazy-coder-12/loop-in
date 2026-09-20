package com.loopin.app.domain

import com.loopin.app.domain.logic.NextDueDateCalculator
import com.loopin.app.domain.model.BillingCycle
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class NextDueDateCalculatorTest {

    @Test
    fun calculateNextDueDate_weekly_advancesSevenDays() {
        val start = LocalDate.of(2026, 9, 20)
        val next = NextDueDateCalculator.calculateNextDueDate(start, BillingCycle.WEEKLY)
        assertEquals(LocalDate.of(2026, 9, 27), next)
    }

    @Test
    fun calculateNextDueDate_monthlyStandard_advancesOneMonth() {
        val start = LocalDate.of(2026, 9, 15)
        val next = NextDueDateCalculator.calculateNextDueDate(start, BillingCycle.MONTHLY)
        assertEquals(LocalDate.of(2026, 10, 15), next)
    }

    @Test
    fun calculateNextDueDate_monthEndJanuaryToFebruary_clampsTo28thInNonLeapYear() {
        // Jan 31, 2025 (non leap) -> Feb 28, 2025
        val start = LocalDate.of(2025, 1, 31)
        val next = NextDueDateCalculator.calculateNextDueDate(start, BillingCycle.MONTHLY, targetDayOfMonth = 31)
        assertEquals(LocalDate.of(2025, 2, 28), next)
    }

    @Test
    fun calculateNextDueDate_monthEndJanuaryToFebruary_clampsTo29thInLeapYear() {
        // Jan 31, 2024 (leap year) -> Feb 29, 2024
        val start = LocalDate.of(2024, 1, 31)
        val next = NextDueDateCalculator.calculateNextDueDate(start, BillingCycle.MONTHLY, targetDayOfMonth = 31)
        assertEquals(LocalDate.of(2024, 2, 29), next)
    }

    @Test
    fun calculateNextDueDate_quarterly_advancesThreeMonths() {
        val start = LocalDate.of(2026, 1, 15)
        val next = NextDueDateCalculator.calculateNextDueDate(start, BillingCycle.QUARTERLY)
        assertEquals(LocalDate.of(2026, 4, 15), next)
    }

    @Test
    fun calculateNextDueDate_yearlyLeapYear_advancesToFeb28OnNonLeapYear() {
        // Feb 29, 2024 (leap) -> Feb 28, 2025 (non-leap)
        val start = LocalDate.of(2024, 2, 29)
        val next = NextDueDateCalculator.calculateNextDueDate(start, BillingCycle.YEARLY)
        assertEquals(LocalDate.of(2025, 2, 28), next)
    }
}
