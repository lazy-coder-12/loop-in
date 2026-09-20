package com.loopin.app.domain

import com.loopin.app.domain.logic.IndianCurrencyFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IndianCurrencyFormatterTest {

    @Test
    fun format_zeroAmount_returnsZeroWithoutDecimals() {
        val result = IndianCurrencyFormatter.format(0L)
        assertEquals("₹0", result)
    }

    @Test
    fun format_hundreds_formatsCorrectly() {
        assertEquals("₹649", IndianCurrencyFormatter.format(64900L))
        assertEquals("₹139", IndianCurrencyFormatter.format(13900L))
        assertEquals("₹999", IndianCurrencyFormatter.format(99900L))
    }

    @Test
    fun format_thousands_groupsLastThreeDigits() {
        assertEquals("₹1,000", IndianCurrencyFormatter.format(100000L))
        assertEquals("₹4,281", IndianCurrencyFormatter.format(428100L))
        assertEquals("₹9,999", IndianCurrencyFormatter.format(999900L))
    }

    @Test
    fun format_lakhs_groupsInTwosAfterThousands() {
        assertEquals("₹10,000", IndianCurrencyFormatter.format(1000000L))
        assertEquals("₹1,00,000", IndianCurrencyFormatter.format(10000000L))
        assertEquals("₹1,24,999", IndianCurrencyFormatter.format(12499900L))
        assertEquals("₹10,50,000", IndianCurrencyFormatter.format(105000000L))
        assertEquals("₹99,99,999", IndianCurrencyFormatter.format(999999900L))
    }

    @Test
    fun format_crores_groupsCorrectly() {
        assertEquals("₹1,00,00,000", IndianCurrencyFormatter.format(1000000000L))
        assertEquals("₹12,34,56,789", IndianCurrencyFormatter.format(12345678900L))
    }

    @Test
    fun format_withNonZeroPaise_rendersTwoDecimalDigits() {
        assertEquals("₹4,281.50", IndianCurrencyFormatter.format(428150L))
        assertEquals("₹2,912.07", IndianCurrencyFormatter.format(291207L))
        assertEquals("₹0.05", IndianCurrencyFormatter.format(5L))
        assertEquals("₹0.99", IndianCurrencyFormatter.format(99L))
    }

    @Test
    fun split_providesDecomposedParts() {
        val splitZero = IndianCurrencyFormatter.split(0L)
        assertEquals("0", splitZero.wholePart)
        assertEquals("", splitZero.decimalPart)
        assertFalse(splitZero.isNegative)

        val splitHero = IndianCurrencyFormatter.split(291207L)
        assertEquals("2,912", splitHero.wholePart)
        assertEquals(".07", splitHero.decimalPart)
        assertFalse(splitHero.isNegative)

        val splitNegative = IndianCurrencyFormatter.split(-64900L)
        assertEquals("649", splitNegative.wholePart)
        assertTrue(splitNegative.isNegative)
    }
}
