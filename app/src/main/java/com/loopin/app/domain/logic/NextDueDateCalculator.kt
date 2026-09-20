package com.loopin.app.domain.logic

import com.loopin.app.domain.model.BillingCycle
import java.time.LocalDate
import java.time.YearMonth

/**
 * Pure domain logic for calculating subscription renewal dates.
 * Correctly handles:
 * - Month-end dates (29th, 30th, 31st).
 * - Leap year transitions (e.g. Feb 29 -> Feb 28 on non-leap years).
 * - Original day-of-month preservation (e.g. a Jan 31 subscription advances to Feb 28/29, then Mar 31).
 */
object NextDueDateCalculator {

    /**
     * Calculates the next due date based on billing cycle, preserving the original day of month
     * when cycling across months of varying lengths.
     */
    fun calculateNextDueDate(
        currentDueDate: LocalDate,
        cycle: BillingCycle,
        targetDayOfMonth: Int = currentDueDate.dayOfMonth
    ): LocalDate {
        return when (cycle) {
            BillingCycle.WEEKLY -> currentDueDate.plusWeeks(1)
            BillingCycle.MONTHLY -> advanceWithTargetDay(currentDueDate, months = 1, targetDayOfMonth)
            BillingCycle.QUARTERLY -> advanceWithTargetDay(currentDueDate, months = 3, targetDayOfMonth)
            BillingCycle.HALF_YEARLY -> advanceWithTargetDay(currentDueDate, months = 6, targetDayOfMonth)
            BillingCycle.YEARLY -> {
                // Java time automatically adjusts Feb 29 in leap year to Feb 28 in non-leap year
                currentDueDate.plusYears(1)
            }
        }
    }

    /**
     * Advances by [months], clamping to the target day of month or the maximum day in that month.
     */
    private fun advanceWithTargetDay(
        startDate: LocalDate,
        months: Long,
        targetDay: Int
    ): LocalDate {
        val targetYearMonth = YearMonth.from(startDate).plusMonths(months)
        val maxDaysInTargetMonth = targetYearMonth.lengthOfMonth()
        val clampedDay = targetDay.coerceAtMost(maxDaysInTargetMonth)
        return targetYearMonth.atDay(clampedDay)
    }
}
