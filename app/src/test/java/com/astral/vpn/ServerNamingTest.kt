package com.astral.vpn

import com.astral.vpn.model.VpnServer
import org.junit.Assert.assertEquals
import org.junit.Test

class ServerNamingTest {
    @Test
    fun testSequentialNaming() {
        val servers = listOf(
            VpnServer("h1", "1.1", "1", "10", "100", "Japan", "JP", ""),
            VpnServer("h2", "1.2", "1", "10", "100", "Japan", "JP", ""),
            VpnServer("h3", "1.3", "1", "10", "100", "Korea", "KR", ""),
            VpnServer("h4", "1.4", "1", "10", "100", "Japan", "JP", "")
        )

        val countryCounts = mutableMapOf<String, Int>()
        val processedServers = servers.map {
            val count = countryCounts.getOrDefault(it.countryshort, 0) + 1
            countryCounts[it.countryshort] = count
            it.copy(displayName = "${it.countryshort}-$count")
        }

        assertEquals("JP-1", processedServers[0].displayName)
        assertEquals("JP-2", processedServers[1].displayName)
        assertEquals("KR-1", processedServers[2].displayName)
        assertEquals("JP-3", processedServers[3].displayName)
    }
}
