package com.example.csuper.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.csuper.util.ConsentStore
import com.example.csuper.ui.ConsentScreen
import com.example.csuper.ui.DashboardScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    consentStore: ConsentStore
) {
    NavHost(
        navController = navController,
        startDestination = "consent"
    ) {
        composable("consent") {
            ConsentScreen(
                onConsentComplete = {
                    // This navigates to dashboard!
                    navController.navigate("dashboard") {
                        popUpTo("consent") { inclusive = true }
                    }
                }
            )
        }
        composable("dashboard") {
            DashboardScreen()
        }
    }
}