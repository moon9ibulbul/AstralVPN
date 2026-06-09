package com.astral.vpn.vpn

import android.content.Context
import android.util.Base64
import com.astral.vpn.model.VpnServer
import de.blinkt.openvpn.OpenVpnApi
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.VpnStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.charset.Charset

object VpnManager {
    private val _connectionStatus = MutableStateFlow("Disconnected")
    val connectionStatus = _connectionStatus.asStateFlow()

    private val _connectedServer = MutableStateFlow<VpnServer?>(null)
    val connectedServer = _connectedServer.asStateFlow()

    private val _serverList = MutableStateFlow<List<VpnServer>>(emptyList())
    val serverList = _serverList.asStateFlow()

    fun setServers(servers: List<VpnServer>) {
        _serverList.value = servers
    }

    fun startVpn(context: Context, server: VpnServer) {
        try {
            val configData = Base64.decode(server.openvpn_configdata_base64, Base64.DEFAULT)
            val config = String(configData, Charset.defaultCharset())

            OpenVpnApi.startVpn(context, config, server.countryshort, "vpn", "vpn")
            _connectedServer.value = server
            _connectionStatus.value = "Connecting..."
        } catch (e: Exception) {
            e.printStackTrace()
            _connectionStatus.value = "Error: ${e.message}"
        }
    }

    fun stopVpn(context: Context) {
        try {
            OpenVPNService.setStop(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        _connectionStatus.value = "Disconnected"
        _connectedServer.value = null
    }

    // This should be called from a lifecycle observer or MainActivity
    fun initStatusListener() {
        VpnStatus.addStateListener { state, _, _, _ ->
            _connectionStatus.value = when (state) {
                "CONNECTED" -> "Connected"
                "DISCONNECTED" -> "Disconnected"
                "CONNECTING" -> "Connecting..."
                "RECONNECTING" -> "Reconnecting..."
                "WAIT" -> "Waiting for server..."
                "AUTH" -> "Authenticating..."
                "GET_CONFIG" -> "Getting configuration..."
                else -> state ?: "Unknown"
            }
        }
    }
}
