package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng

fun getAllRoutes(): List<BusRoute> {
    return listOf(
        BusRoute(
            name = "Ruta #1 RUTA#1",
            number = "1",
            pathPoints = listOf(
                LatLng(7.1195, -73.1162),
                LatLng(7.1180, -73.1140)
            ),
            routeColor = Color(0xFF1976D2).hashCode()
        ),
        BusRoute(
            name = "Ruta #2 CUMBRE-EL CARMEN",
            number = "2",
            pathPoints = listOf(
                LatLng(7.1210, -73.1150),
                LatLng(7.1220, -73.1180)
            ),
            routeColor = Color(0xFF43A047).hashCode()
        ),
        BusRoute(
            name = "Ruta #3 BOSCONIA - LOS ANGELES - CHAPINERO",
            number = "3",
            pathPoints = listOf(
                LatLng(7.1240, -73.1130),
                LatLng(7.1260, -73.1150)
            ),
            routeColor = Color(0xFFFBC02D).hashCode()
        ),
        BusRoute(
            name = "Ruta #4 LA JOYA - SAN MATEO - DON BOSCO",
            number = "4",
            pathPoints = listOf(
                LatLng(7.1185, -73.1200),
                LatLng(7.1170, -73.1250)
            ),
            routeColor = Color(0xFFE64A19).hashCode()
        ),
        BusRoute(
            name = "Ruta #5 CRISTAL BAJO - SAN LUIS - PROVENZA",
            number = "5",
            pathPoints = listOf(
                LatLng(7.1160, -73.1190),
                LatLng(7.1150, -73.1160)
            ),
            routeColor = Color(0xFF7B1FA2).hashCode()
        )
    )
}

// 🔹 Esta función ahora soporta "ruta_1", "R1" o "1" sin importar el formato
fun getRouteById(id: String): BusRoute? {
    val normalizedId = when (id.lowercase()) {
        "ruta_1", "r1", "1" -> "1"
        "ruta_2", "r2", "2" -> "2"
        "ruta_3", "r3", "3" -> "3"
        "ruta_4", "r4", "4" -> "4"
        "ruta_5", "r5", "5" -> "5"
        else -> id
    }
    return getAllRoutes().find { it.number == normalizedId }
}
