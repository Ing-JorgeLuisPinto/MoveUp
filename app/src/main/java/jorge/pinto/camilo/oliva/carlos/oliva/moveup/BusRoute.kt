package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import com.google.android.gms.maps.model.LatLng

data class BusRoute(
    val name: String = "",
    val number: String = "",
    val pathPoints: List<LatLng> = emptyList(),
    val routeColor: Int = 0xFF1565C0.toInt(),

    // 🔹 Campos opcionales (para evitar errores en RouteDetailScreen)
    val status: String = "En servicio",
    val duration: String = "N/A",
    val frequency: String = "N/A",
    val fare: String = "$0",
    val company: String = "Sin datos",
    val schedule: String = "No especificado",

    // 🔹 Lista de paraderos
    val stops: List<BusStop> = emptyList()
)

data class BusStop(
    val name: String = "",
    val order: Int = 0,
    val location: LatLng = LatLng(0.0, 0.0)
)
