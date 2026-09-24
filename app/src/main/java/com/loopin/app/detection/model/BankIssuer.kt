package com.loopin.app.detection.model

/**
 * Recognizes common Indian banks, payment apps, and card issuers
 * that issue RBI-mandated pre-debit / e-mandate notifications.
 */
enum class BankIssuer(
    val displayName: String,
    val identifierKeywords: List<String>,
    val packageNames: List<String>
) {
    HDFC(
        displayName = "HDFC Bank",
        identifierKeywords = listOf("hdfc", "hdfcbk", "payzapp"),
        packageNames = listOf("com.snapwork.hdfc", "com.hdfc.payzapp")
    ),
    ICICI(
        displayName = "ICICI Bank",
        identifierKeywords = listOf("icici", "icicibk", "imobile"),
        packageNames = listOf("com.csam.icici.bank.imobile")
    ),
    SBI(
        displayName = "State Bank of India",
        identifierKeywords = listOf("sbi", "sbicard", "state bank"),
        packageNames = listOf("com.sbi.lotusintouch", "com.sbicard.app")
    ),
    AXIS(
        displayName = "Axis Bank",
        identifierKeywords = listOf("axis", "axisbk"),
        packageNames = listOf("com.axis.mobile")
    ),
    KOTAK(
        displayName = "Kotak Mahindra Bank",
        identifierKeywords = listOf("kotak"),
        packageNames = listOf("com.msf.kbank.mobile")
    ),
    PNB(
        displayName = "Punjab National Bank",
        identifierKeywords = listOf("pnb", "punjab national"),
        packageNames = listOf("com.pnb.one")
    ),
    BOB(
        displayName = "Bank of Baroda",
        identifierKeywords = listOf("baroda", "bob"),
        packageNames = listOf("com.bankofbaroda.mconnect")
    ),
    CANARA(
        displayName = "Canara Bank",
        identifierKeywords = listOf("canara"),
        packageNames = listOf("com.canarabank.ai1")
    ),
    INDUSIND(
        displayName = "IndusInd Bank",
        identifierKeywords = listOf("indusind"),
        packageNames = listOf("com.indusind")
    ),
    YES(
        displayName = "Yes Bank",
        identifierKeywords = listOf("yes bank", "yesbank"),
        packageNames = listOf("com.yesbank")
    ),
    AMEX(
        displayName = "American Express",
        identifierKeywords = listOf("amex", "american express"),
        packageNames = listOf("com.americanexpress.android.acctsvcs.us")
    ),
    CITI(
        displayName = "Citibank",
        identifierKeywords = listOf("citi", "citibank"),
        packageNames = listOf("com.citibank.mobile.in")
    ),
    RBL(
        displayName = "RBL Bank",
        identifierKeywords = listOf("rbl", "rblbank"),
        packageNames = listOf("com.rblbank.mobank")
    ),
    GPAY(
        displayName = "Google Pay",
        identifierKeywords = listOf("gpay", "google pay", "googlepay"),
        packageNames = listOf("com.google.android.apps.nbu.paisa.user")
    ),
    PHONEPE(
        displayName = "PhonePe",
        identifierKeywords = listOf("phonepe"),
        packageNames = listOf("com.phonepe.app")
    ),
    PAYTM(
        displayName = "Paytm",
        identifierKeywords = listOf("paytm"),
        packageNames = listOf("net.one97.paytm")
    ),
    CRED(
        displayName = "CRED",
        identifierKeywords = listOf("cred"),
        packageNames = listOf("com.dreamplug.androidapp")
    ),
    OTHER(
        displayName = "Bank / Issuer",
        identifierKeywords = emptyList(),
        packageNames = emptyList()
    );

    companion object {
        fun resolve(packageName: String, text: String): BankIssuer {
            val lowerPkg = packageName.lowercase()
            val lowerText = text.lowercase()

            for (issuer in entries) {
                if (issuer == OTHER) continue
                if (issuer.packageNames.any { lowerPkg.contains(it.lowercase()) }) {
                    return issuer
                }
                if (issuer.identifierKeywords.any { lowerText.contains(it) }) {
                    return issuer
                }
            }
            return OTHER
        }
    }
}
