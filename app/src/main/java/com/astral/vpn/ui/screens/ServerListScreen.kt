package com.astral.vpn.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.astral.vpn.api.VpnApi
import com.astral.vpn.model.ServerData
import com.astral.vpn.model.VpnServer
import com.astral.vpn.vpn.VpnManager
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerListScreen(navController: NavController) {
    var servers by remember { mutableStateOf<List<VpnServer>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://raw.githubusercontent.com/9xN/auto-ovpn/main/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val api = retrofit.create(VpnApi::class.java)
                val response = api.getServers()
                if (response.size() > 0) {
                    val gson = Gson()
                    val serverData = gson.fromJson(response.get(0), ServerData::class.java)

                    val countryCounts = mutableMapOf<String, Int>()
                    val processedServers = serverData.servers.map {
                        val count = countryCounts.getOrDefault(it.countryshort, 0) + 1
                        countryCounts[it.countryshort] = count
                        it.copy(displayName = "${it.countryshort}-$count")
                    }
                    servers = processedServers
                    VpnManager.setServers(processedServers)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Server") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(servers) { server ->
                    ServerItem(server) {
                        VpnManager.startVpn(navController.context, server)
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}

@Composable
fun ServerItem(server: VpnServer, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        headlineContent = { Text(server.displayName) },
        supportingContent = { Text(server.countrylong) },
        leadingContent = {
            AsyncImage(
                model = "https://flagcdn.com/w80/${server.countryshort.lowercase()}.png",
                contentDescription = server.countryshort,
                modifier = Modifier.width(40.dp)
            )
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${server.ping} ms", modifier = Modifier.padding(end = 8.dp))
                val signalColor = when {
                    (server.ping.toIntOrNull() ?: 999) < 50 -> Color.Green
                    (server.ping.toIntOrNull() ?: 999) < 150 -> Color.Yellow
                    else -> Color.Red
                }
                Icon(Icons.Default.SignalCellularAlt, contentDescription = "Ping", tint = signalColor)
            }
        }
    )
}
