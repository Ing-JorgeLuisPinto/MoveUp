package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.maps.model.LatLng

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

object FirestoreUtils {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun saveRoute(context: Context, busRoute: BusRoute) {
        val userId = auth.currentUser?.uid ?: return

        val routeData = hashMapOf(
            "name" to busRoute.name,
            "number" to busRoute.number,
            "color" to busRoute.routeColor
        )

        db.collection("users")
            .document(userId)
            .collection("saved_routes")
            .document(busRoute.number)
            .set(routeData)
            .addOnSuccessListener {
                Toast.makeText(context, "Ruta guardada correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al guardar: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun getSavedRoutes(
        onSuccess: (List<BusRoute>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("saved_routes")
            .get()
            .addOnSuccessListener { snapshot ->
                val routes = snapshot.documents.mapNotNull { doc ->
                    BusRoute(
                        name = doc.getString("name") ?: "",
                        number = doc.getString("number") ?: "",
                        routeColor = (doc.getLong("color") ?: 0xFF1565C0).toInt()
                    )
                }
                onSuccess(routes)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun deleteRoute(context: Context, routeNumber: String) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("saved_routes")
            .document(routeNumber)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Ruta eliminada correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al eliminar: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
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