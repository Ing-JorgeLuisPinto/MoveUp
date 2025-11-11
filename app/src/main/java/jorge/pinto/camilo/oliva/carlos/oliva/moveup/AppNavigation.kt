package jorge.pinto.camilo.oliva.carlos.oliva.moveup
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // Splash Screen
        composable("splash") {
            SplashScreen(
                onNavigateToMainMenu = {
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // Main Menu
        composable("main") {
            MainMenuScreen(
                onNavigateToSearch = { navController.navigate("search") },
                onNavigateToMap = { navController.navigate("map") },
                onNavigateToMyRoutes = { navController.navigate("myroutes") },
                onNavigateToReport = { navController.navigate("report") },
                onNavigateToDriverLogin = { navController.navigate("driver_login") }
            )
        }

        // Search Route
        composable("search") {
            SearchRouteScreen(
                onNavigateBack = { navController.popBackStack() },
                onRouteClick = { routeId ->
                    navController.navigate("route_detail/$routeId")
                }
            )
        }

        // Route Detail
        composable(
            route = "route_detail/{routeId}",
            arguments = listOf(navArgument("routeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString("routeId") ?: ""
            RouteDetailScreen(
                routeId = routeId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Map Full Screen
        composable("map") {
            MapFullScreen(
                onNavigateBack = { navController.popBackStack() },
                selectedRouteId = null
            )
        }

        // My Routes
        composable("myroutes") {
            MyRoutesScreen(
                onNavigateBack = { navController.popBackStack() },
                onRouteClick = { routeId ->
                    navController.navigate("route_detail/$routeId")
                }
            )
        }

        // Report Screen (Usuario normal)
        composable("report") {
            ReportScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Driver Login
        composable("driver_login") {
            DriverLoginScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRegister = { navController.navigate("driver_register") },
                onLoginSuccess = {
                    navController.navigate("driver_dashboard") {
                        popUpTo("driver_login") { inclusive = true }
                    }
                }
            )
        }

        // Driver Register
        composable("driver_register") {
            DriverRegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate("driver_dashboard") {
                        popUpTo("driver_login") { inclusive = true }
                    }
                }
            )
        }

        // Driver Dashboard (Punto de conexión central)
        composable("driver_dashboard") {
            DriverDashboardScreen(
                onLogout = {
                    navController.navigate("main") {
                        popUpTo("driver_dashboard") { inclusive = true }
                    }
                },
                onNavigateToReport = { navController.navigate("driver_report") },
                onNavigateToRoute = { navController.navigate("driver_route") },
                onNavigateToHistory = { navController.navigate("driver_history") },
                onNavigateToSettings = { navController.navigate("driver_settings") }
            )
        }

        // Driver Report Screen
        composable("driver_report") {
            DriverReportScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Driver Route Screen
        composable("driver_route") {
            DriverRouteScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Driver History Screen
        composable("driver_history") {
            DriverHistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Driver Settings Screen
        composable("driver_settings") {
            DriverSettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}