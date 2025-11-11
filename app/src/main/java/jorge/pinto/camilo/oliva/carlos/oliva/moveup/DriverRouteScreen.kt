package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.LatLng
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale


val BUCARAMANGA_CENTER = LatLng(7.1195, -73.1162)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun DriverRouteScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    var busRouteData by remember { mutableStateOf<BusRoute?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isServiceStarted by remember { mutableStateOf(false) }
    var isMapLoaded by remember { mutableStateOf(false) }

    // Estado de permiso
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(BUCARAMANGA_CENTER, 12f)
    }

    // ✅ CARGA DE RUTA DESDE FIRESTORE
    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("drivers").document(userId).get()
                .addOnSuccessListener { document ->
                    val assignedValue = document.getString("routeAssigned")
                    var route: BusRoute? = null
                    if (assignedValue != null) {
                        // Buscar por ID (ej. "4")
                        route = getRouteById(assignedValue)

                        // Si no se encuentra, buscar por Number (ej. "R4")
                        if (route == null) {
                            route = getAllRoutes().find { it.number == assignedValue }
                        }
                    }
                    busRouteData = route
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

    // ✅ MOVER CÁMARA SOLO CUANDO EL MAPA ESTÉ LISTO
    LaunchedEffect(busRouteData, isMapLoaded) {
        if (busRouteData != null && isMapLoaded) {
            try {
                val initialPosition = busRouteData!!.pathPoints.firstOrNull() ?: BUCARAMANGA_CENTER
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(initialPosition, 14f),
                    durationMs = 1000
                )
            } catch (e: Exception) {
                // Silenciar errores de inicialización
            }
        }
    }

    // INICIO DEL SERVICIO DE UBICACIÓN
    LaunchedEffect(busRouteData, locationPermissionState.status.isGranted) {
        if (busRouteData != null && locationPermissionState.status.isGranted && !isServiceStarted) {
            val serviceIntent = Intent(context, LocationService::class.java).apply {
                action = "START_LOCATION_SERVICE"
            }
            context.startService(serviceIntent)
            isServiceStarted = true
        }
    }

    // DETENER EL SERVICIO
    DisposableEffect(Unit) {
        onDispose {
            if (isServiceStarted) {
                val serviceIntent = Intent(context, LocationService::class.java).apply {
                    action = "STOP_LOCATION_SERVICE"
                }
                context.stopService(serviceIntent)
            }
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
                // Si no hay permisos
                !locationPermissionState.status.isGranted -> {
                    PermissionRequiredScreen(
                        context = context,
                        permissionState = locationPermissionState
                    )
                }
                // Si hay ruta y hay permisos, muestra el mapa
                busRouteData != null && locationPermissionState.status.isGranted -> {
                    RouteMapContent(
                        busRouteData = busRouteData!!,
                        cameraPositionState = cameraPositionState,
                        onMapLoaded = { isMapLoaded = true }
                    )
                }
                // Si no se encuentra la ruta
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "No tienes una ruta asignada",
                                color = Color.Gray,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Contacta al administrador para que te asigne una ruta",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

// ================================================================
// COMPONENTES DE VISTA SEPARADOS
// ================================================================

@Composable
fun RouteMapContent(
    busRouteData: BusRoute,
    cameraPositionState: CameraPositionState,
    onMapLoaded: () -> Unit
) {
    GoogleMap(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = true),
        onMapLoaded = onMapLoaded  // ✅ CALLBACK CUANDO EL MAPA SE CARGA
    ) {
        // 1. DIBUJAR LA RUTA
        Polyline(
            points = busRouteData.pathPoints,
            color = busRouteData.routeColor,
            width = 10f,
            jointType = JointType.ROUND
        )

        // 2. MARCADORES DE PARADAS
        busRouteData.stops.forEach { stop ->
            Marker(
                state = rememberMarkerState(position = stop.location),
                title = "Parada ${stop.order}: ${stop.name}",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
            )
        }
    }
}

fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    ).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequiredScreen(
    context: Context,
    permissionState: PermissionState
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "📍 Permiso de Ubicación Necesario",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "Para ver tu ruta y transmitir tu ubicación en tiempo real, necesitamos acceso a tu ubicación precisa.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (permissionState.status.shouldShowRationale) {
                            openAppSettings(context)
                        } else {
                            permissionState.launchPermissionRequest()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (permissionState.status.shouldShowRationale) "Abrir Ajustes" else "Conceder Permiso")
                }
            }
        }
    }
}