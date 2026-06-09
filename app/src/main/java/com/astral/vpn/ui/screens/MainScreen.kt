package com.astral.vpn.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.astral.vpn.vpn.VpnManager
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val status by VpnManager.connectionStatus.collectAsState()
    val connectedServer by VpnManager.connectedServer.collectAsState()
    val serverList by VpnManager.serverList.collectAsState()

    var duration by remember { mutableLongStateOf(0L) }

    LaunchedEffect(status) {
        if (status == "Connected") {
            val startTime = System.currentTimeMillis()
            while (status == "Connected") {
                duration = (System.currentTimeMillis() - startTime) / 1000
                delay(1000)
            }
        } else {
            duration = 0L
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("AstralVPN", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Alignment.CenterVertically
        ) {
            Text(
                text = status,
                fontSize = 20.sp,
                color = if (status == "Connected") Color.Green else MaterialTheme.colorScheme.onBackground
            )

            if (status == "Connected" && connectedServer != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "IP: ${connectedServer?.ip ?: "N/A"}")
                Text(text = "Duration: ${formatDuration(duration)}")
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    if (status == "Disconnected" || status == "Unknown") {
                        if (connectedServer != null) {
                            VpnManager.startVpn(navController.context, connectedServer!!)
                        } else if (serverList.isNotEmpty()) {
                            // Recommended: Pick first one for now
                            VpnManager.startVpn(navController.context, serverList.first())
                        } else {
                            navController.navigate("serverList")
                        }
                    } else {
                        VpnManager.stopVpn(navController.context)
                    }
                },
                modifier = Modifier.size(200.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (status == "Connected") Color.Red else Color(0xFF6200EE)
                )
            ) {
                Text(
                    text = if (status == "Connected") "STOP" else "START",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedButton(onClick = { navController.navigate("serverList") }) {
                Text(text = connectedServer?.displayName ?: "Select Server")
            }
        }
    }
}
