package com.example.csuper.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.csuper.ui.ConsentScreen
import com.example.csuper.ui.DashboardScreen
import com.example.csuper.util.ConsentStore

/**
 * Navigation graph for C-SUPER app
 */
sealed class Screen(val route: String) {
    object Consent : Screen("consent")
    object Dashboard : Screen("dashboard")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    consentStore: ConsentStore
) {
    val isFirstLaunch by consentStore.isFirstLaunch().collectAsState(initial = true)
    
    val startDestination = if (isFirstLaunch) {
        Screen.Consent.route
    } else {
        Screen.Dashboard.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Consent.route) {
            ConsentScreen(
                onConsentComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Consent.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
    }
}
