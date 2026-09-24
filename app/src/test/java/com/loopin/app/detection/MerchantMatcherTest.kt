package com.loopin.app.detection

import com.loopin.app.detection.matcher.MerchantMatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MerchantMatcherTest {

    @Test
    fun `match matches Netflix and accurately identifies Premium plan from price`() {
        val result = MerchantMatcher.match("NETFLIX ENTERTAINMENT", 64900L)
        assertEquals("Netflix", result.serviceName)
        assertNotNull(result.catalogService)
        assertEquals("netflix", result.catalogService?.id)
        assertNotNull(result.matchedPlan)
        assertEquals("Premium", result.matchedPlan?.name)
        assertEquals("Entertainment", result.category)
        assertTrue(result.confidence >= 0.9f)
    }

    @Test
    fun `match matches Netflix and identifies Mobile plan from price`() {
        val result = MerchantMatcher.match("NETFLIX.COM", 14900L)
        assertEquals("Netflix", result.serviceName)
        assertEquals("Mobile", result.matchedPlan?.name)
    }

    @Test
    fun `match matches Hotstar aliases to catalog Jio Hotstar`() {
        val result1 = MerchantMatcher.match("DISNEY+ HOTSTAR", 49900L)
        assertEquals("Jio Hotstar", result1.serviceName)
        assertEquals("Super Mobile", result1.matchedPlan?.name)

        val result2 = MerchantMatcher.match("NOVI DIGITAL ENTERTAINMENT", 49900L)
        assertEquals("Jio Hotstar", result2.serviceName)
    }

    @Test
    fun `match matches Spotify aliases to Spotify catalog`() {
        val result = MerchantMatcher.match("SPOTIFY INDIA", 11900L)
        assertEquals("Spotify", result.serviceName)
        assertEquals("Music", result.category)
    }

    @Test
    fun `match matches non-catalog popular services like YouTube, Prime, and Swiggy`() {
        val yt = MerchantMatcher.match("GOOGLE YOUTUBE", 14900L)
        assertEquals("YouTube Premium", yt.serviceName)
        assertNull(yt.catalogService)

        val prime = MerchantMatcher.match("AMAZON PRIME", 29900L)
        assertEquals("Amazon Prime", prime.serviceName)

        val swiggy = MerchantMatcher.match("BUNDL TECHNOLOGIES (SWIGGY ONE)", 29900L)
        assertEquals("Swiggy One", swiggy.serviceName)
        assertEquals("Food & Lifestyle", swiggy.category)
    }

    @Test
    fun `match handles unknown clean merchant and formats title case`() {
        val result = MerchantMatcher.match("MIDJOURNEY INC", 80000L)
        assertEquals("Midjourney", result.serviceName)
        assertNull(result.catalogService)
        assertEquals("Other", result.category)
    }
}
