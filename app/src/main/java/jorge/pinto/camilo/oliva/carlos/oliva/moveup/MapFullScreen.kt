package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.data.kml.KmlLayer

@Composable
fun MapFullScreen(
    selectedRouteId: String, // ← Recibe la ruta seleccionada desde AppNavigation
    onNavigateBack: () -> Unit
) {
    BackHandler(onBack = onNavigateBack)

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Mapa principal
            MapViewContainer(selectedRouteId)

            // Botón de retroceso
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver"
                )
            }
        }
    }
}

@Composable
fun MapViewContainer(selectedRouteId: String) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            val mapView = MapView(context)
            mapView.onCreate(null)
            mapView.getMapAsync(MapReadyCallback(context, selectedRouteId))
            mapView
        },
        update = { mapView ->
            mapView.onResume()
        }
    )
}

class MapReadyCallback(
    private val context: Context,
    private val selectedRouteId: String
) : OnMapReadyCallback {

    override fun onMapReady(map: GoogleMap) {
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isCompassEnabled = true

        try {
            // 🔹 Determina cuál archivo KML cargar según la ruta seleccionada
            val routeResId = getRouteResourceId(context, selectedRouteId)
            if (routeResId != null) {
                val layer = KmlLayer(map, routeResId, context)
                layer.addLayerToMap()

                // Centra el mapa en Bucaramanga (aprox)
                val center = LatLng(7.1254, -73.1198)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 13f))
            } else {
                println("⚠️ No se encontró el archivo KML para la ruta: $selectedRouteId")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 🔹 Mapea el nombre de la ruta con el archivo en res/raw
     *    Ejemplo: selectedRouteId = "ruta_1" → archivo: res/raw/ruta_1.kml
     */
    private fun getRouteResourceId(context: Context, routeName: String): Int? {
        // Limpia el nombre por si viene con espacios o caracteres raros
        val cleanName = routeName.lowercase()
            .replace(" ", "_")
            .replace("#", "")
            .replace("(", "")
            .replace(")", "")
            .replace("-", "_")

        val resId = context.resources.getIdentifier(
            cleanName,
            "raw",
            context.packageName
        )

        return if (resId != 0) resId else null
    }
}
