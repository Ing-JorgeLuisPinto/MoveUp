package jorge.pinto.camilo.oliva.carlos.oliva.moveup.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.*
import jorge.pinto.camilo.oliva.carlos.oliva.moveup.data.BusRoute
import jorge.pinto.camilo.oliva.carlos.oliva.moveup.data.BusStop
import jorge.pinto.camilo.oliva.carlos.oliva.moveup.data.getAllRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapFullScreen(
    onNavigateBack: () -> Unit,
    selectedRouteId: String? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Estado del mapa
    val bucaramanga = LatLng(7.1193, -73.1227)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bucaramanga, 13f)
    }

    // Permisos de ubicación
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }

    // Obtener rutas
    val routes = remember { getAllRoutes() }
    val selectedRoute = remember(selectedRouteId) {
        selectedRouteId?.let { routes.find { route -> route.id == it } }
    }

    // Estados de UI
    var showRouteInfo by remember { mutableStateOf(selectedRoute != null) }
    var selectedStopInfo by remember { mutableStateOf<BusStop?>(null) }

    // Lifecycle observer para limpiar recursos
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                // Limpiar recursos si es necesario
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Mapa
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission,
                mapType = MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                compassEnabled = true
            )
        ) {
            // Dibujar rutas
            if (selectedRoute != null) {
                // Solo mostrar la ruta seleccionada
                DrawRoute(selectedRoute, true) { stop ->
                    selectedStopInfo = stop
                }
            } else {
                // Mostrar todas las rutas
                routes.forEach { route ->
                    DrawRoute(route, false) { stop ->
                        selectedStopInfo = stop
                    }
                }
            }
        }

        // Barra superior
        TopAppBar(
            title = {
                Text(
                    text = selectedRoute?.name ?: "Todas las Rutas",
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF2196F3)
            )
        )

        // Botón de Mi Ubicación
        FloatingActionButton(
            onClick = {
                if (hasLocationPermission) {
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                    try {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            location?.let {
                                val currentLatLng = LatLng(it.latitude, it.longitude)
                                cameraPositionState.move(
                                    CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f)
                                )
                            }
                        }
                    } catch (e: SecurityException) {
                        // Manejar excepción
                    }
                } else {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Mi ubicación",
                tint = Color(0xFF2196F3)
            )
        }

        // Información de la ruta seleccionada
        if (showRouteInfo && selectedRoute != null) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedRoute.number,
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color(0xFF2196F3)
                            )
                            Text(
                                text = selectedRoute.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        IconButton(onClick = { showRouteInfo = false }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Cerrar",
                                tint = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoChip("⏱️ ${selectedRoute.duration}")
                        InfoChip("🚌 ${selectedRoute.frequency}")
                        InfoChip("💵 ${selectedRoute.fare}")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${selectedRoute.stops.size} paraderos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }

        // Información del paradero seleccionado
        selectedStopInfo?.let { stop ->
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Parada #${stop.order}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF2196F3)
                        )
                        Text(
                            text = stop.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    IconButton(onClick = { selectedStopInfo = null }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Cerrar",
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawRoute(
    route: BusRoute,
    isSelected: Boolean,
    onStopClick: (BusStop) -> Unit
) {
    // Dibujar la polilínea (ruta)
    Polyline(
        points = route.pathPoints,
        color = if (isSelected) Color(0xFF2196F3) else Color(0xFF4CAF50),
        width = if (isSelected) 12f else 8f,
        geodesic = true
    )

    // Marcador de origen (punto A)
    route.stops.firstOrNull()?.let { firstStop ->
        Marker(
            state = MarkerState(position = firstStop.location),
            title = "Origen: ${firstStop.name}",
            snippet = route.name,
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        )
    }

    // Marcador de destino (punto B)
    route.stops.lastOrNull()?.let { lastStop ->
        Marker(
            state = MarkerState(position = lastStop.location),
            title = "Destino: ${lastStop.name}",
            snippet = route.name,
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        )
    }

    // Marcadores de paraderos intermedios
    route.stops.drop(1).dropLast(1).forEach { stop ->
        Marker(
            state = MarkerState(position = stop.location),
            title = stop.name,
            snippet = "Parada #${stop.order}",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
            onClick = {
                onStopClick(stop)
                true
            }
        )
    }
}

@Composable
private fun InfoChip(text: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFE3F2FD),
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF1976D2)
        )
    }
}