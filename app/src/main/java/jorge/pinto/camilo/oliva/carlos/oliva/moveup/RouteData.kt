package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import com.google.android.gms.maps.model.LatLng
import androidx.compose.ui.graphics.Color // Necesario para la función de color

// Extensión para asignar un color a la ruta basado en su número.
fun BusRoute.getRouteColor(): Color {
    return when (this.number) {
        "R1" -> Color(0xFFE53935) // Rojo
        "R2" -> Color(0xFF4CAF50) // Verde
        "R3" -> Color(0xFF2196F3) // Azul
        "R4" -> Color(0xFFFF9800) // Naranja
        "R5" -> Color(0xFF9C27B0) // Púrpura
        else -> Color.Black
    }
}

data class BusRoute(
    val id: String,
    val number: String,
    val name: String,
    val company: String,
    val origin: String,
    val destination: String,
    val duration: String,
    val frequency: String,
    val schedule: String,
    val fare: String,
    val status: String,
    val pathPoints: List<LatLng>,
    val stops: List<BusStop>
)

data class BusStop(
    val id: String,
    val name: String,
    val location: LatLng,
    val order: Int
)

fun getAllRoutes(): List<BusRoute> {
    return listOf(
        // Ruta 1: Comuna Norte - Centro
        BusRoute(
            id = "1", number = "R1", name = "Comuna Norte - Centro", company = "Metrolínea", origin = "Comuna Norte", destination = "Centro Comercial Cacique", duration = "35 min", frequency = "Cada 10 min", schedule = "5:00 AM - 11:00 PM", fare = "$2,800", status = "En servicio",
            pathPoints = listOf(LatLng(7.1380, -73.1198), LatLng(7.1375, -73.1200), LatLng(7.1305, -73.1185), LatLng(7.1260, -73.1178), LatLng(7.1215, -73.1170), LatLng(7.1170, -73.1162), LatLng(7.1125, -73.1155), LatLng(7.1095, -73.1150)),
            stops = listOf(BusStop("s1", "Terminal Comuna Norte", LatLng(7.1380, -73.1198), 1), BusStop("s2", "Estadio Departamental", LatLng(7.1305, -73.1185), 2), BusStop("s6", "Centro Comercial Cacique", LatLng(7.1125, -73.1155), 6))
        ),
        // Ruta 2: Cabecera - Provenza
        BusRoute(
            id = "2", number = "R2", name = "Cabecera - Provenza", company = "Metrolínea", origin = "Cabecera del Llano", destination = "Provenza", duration = "25 min", frequency = "Cada 8 min", schedule = "5:30 AM - 10:30 PM", fare = "$2,600", status = "En servicio",
            pathPoints = listOf(LatLng(7.1295, -73.1200), LatLng(7.1265, -73.1185), LatLng(7.1255, -73.1160), LatLng(7.1235, -73.1110), LatLng(7.1205, -73.1050)),
            stops = listOf(BusStop("s8", "Cabecera Mall", LatLng(7.1295, -73.1200), 1), BusStop("s9", "Universidad UNAB", LatLng(7.1265, -73.1185), 2), BusStop("s12", "Provenza Centro", LatLng(7.1205, -73.1050), 5))
        ),
        // Ruta 3: Floridablanca - Centro
        BusRoute(
            id = "3", number = "R3", name = "Floridablanca - Centro", company = "Cootranscar", origin = "Floridablanca", destination = "Parque García Rovira", duration = "40 min", frequency = "Cada 12 min", schedule = "5:00 AM - 11:00 PM", fare = "$2,900", status = "En servicio",
            pathPoints = listOf(LatLng(7.0628, -73.0867), LatLng(7.0784, -73.0936), LatLng(7.0940, -73.1005), LatLng(7.1096, -73.1074), LatLng(7.1200, -73.1120)),
            stops = listOf(BusStop("s13", "Terminal Floridablanca", LatLng(7.0628, -73.0867), 1), BusStop("s14", "Cañaveral", LatLng(7.0784, -73.0936), 2), BusStop("s17", "Parque García Rovira", LatLng(7.1200, -73.1120), 5))
        ),
        // Ruta 4: Piedecuesta - UIS
        BusRoute(
            id = "4", number = "R4", name = "Piedecuesta - UIS", company = "Cotaxi", origin = "Piedecuesta", destination = "Universidad UIS", duration = "30 min", frequency = "Cada 15 min", schedule = "6:00 AM - 9:00 PM", fare = "$2,700", status = "En servicio",
            pathPoints = listOf(LatLng(6.9840, -73.0520), LatLng(7.0000, -73.0640), LatLng(7.0240, -73.0820), LatLng(7.0400, -73.0940)),
            stops = listOf(BusStop("s18", "Centro Piedecuesta", LatLng(6.9840, -73.0520), 1), BusStop("s20", "Portal UIS", LatLng(7.0240, -73.0820), 3), BusStop("s21", "Universidad UIS", LatLng(7.0400, -73.0940), 4))
        ),
        // Ruta 5: Girón - Caracolí
        BusRoute(
            id = "5", number = "R5", name = "Girón - Caracolí", company = "Cootranscar", origin = "Girón", destination = "Caracolí", duration = "28 min", frequency = "Cada 10 min", schedule = "5:00 AM - 10:00 PM", fare = "$2,500", status = "En servicio",
            pathPoints = listOf(LatLng(7.0694, -73.1692), LatLng(7.0806, -73.1608), LatLng(7.0918, -73.1524), LatLng(7.1030, -73.1440)),
            stops = listOf(BusStop("s22", "Parque Girón", LatLng(7.0694, -73.1692), 1), BusStop("s23", "Centro Comercial", LatLng(7.0806, -73.1608), 2), BusStop("s25", "Terminal Caracolí", LatLng(7.1030, -73.1440), 4))
        )
    )
}

fun getRouteById(routeId: String): BusRoute? {
    return getAllRoutes().find { it.id == routeId }
}