package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
// Importar la versión AutoMirrored para evitar el aviso de depreciación
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier // <-- Asegúrate de que este import esté presente
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp // <-- Asegúrate de que este import esté presente
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.LatLng


// LatLng por defecto (Centro de Bucaramanga)
val BUCARAMANGA_CENTER = LatLng(7.1195, -73.1162)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverRouteScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    var routeAssignedId by remember { mutableStateOf<String?>(null) }
    var busRouteData by remember { mutableStateOf<BusRoute?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Estado del mapa y la cámara
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(BUCARAMANGA_CENTER, 12f)
    }

    // Cargar la ruta asignada del conductor
    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("drivers").document(userId).get()
                .addOnSuccessListener { document ->
                    val routeId = document.getString("routeAssigned")
                    routeAssignedId = routeId

                    if (routeId != null) {
                        val route = getRouteById(routeId)
                        busRouteData = route

                        // Mover la cámara a la primera parada de la ruta
                        val initialPosition = route?.pathPoints?.firstOrNull() ?: BUCARAMANGA_CENTER
                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(initialPosition, 14f))
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al cargar la ruta: ${it.message}", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Ruta Asignada", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2196F3))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF2196F3))
                    }
                }
                busRouteData != null -> {
                    // Tarjeta de información de la ruta
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "${busRouteData!!.number}: ${busRouteData!!.name}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = busRouteData!!.getRouteColor()
                            )
                            Spacer(Modifier.height(4.dp))
                            Text("Origen: ${busRouteData!!.origin} - Destino: ${busRouteData!!.destination}", style = MaterialTheme.typography.bodyMedium)
                            Text("Paradas: ${busRouteData!!.stops.size} | Duración: ${busRouteData!!.duration}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }

                    // ** MAPA DE GOOGLE MAPS COMPOSE **
                    GoogleMap(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp).padding(bottom = 16.dp),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = true)
                    ) {
                        // 1. DIBUJAR LA RUTA
                        Polyline(
                            points = busRouteData!!.pathPoints,
                            color = busRouteData!!.getRouteColor(),
                            width = 10f,
                            jointType = JointType.ROUND
                        )

                        // 2. MARCADORES DE PARADAS
                        busRouteData!!.stops.forEach { stop ->
                            Marker(
                                state = rememberMarkerState(position = stop.location),
                                title = "Parada ${stop.order}: ${stop.name}",
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                            )
                        }
                    }
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Ruta asignada no encontrada o no válida.", color = Color.Gray, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}