package com.loopin.app.data.catalog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ServiceCatalogProviderTest {

    @Test
    fun `all services returns populated catalog`() {
        val services = ServiceCatalogProvider.getAllServices()
        assertTrue("Catalog should have services", services.isNotEmpty())
        assertTrue("Should include at least 7 services", services.size >= 7)
    }

    @Test
    fun `netflix service has expected 4 plans`() {
        val netflix = ServiceCatalogProvider.getServiceById("netflix")
        assertNotNull("Netflix must exist in catalog", netflix)
        assertEquals("Netflix", netflix!!.name)
        assertEquals(4, netflix.plans.size)

        val planIds = netflix.plans.map { it.id }
        assertTrue(planIds.contains("mobile"))
        assertTrue(planIds.contains("basic"))
        assertTrue(planIds.contains("standard"))
        assertTrue(planIds.contains("premium"))

        val mobile = netflix.plans.first { it.id == "mobile" }
        assertEquals("₹ 149", mobile.priceFormatted)
        assertEquals("480p", mobile.resolution)
        assertEquals(6, mobile.features.size)

        val basic = netflix.plans.first { it.id == "basic" }
        assertEquals("₹ 199", basic.priceFormatted)
        assertEquals("720p", basic.resolution)

        val standard = netflix.plans.first { it.id == "standard" }
        assertEquals("₹ 499", standard.priceFormatted)
        assertEquals("1080p", standard.resolution)

        val premium = netflix.plans.first { it.id == "premium" }
        assertEquals("₹ 649", premium.priceFormatted)
        assertEquals("4K +HDR", premium.resolution)
    }

    @Test
    fun `getServiceById case insensitive lookup`() {
        val netflixLower = ServiceCatalogProvider.getServiceById("netflix")
        val netflixUpper = ServiceCatalogProvider.getServiceById("NETFLIX")
        assertNotNull(netflixLower)
        assertNotNull(netflixUpper)
        assertEquals(netflixLower!!.id, netflixUpper!!.id)
    }

    @Test
    fun `getServiceById returns null for unknown id`() {
        val unknown = ServiceCatalogProvider.getServiceById("nonexistent_service_xyz")
        assertNull(unknown)
    }

    @Test
    fun `all services have non-empty website URLs and plans`() {
        val services = ServiceCatalogProvider.getAllServices()
        for (service in services) {
            assertTrue("Service ${service.id} should have a website URL", service.websiteUrl.isNotBlank())
            assertTrue("Service ${service.id} should have plans", service.plans.isNotEmpty())
            for (plan in service.plans) {
                assertTrue("Plan ${plan.id} should have features", plan.features.isNotEmpty())
                assertTrue("Plan ${plan.id} should have positive price", plan.priceMinor > 0)
            }
        }
    }
}
