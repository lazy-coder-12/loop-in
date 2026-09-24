package com.loopin.app.detection.samples

import com.loopin.app.detection.model.BankIssuer

data class SampleDebitAlert(
    val id: String,
    val issuer: BankIssuer,
    val title: String,
    val body: String,
    val packageName: String,
    val expectedService: String,
    val expectedAmountMinor: Long
)

object SampleDebitAlerts {

    val samples: List<SampleDebitAlert> = listOf(
        SampleDebitAlert(
            id = "hdfc_netflix",
            issuer = BankIssuer.HDFC,
            title = "HDFC Bank Alert: Pre-debit notification",
            body = "Dear Customer, pre-debit alert: Rs 649.00 will be debited from your HDFC Bank Card ending 1089 towards NETFLIX ENTERTAINMENT on 26-Sep-2024. Mandate Ref: UMRN882194.",
            packageName = "com.snapwork.hdfc",
            expectedService = "Netflix",
            expectedAmountMinor = 64900L
        ),
        SampleDebitAlert(
            id = "icici_spotify",
            issuer = BankIssuer.ICICI,
            title = "ICICI Bank: Standing Instruction Alert",
            body = "Dear Customer, your standing instruction for INR 499.00 towards SPOTIFY is scheduled for debit on 28-09-2024 from ICICI Bank Card ending 4455.",
            packageName = "com.csam.icici.bank.imobile",
            expectedService = "Spotify",
            expectedAmountMinor = 49900L
        ),
        SampleDebitAlert(
            id = "sbi_hotstar",
            issuer = BankIssuer.SBI,
            title = "SBI Card: e-Mandate Pre-Debit Alert",
            body = "Dear SBI Cardholder, pre-debit alert: Rs. 499.00 will be debited for DISNEY+ HOTSTAR on 27-Sep-2024 on your SBI Credit Card ending 9876.",
            packageName = "com.sbicard.app",
            expectedService = "Jio Hotstar",
            expectedAmountMinor = 49900L
        ),
        SampleDebitAlert(
            id = "axis_linkedin",
            issuer = BankIssuer.AXIS,
            title = "Axis Bank Pre-Debit Alert",
            body = "Axis Bank: Auto-debit of INR 1,499.00 towards LINKEDIN is scheduled on 29-Sep-2024 from A/c XX8899. Mandate Ref: AXIS8821.",
            packageName = "com.axis.mobile",
            expectedService = "Linkedin",
            expectedAmountMinor = 149900L
        ),
        SampleDebitAlert(
            id = "gpay_figma",
            issuer = BankIssuer.GPAY,
            title = "Google Pay: AutoPay scheduled",
            body = "Upcoming AutoPay of ₹ 2,500.00 for Figma is scheduled to be debited tomorrow. Mandate ID: UPI994411.",
            packageName = "com.google.android.apps.nbu.paisa.user",
            expectedService = "Figma",
            expectedAmountMinor = 250000L
        ),
        SampleDebitAlert(
            id = "kotak_claude",
            issuer = BankIssuer.KOTAK,
            title = "Kotak Bank: SI Pre-Debit Notice",
            body = "Pre-debit notice: Rs 499.00 will be auto-debited on 30-Sep-2024 towards CLAUDE AI from Kotak Debit Card ending 3321.",
            packageName = "com.msf.kbank.mobile",
            expectedService = "Claude AI",
            expectedAmountMinor = 49900L
        )
    )
}
