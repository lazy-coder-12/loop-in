package com.loopin.app.detection

import com.loopin.app.detection.model.BankIssuer
import com.loopin.app.detection.parser.RbiNotificationParser
import com.loopin.app.detection.samples.SampleDebitAlerts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class RbiNotificationParserTest {

    private val testReferenceDate: LocalDate = LocalDate.of(2024, 9, 25)

    @Test
    fun `parseNotification parses HDFC Bank Netflix pre-debit alert`() {
        val hdfcSample = SampleDebitAlerts.samples.first { it.id == "hdfc_netflix" }
        val parsed = RbiNotificationParser.parseNotification(
            packageName = hdfcSample.packageName,
            title = hdfcSample.title,
            text = hdfcSample.body,
            referenceDate = testReferenceDate
        )

        assertNotNull("Alert should be successfully parsed", parsed)
        assertEquals("Netflix", parsed!!.matchedServiceName)
        assertEquals(64900L, parsed.amountMinor)
        assertEquals("₹649", parsed.formattedAmount)
        assertEquals(LocalDate.of(2024, 9, 26), parsed.debitDate)
        assertEquals(BankIssuer.HDFC, parsed.bankIssuer)
        assertEquals("ending 1089", parsed.accountOrCardMask)
        assertEquals("UMRN882194", parsed.mandateRef)
        assertEquals("Premium", parsed.matchedPlanName?.take(7))
    }

    @Test
    fun `parseNotification parses ICICI Bank Spotify standing instruction`() {
        val iciciSample = SampleDebitAlerts.samples.first { it.id == "icici_spotify" }
        val parsed = RbiNotificationParser.parseNotification(
            packageName = iciciSample.packageName,
            title = iciciSample.title,
            text = iciciSample.body,
            referenceDate = testReferenceDate
        )

        assertNotNull(parsed)
        assertEquals("Spotify", parsed!!.matchedServiceName)
        assertEquals(49900L, parsed.amountMinor)
        assertEquals(LocalDate.of(2024, 9, 28), parsed.debitDate)
        assertEquals(BankIssuer.ICICI, parsed.bankIssuer)
        assertEquals("ending 4455", parsed.accountOrCardMask)
    }

    @Test
    fun `parseNotification parses SBI Disney+ Hotstar e-mandate`() {
        val sbiSample = SampleDebitAlerts.samples.first { it.id == "sbi_hotstar" }
        val parsed = RbiNotificationParser.parseNotification(
            packageName = sbiSample.packageName,
            title = sbiSample.title,
            text = sbiSample.body,
            referenceDate = testReferenceDate
        )

        assertNotNull(parsed)
        assertEquals("Jio Hotstar", parsed!!.matchedServiceName)
        assertEquals(49900L, parsed.amountMinor)
        assertEquals(LocalDate.of(2024, 9, 27), parsed.debitDate)
        assertEquals(BankIssuer.SBI, parsed.bankIssuer)
        assertEquals("ending 9876", parsed.accountOrCardMask)
        assertEquals("Super Mobile", parsed.matchedPlanName)
    }

    @Test
    fun `parseNotification parses Axis Bank LinkedIn pre-debit with comma amount`() {
        val axisSample = SampleDebitAlerts.samples.first { it.id == "axis_linkedin" }
        val parsed = RbiNotificationParser.parseNotification(
            packageName = axisSample.packageName,
            title = axisSample.title,
            text = axisSample.body,
            referenceDate = testReferenceDate
        )

        assertNotNull(parsed)
        assertEquals("Linkedin", parsed!!.matchedServiceName)
        assertEquals(149900L, parsed.amountMinor)
        assertEquals("₹1,499", parsed.formattedAmount)
        assertEquals(LocalDate.of(2024, 9, 29), parsed.debitDate)
        assertEquals(BankIssuer.AXIS, parsed.bankIssuer)
        assertEquals("XX8899", parsed.accountOrCardMask)
        assertEquals("AXIS8821", parsed.mandateRef)
    }

    @Test
    fun `parseNotification parses Google Pay AutoPay for Figma with tomorrow date`() {
        val gpaySample = SampleDebitAlerts.samples.first { it.id == "gpay_figma" }
        val parsed = RbiNotificationParser.parseNotification(
            packageName = gpaySample.packageName,
            title = gpaySample.title,
            text = gpaySample.body,
            referenceDate = testReferenceDate
        )

        assertNotNull(parsed)
        assertEquals("Figma", parsed!!.matchedServiceName)
        assertEquals(250000L, parsed.amountMinor)
        assertEquals(testReferenceDate.plusDays(1), parsed.debitDate)
        assertEquals(BankIssuer.GPAY, parsed.bankIssuer)
        assertEquals("Organization", parsed.matchedPlanName)
    }

    @Test
    fun `parseNotification parses Kotak Claude AI pre-debit`() {
        val kotakSample = SampleDebitAlerts.samples.first { it.id == "kotak_claude" }
        val parsed = RbiNotificationParser.parseNotification(
            packageName = kotakSample.packageName,
            title = kotakSample.title,
            text = kotakSample.body,
            referenceDate = testReferenceDate
        )

        assertNotNull(parsed)
        assertEquals("Claude AI", parsed!!.matchedServiceName)
        assertEquals(49900L, parsed.amountMinor)
        assertEquals(LocalDate.of(2024, 9, 30), parsed.debitDate)
        assertEquals(BankIssuer.KOTAK, parsed.bankIssuer)
    }

    @Test
    fun `parseNotification discards OTP notifications immediately`() {
        val otpMessage = "Your OTP is 492019 for transaction of Rs 499.00 at Netflix. Do not share with anyone."
        val parsed = RbiNotificationParser.parseNotification(
            packageName = "com.snapwork.hdfc",
            title = "HDFC Bank OTP",
            text = otpMessage,
            referenceDate = testReferenceDate
        )

        assertNull("OTP notifications must never be parsed as pre-debit alerts", parsed)
        assertFalse(RbiNotificationParser.isPreDebitNotification("HDFC Bank OTP", otpMessage))
    }

    @Test
    fun `parseNotification discards ATM withdrawals`() {
        val atmMessage = "Cash of Rs 5000.00 withdrawn at ATM from your A/c XX1234 on 24-Sep-2024."
        val parsed = RbiNotificationParser.parseNotification(
            packageName = "com.snapwork.hdfc",
            title = "ATM Withdrawal",
            text = atmMessage,
            referenceDate = testReferenceDate
        )

        assertNull("ATM cash withdrawals must be discarded", parsed)
        assertFalse(RbiNotificationParser.isPreDebitNotification("ATM Withdrawal", atmMessage))
    }

    @Test
    fun `parseNotification discards marketing and promotional spam`() {
        val promoMessage = "Get 50% cashback on your next purchase! Apply now for instant personal loan up to Rs 5 Lakhs."
        val parsed = RbiNotificationParser.parseNotification(
            packageName = "com.snapwork.hdfc",
            title = "Special Offer For You!",
            text = promoMessage,
            referenceDate = testReferenceDate
        )

        assertNull(parsed)
    }
}
