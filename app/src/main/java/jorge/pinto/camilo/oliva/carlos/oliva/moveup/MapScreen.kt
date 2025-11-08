package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

data class BusRoute(
    val id: String,
    val name: String,
    val points: List<LatLng>,
    val color: androidx.compose.ui.graphics.Color
)

@Composable
fun MapScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Coordenadas de Bucaramanga, Colombia (ejemplo)
    val bucaramanga = LatLng(7.1193, -73.1227)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bucaramanga, 13f)
    }

    // Rutas de ejemplo
    val routes = remember {
        listOf(
            BusRoute(
                id = "1",
                name = "Ruta 1 - Centro",
                points = listOf(
                    LatLng(7.1193, -73.1227),
                    LatLng(7.1250, -73.1200),
                    LatLng(7.1300, -73.1180)
                ),
                color = androidx.compose.ui.graphics.Color.Blue
            ),
            BusRoute(
                id = "2",
                name = "Ruta 2 - Norte",
                points = listOf(
                    LatLng(7.1193, -73.1227),
                    LatLng(7.1150, -73.1250),
                    LatLng(7.1100, -73.1280)
                ),
                color = androidx.compose.ui.graphics.Color.Red
            )
        )
    }

    var selectedRoute by remember { mutableStateOf<BusRoute?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            routes.forEach { route ->
                Polyline(
                    points = route.points,
                    color = route.color,
                    width = 10f,
                    onClick = {
                        selectedRoute = route
                    }
                )

                route.points.forEach { point ->
                    Marker(
                        state = MarkerState(position = point),
                        title = route.name
                    )
                }
            }
        }

        // Tarjeta de información de ruta seleccionada
        selectedRoute?.let { route ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = route.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Toca para ver más detalles",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = { selectedRoute = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}