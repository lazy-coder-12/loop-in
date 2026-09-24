package com.loopin.app.detection.matcher

import com.loopin.app.data.catalog.CatalogPlan
import com.loopin.app.data.catalog.CatalogService
import com.loopin.app.data.catalog.ServiceCatalogProvider

data class MerchantMatchResult(
    val serviceName: String,
    val catalogService: CatalogService?,
    val matchedPlan: CatalogPlan?,
    val category: String,
    val confidence: Float
)

object MerchantMatcher {

    private val aliasToCatalogId = mapOf(
        // Netflix
        "netflix" to "netflix",
        "nflx" to "netflix",
        "netflix entertainment" to "netflix",
        "netflix india" to "netflix",
        "netflix.com" to "netflix",

        // Hotstar
        "disney+ hotstar" to "hotstar",
        "disney hotstar" to "hotstar",
        "hotstar" to "hotstar",
        "jio hotstar" to "hotstar",
        "novi digital" to "hotstar",
        "star india" to "hotstar",

        // Spotify
        "spotify" to "spotify",
        "spotify india" to "spotify",
        "spotify ab" to "spotify",

        // Claude AI
        "claude" to "claude",
        "claude ai" to "claude",
        "claude pro" to "claude",
        "anthropic" to "claude",

        // Figma
        "figma" to "figma",
        "figma inc" to "figma",

        // LinkedIn
        "linkedin" to "linkedin",
        "linkedin ireland" to "linkedin",
        "linkedin corp" to "linkedin",
        "linkedin premium" to "linkedin"
    )

    private val commonKnownServices = mapOf(
        "youtube" to ("YouTube Premium" to "Entertainment"),
        "google youtube" to ("YouTube Premium" to "Entertainment"),
        "amazon prime" to ("Amazon Prime" to "Entertainment"),
        "amzn prime" to ("Amazon Prime" to "Entertainment"),
        "amazon digital" to ("Amazon Prime" to "Entertainment"),
        "prime video" to ("Amazon Prime" to "Entertainment"),
        "apple" to ("Apple Services" to "Productivity"),
        "apple.com/bill" to ("Apple Services" to "Productivity"),
        "itunes" to ("Apple Services" to "Productivity"),
        "microsoft" to ("Microsoft 365" to "Productivity"),
        "microsoft 365" to ("Microsoft 365" to "Productivity"),
        "msft" to ("Microsoft 365" to "Productivity"),
        "swiggy" to ("Swiggy One" to "Food & Lifestyle"),
        "swiggy one" to ("Swiggy One" to "Food & Lifestyle"),
        "bundl technologies" to ("Swiggy One" to "Food & Lifestyle"),
        "zomato" to ("Zomato Gold" to "Food & Lifestyle"),
        "zomato gold" to ("Zomato Gold" to "Food & Lifestyle"),
        "chatgpt" to ("OpenAI ChatGPT" to "Productivity"),
        "openai" to ("OpenAI ChatGPT" to "Productivity"),
        "github" to ("GitHub" to "Productivity"),
        "notion" to ("Notion" to "Productivity"),
        "google one" to ("Google One" to "Cloud Storage"),
        "jio cinema" to ("JioCinema" to "Entertainment"),
        "jiocinema" to ("JioCinema" to "Entertainment"),
        "sonyliv" to ("SonyLIV" to "Entertainment"),
        "sony liv" to ("SonyLIV" to "Entertainment"),
        "culver max" to ("SonyLIV" to "Entertainment"),
        "zee5" to ("Zee5" to "Entertainment"),
        "cult.fit" to ("Cult.fit" to "Fitness"),
        "cultfit" to ("Cult.fit" to "Fitness")
    )

    fun match(rawMerchant: String, amountMinor: Long): MerchantMatchResult {
        val cleanCandidate = cleanMerchantString(rawMerchant)
        val normalized = cleanCandidate.lowercase()

        // 1. Exact or alias match against ServiceCatalogProvider
        val catalogId = aliasToCatalogId[normalized]
            ?: aliasToCatalogId.entries.firstOrNull { normalized.contains(it.key) || it.key.contains(normalized) }?.value

        if (catalogId != null) {
            val catalogService = ServiceCatalogProvider.getServiceById(catalogId)
            if (catalogService != null) {
                // Check if any plan price matches
                val matchedPlan = catalogService.plans.find { it.priceMinor == amountMinor }
                return MerchantMatchResult(
                    serviceName = catalogService.name,
                    catalogService = catalogService,
                    matchedPlan = matchedPlan,
                    category = catalogService.category,
                    confidence = if (matchedPlan != null) 0.98f else 0.90f
                )
            }
        }

        // 2. Match against common popular subscription services
        val commonMatch = commonKnownServices.entries.firstOrNull {
            normalized.contains(it.key) || it.key.contains(normalized)
        }
        if (commonMatch != null) {
            val (name, cat) = commonMatch.value
            return MerchantMatchResult(
                serviceName = name,
                catalogService = null,
                matchedPlan = null,
                category = cat,
                confidence = 0.85f
            )
        }

        // 3. Fallback: Clean title-cased name
        val titleCased = toTitleCase(cleanCandidate)
        return MerchantMatchResult(
            serviceName = if (titleCased.isNotBlank()) titleCased else "Recurring Subscription",
            catalogService = null,
            matchedPlan = null,
            category = "Other",
            confidence = 0.5f
        )
    }

    private fun cleanMerchantString(raw: String): String {
        var s = raw.trim()
        // Strip common bank preposition prefixes if accidentally included
        s = s.replace(Regex("(?i)^(?:towards|for|to|at|with|merchant:?)\\s+"), "")
        // Strip legal entity, payment gateway, and filler suffixes
        s = s.replace(Regex("(?i)\\b(?:pvt\\.?|ltd\\.?|inc\\.?|llc\\.?|corp\\.?|services|india|digital|technologies|entertainment)\\b"), "")
        // Remove trailing punctuation or extra symbols
        s = s.replace(Regex("[^a-zA-Z0-9+\\s]"), " ").replace(Regex("\\s+"), " ").trim()
        return s
    }

    private fun toTitleCase(input: String): String {
        return input.split(" ")
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
    }
}
