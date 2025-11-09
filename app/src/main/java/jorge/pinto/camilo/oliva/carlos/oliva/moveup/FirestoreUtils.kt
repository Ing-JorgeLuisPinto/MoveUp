package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.maps.model.LatLng

// 🔥 Función para guardar una ruta en Firestore
fun saveRouteToFirestore(route: BusRoute, onResult: (Boolean) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    // Firestore no admite directamente objetos LatLng, así que los convertimos a mapas
    val pathPointsMap = route.pathPoints.map {
        mapOf("lat" to it.latitude, "lng" to it.longitude)
    }

    val stopsMap = route.stops.map { stop ->
        mapOf(
            "id" to stop.id,
            "name" to stop.name,
            "lat" to stop.location.latitude,
            "lng" to stop.location.longitude,
            "order" to stop.order
        )
    }

    val routeData = hashMapOf(
        "id" to route.id,
        "number" to route.number,
        "name" to route.name,
        "company" to route.company,
        "origin" to route.origin,
        "destination" to route.destination,
        "duration" to route.duration,
        "frequency" to route.frequency,
        "schedule" to route.schedule,
        "fare" to route.fare,
        "status" to route.status,
        "pathPoints" to pathPointsMap,
        "stops" to stopsMap
    )

    db.collection("saved_routes")
        .document(route.id)
        .set(routeData)
        .addOnSuccessListener { onResult(true) }
        .addOnFailureListener { onResult(false) }

}
// 🔥 Función para guardar un incidente reportado
fun reportIncidentToFirestore(
    routeId: String,
    description: String,
    location: LatLng?,
    onResult: (Boolean) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val incidentData = hashMapOf(
        "routeId" to routeId,
        "description" to description,
        "timestamp" to System.currentTimeMillis(),
        "location" to location?.let {
            mapOf("lat" to it.latitude, "lng" to it.longitude)
        }
    )

    db.collection("incidents")
        .add(incidentData)
        .addOnSuccessListener { onResult(true) }
        .addOnFailureListener { onResult(false) }
}

