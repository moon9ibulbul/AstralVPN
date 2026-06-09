package com.astral.vpn.api

import com.astral.vpn.model.ServerData
import com.google.gson.JsonArray
import retrofit2.http.GET

interface VpnApi {
    @GET("json/data.json")
    suspend fun getServers(): JsonArray
}
