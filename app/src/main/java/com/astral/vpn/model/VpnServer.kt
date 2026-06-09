package com.astral.vpn.model

import com.google.gson.annotations.SerializedName

data class ServerData(
    @SerializedName("servers")
    val servers: List<VpnServer>,
    @SerializedName("countries")
    val countries: Map<String, String>
)

data class VpnServer(
    val hostname: String,
    val ip: String,
    val score: String,
    val ping: String,
    val speed: String,
    val countrylong: String,
    val countryshort: String,
    val openvpn_configdata_base64: String,
    // Helper field for the generated name
    var displayName: String = ""
)
