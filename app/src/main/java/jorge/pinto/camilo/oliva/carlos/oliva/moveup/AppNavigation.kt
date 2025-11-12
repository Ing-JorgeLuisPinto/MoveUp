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
        // 🔹 Splash
        composable("splash") {
            SplashScreen(
                onNavigateToMainMenu = {
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // 🔹 Menú Principal
        composable("main") {
            MainMenuScreen(
                onNavigateToSearch = { navController.navigate("search") },
                onNavigateToMap = { routeName ->
                    navController.navigate("map/$routeName")
                },
                onNavigateToMyRoutes = { navController.navigate("myroutes") },
                onNavigateToReport = { navController.navigate("report") },
                onNavigateToDriverLogin = { navController.navigate("driver_login") }
            )
        }

        // 🔹 Buscar Ruta
// 🔹 Buscar Ruta
        composable("search") {
            SearchRouteScreen(
                onNavigateBack = { navController.popBackStack() },
                onRouteClick = { routeId ->
                    navController.navigate("map/$routeId")
                }
            )
        }

        // 🔹 Pantalla de Detalle (por si la usas más adelante)
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

        // 🔹 Mapa Completo
        composable(
            route = "map/{routeName}",
            arguments = listOf(navArgument("routeName") { type = NavType.StringType })
        ) { backStackEntry ->
            val routeName = backStackEntry.arguments?.getString("routeName") ?: ""
            MapFullScreen(
                onNavigateBack = { navController.popBackStack() },
                selectedRouteId = routeName
            )
        }

        // 🔹 Rutas Guardadas
        composable("myroutes") {
            MyRoutesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 🔹 Reporte Usuario
        composable("report") {
            ReportScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 🔹 Login Conductor
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

        // 🔹 Registro Conductor
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

        // 🔹 Dashboard Conductor
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

        // 🔹 Reporte Conductor
        composable("driver_report") {
            DriverReportScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 🔹 Ruta Conductor
        composable("driver_route") {
            DriverRouteScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 🔹 Historial Conductor
        composable("driver_history") {
            DriverHistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 🔹 Configuración Conductor
        composable("driver_settings") {
            DriverSettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

