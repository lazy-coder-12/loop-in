package com.loopin.app.data.catalog

import com.loopin.app.R
import com.loopin.app.domain.model.BillingCycle

data class CatalogPlan(
    val id: String,
    val name: String,
    val resolution: String,
    val priceMinor: Long,
    val priceFormatted: String,
    val billingCycle: BillingCycle = BillingCycle.MONTHLY,
    val features: List<String>
)

data class CatalogService(
    val id: String,
    val name: String,
    val category: String,
    val iconRes: Int,
    val websiteUrl: String,
    val plans: List<CatalogPlan>
)

object ServiceCatalogProvider {

    private val services = listOf(
        CatalogService(
            id = "netflix",
            name = "Netflix",
            category = "Entertainment",
            iconRes = R.drawable.card_netflix,
            websiteUrl = "https://help.netflix.com/en/node/24926",
            plans = listOf(
                CatalogPlan(
                    id = "mobile",
                    name = "Mobile",
                    resolution = "480p",
                    priceMinor = 14900L,
                    priceFormatted = "₹ 149",
                    billingCycle = BillingCycle.MONTHLY,
                    features = listOf(
                        "Monthly price ₹ 149.",
                        "Fair video and sound quality.",
                        "Max available resolution 480p.",
                        "Supported devices include mobile phone and tablet.",
                        "Devices your household can watch at the same time only one.",
                        "Available download devices only one."
                    )
                ),
                CatalogPlan(
                    id = "basic",
                    name = "Basic",
                    resolution = "720p",
                    priceMinor = 19900L,
                    priceFormatted = "₹ 199",
                    billingCycle = BillingCycle.MONTHLY,
                    features = listOf(
                        "Monthly price ₹ 199.",
                        "Good video and sound quality.",
                        "Max available resolution 720p (HD).",
                        "Supported devices include TV, computer, mobile phone and tablet.",
                        "Devices your household can watch at the same time only one.",
                        "Available download devices only one."
                    )
                ),
                CatalogPlan(
                    id = "standard",
                    name = "Standard",
                    resolution = "1080p",
                    priceMinor = 49900L,
                    priceFormatted = "₹ 499",
                    billingCycle = BillingCycle.MONTHLY,
                    features = listOf(
                        "Monthly price ₹ 499.",
                        "Great video and sound quality.",
                        "Max available resolution 1080p (Full HD).",
                        "Supported devices include TV, computer, mobile phone and tablet.",
                        "Devices your household can watch at the same time 2.",
                        "Available download devices 2."
                    )
                ),
                CatalogPlan(
                    id = "premium",
                    name = "Premium",
                    resolution = "4K +HDR",
                    priceMinor = 64900L,
                    priceFormatted = "₹ 649",
                    billingCycle = BillingCycle.MONTHLY,
                    features = listOf(
                        "Monthly price ₹ 649.",
                        "Best video and sound quality.",
                        "Max available resolution 4K (Ultra HD) + HDR.",
                        "Spatial audio (immersive sound) included.",
                        "Supported devices include TV, computer, mobile phone and tablet.",
                        "Devices your household can watch at the same time 4.",
                        "Available download devices 6."
                    )
                )
            )
        ),
        CatalogService(
            id = "spotify",
            name = "Spotify",
            category = "Music",
            iconRes = R.drawable.card_spotify,
            websiteUrl = "https://www.spotify.com/in-en/premium/",
            plans = listOf(
                CatalogPlan(
                    id = "individual",
                    name = "Individual",
                    resolution = "High Quality Audio",
                    priceMinor = 11900L,
                    priceFormatted = "₹ 119",
                    billingCycle = BillingCycle.MONTHLY,
                    features = listOf(
                        "Monthly price ₹ 119.",
                        "Ad-free music listening.",
                        "Play anywhere - even offline.",
                        "On-demand playback.",
                        "Prepay or subscribe."
                    )
                ),
                CatalogPlan(
                    id = "duo",
                    name = "Duo",
                    resolution = "2 Premium Accounts",
                    priceMinor = 14900L,
                    priceFormatted = "₹ 149",
                    billingCycle = BillingCycle.MONTHLY,
                    features = listOf(
                        "Monthly price ₹ 149.",
                        "2 Premium accounts for a couple under one roof.",
                        "Ad-free music listening, play offline, on-demand playback.",
                        "Prepay or subscribe."
                    )
                ),
                CatalogPlan(
                    id = "family",
                    name = "Family",
                    resolution = "Up to 6 Accounts",
                    priceMinor = 17900L,
                    priceFormatted = "₹ 179",
                    billingCycle = BillingCycle.MONTHLY,
                    features = listOf(
                        "Monthly price ₹ 179.",
                        "Up to 6 Premium accounts for family members living together.",
                        "Block explicit music.",
                        "Ad-free music listening, play offline, on-demand playback."
                    )
                )
            )
        ),
        CatalogService(
            id = "gemini",
            name = "Gemini",
            category = "AI Tools",
            iconRes = R.drawable.card_gemini,
            websiteUrl = "https://gemini.google.com/advanced",
            plans = listOf(
                CatalogPlan(
                    id = "advanced",
                    name = "Advanced",
                    resolution = "1.5 Pro & Ultra",
                    priceMinor = 195000L,
                    priceFormatted = "₹ 1,950",
                    billingCycle = BillingCycle.MONTHLY,
                    features = listOf(
                        "Monthly price ₹ 1,950.",
                        "Access to Google's most capable AI models.",
                        "Integrated across Gmail, Docs, Slides, and Meet.",
                        "2 TB cloud storage included in Google One.",
                        "Priority access to new features."
                    )
                ),
                CatalogPlan(
                    id = "business",
                    name = "Business",
                    resolution = "Workspace AI",
                    priceMinor = 250000L,
                    priceFormatted = "₹ 2,500",
                    billingCycle = BillingCycle.MONTHLY,
                    features = listOf(
                        "Monthly price ₹ 2,500.",
                        "Enterprise-grade data protection.",
                        "Admin controls and compliance tools.",
                        "Integrated with Google Workspace."
                    )
                )
            )
        ),
        CatalogService(
            id = "hotstar",
            name = "Jio Hotstar",
            category = "Entertainment",
            iconRes = R.drawable.icon_hotstar,
            websiteUrl = "https://www.hotstar.com/in/paywall",
            plans = listOf(
                CatalogPlan(
                    id = "mobile",
                    name = "Super Mobile",
                    resolution = "720p HD",
                    priceMinor = 49900L,
                    priceFormatted = "₹ 499",
                    billingCycle = BillingCycle.YEARLY,
                    features = listOf(
                        "Yearly price ₹ 499.",
                        "All content: Movies, Live Sports, TV, Specials.",
                        "Watch on 1 mobile device.",
                        "HD 720p video quality.",
                        "Stereo audio quality."
                    )
                ),
                CatalogPlan(
                    id = "super",
                    name = "Super All Devices",
                    resolution = "1080p Full HD",
                    priceMinor = 89900L,
                    priceFormatted = "₹ 899",
                    billingCycle = BillingCycle.YEARLY,
                    features = listOf(
                        "Yearly price ₹ 899.",
                        "All content: Movies, Live Sports, TV, Specials.",
                        "Watch on TV or Laptop (2 devices simultaneously).",
                        "Full HD 1080p video quality.",
                        "Dolby 5.1 sound."
                    )
                ),
                CatalogPlan(
                    id = "premium",
                    name = "Premium 4K",
                    resolution = "4K + Dolby Vision",
                    priceMinor = 149900L,
                    priceFormatted = "₹ 1,499",
                    billingCycle = BillingCycle.YEARLY,
                    features = listOf(
                        "Yearly price ₹ 1,499.",
                        "Ad-free entertainment (except Live Sports).",
                        "Watch on 4 devices simultaneously.",
                        "4K Ultra HD video + Dolby Vision.",
                        "Dolby Atmos sound."
                    )
                )
            )
        ),
        CatalogService(
            id = "claude",
            name = "Claude AI",
            category = "Productivity",
            iconRes = R.drawable.icon_claude,
            websiteUrl = "https://claude.ai/pricing",
            plans = listOf(
                CatalogPlan(
                    id = "pro",
                    name = "Claude Pro",
                    resolution = "Claude 3.5 Sonnet & Opus",
                    priceMinor = 199900L,
                    priceFormatted = "₹ 1,999",
                    billingCycle = BillingCycle.MONTHLY,
                    features = listOf(
                        "Monthly price ₹ 1,999.",
                        "5x more usage compared to Free tier.",
                        "Priority access during high-traffic periods.",
                        "Early access to new features and models.",
                        "Projects workspace for organizing chats and docs."
                    )
                ),
                CatalogPlan(
                    id = "team",
                    name = "Claude Team",
                    resolution = "Collab & High Limits",
                    priceMinor = 250000L,
                    priceFormatted = "₹ 2,500",
                    billingCycle = BillingCycle.MONTHLY,
                    features = listOf(
                        "Monthly price ₹ 2,500 per member.",
                        "Higher usage limits than Pro.",
                        "Shared projects and knowledge base across teams.",
                        "Central billing and user administration."
                    )
                )
            )
        ),
        CatalogService(
            id = "figma",
            name = "Figma",
            category = "Productivity",
            iconRes = R.drawable.icon_figma,
            websiteUrl = "https://www.figma.com/pricing/",
            plans = listOf(
                CatalogPlan(
                    id = "professional",
                    name = "Professional",
                    resolution = "Full Design & Code",
                    priceMinor = 120000L,
                    priceFormatted = "₹ 1,200",
                    billingCycle = BillingCycle.MONTHLY,
                    features = listOf(
                        "Monthly price ₹ 1,200 per editor.",
                        "Unlimited Figma files and version history.",
                        "Shared team libraries and design systems.",
                        "Advanced prototyping and Dev Mode access."
                    )
                ),
                CatalogPlan(
                    id = "organization",
                    name = "Organization",
                    resolution = "Org-wide Systems",
                    priceMinor = 250000L,
                    priceFormatted = "₹ 2,500",
                    billingCycle = BillingCycle.MONTHLY,
                    features = listOf(
                        "Monthly price ₹ 2,500 per editor.",
                        "Org-wide design systems and analytics.",
                        "Branching and merging workflows.",
                        "SSO and centralized admin controls."
                    )
                )
            )
        ),
        CatalogService(
            id = "linkedin",
            name = "Linkedin",
            category = "Career",
            iconRes = R.drawable.icon_linkedin,
            websiteUrl = "https://www.linkedin.com/premium/products",
            plans = listOf(
                CatalogPlan(
                    id = "career",
                    name = "Premium Career",
                    resolution = "Get Hired & Stand Out",
                    priceMinor = 149900L,
                    priceFormatted = "₹ 1,499",
                    billingCycle = BillingCycle.MONTHLY,
                    features = listOf(
                        "Monthly price ₹ 1,499.",
                        "5 InMail messages per month.",
                        "See who viewed your profile in the last 365 days.",
                        "Applicant insights and competitive analysis.",
                        "Full access to LinkedIn Learning courses."
                    )
                ),
                CatalogPlan(
                    id = "business",
                    name = "Premium Business",
                    resolution = "Business Insights",
                    priceMinor = 249900L,
                    priceFormatted = "₹ 2,499",
                    billingCycle = BillingCycle.MONTHLY,
                    features = listOf(
                        "Monthly price ₹ 2,499.",
                        "15 InMail messages per month.",
                        "Unlimited people browsing up to 3rd degree.",
                        "Deep company insights and growth metrics."
                    )
                )
            )
        )
    )

    fun getServiceById(id: String): CatalogService? {
        return services.find { it.id.equals(id, ignoreCase = true) }
    }

    fun getAllServices(): List<CatalogService> = services
}
