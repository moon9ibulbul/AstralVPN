package com.astral.vpn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.astral.vpn.ui.screens.MainScreen
import com.astral.vpn.ui.screens.ServerListScreen
import com.astral.vpn.ui.theme.AstralVPNTheme
import com.astral.vpn.vpn.VpnManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        VpnManager.initStatusListener()

        setContent {
            AstralVPNTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") {
                            MainScreen(navController)
                        }
                        composable("serverList") {
                            ServerListScreen(navController)
                        }
                    }
                }
            }
        }
    }
}
