package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.gms.maps.model.LatLng
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.*
import org.xmlpull.v1.XmlPullParser
import android.util.Xml
import java.io.InputStream

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

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(BUCARAMANGA_CENTER, 12f)
    }

    // 🔹 Selector de archivo (.kml o .xml)
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val route = loadRouteFromFile(context, it)
            if (route != null) {
                busRouteData = route
                Toast.makeText(context, "Ruta cargada desde archivo", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error al leer el archivo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 🔥 Cargar ruta asignada desde Firestore
    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("drivers").document(userId).get()
                .addOnSuccessListener { document ->
                    val assignedRoute = document.getString("routeAssigned")
                    if (assignedRoute != null) {
                        db.collection("routes").document(assignedRoute).get()
                            .addOnSuccessListener { routeDoc ->
                                val route = routeDoc.toObject(BusRoute::class.java)
                                busRouteData = route
                                isLoading = false
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Error al cargar ruta asignada", Toast.LENGTH_SHORT).show()
                                isLoading = false
                            }
                    } else {
                        isLoading = false
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al conectar con Firestore", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    // 🔹 Mueve la cámara al cargar la ruta
    LaunchedEffect(busRouteData, isMapLoaded) {
        if (busRouteData != null && isMapLoaded) {
            val startPoint = busRouteData!!.pathPoints.firstOrNull() ?: BUCARAMANGA_CENTER
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(startPoint, 14f),
                durationMs = 1000
            )
        }
    }

    // 🚀 Servicio de ubicación
    LaunchedEffect(busRouteData, locationPermissionState.status.isGranted) {
        if (busRouteData != null && locationPermissionState.status.isGranted && !isServiceStarted) {
            val intent = Intent(context, LocationService::class.java).apply {
                action = "START_LOCATION_SERVICE"
            }
            context.startService(intent)
            isServiceStarted = true
        }
    }

    // 🔹 Detener servicio al salir
    DisposableEffect(Unit) {
        onDispose {
            if (isServiceStarted) {
                val intent = Intent(context, LocationService::class.java).apply {
                    action = "STOP_LOCATION_SERVICE"
                }
                context.stopService(intent)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Ruta Asignada", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { filePicker.launch("*/*") }) {
                        Text("Cargar Archivo", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2196F3))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            when {
                isLoading -> LoadingIndicator()
                !locationPermissionState.status.isGranted -> PermissionRequiredScreen(context, locationPermissionState)
                busRouteData != null -> RouteMapContent(busRouteData!!, cameraPositionState) { isMapLoaded = true }
                else -> NoRouteAvailableMessage()
            }
        }
    }
}

// ================================================================
// 🔹 PARSEO DE ARCHIVOS .KML Y .XML
// ================================================================
fun loadRouteFromFile(context: Context, uri: Uri): BusRoute? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        when {
            uri.toString().endsWith(".kml", true) -> parseKML(inputStream!!)
            uri.toString().endsWith(".xml", true) -> parseXML(inputStream!!)
            else -> null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun parseKML(inputStream: InputStream): BusRoute {
    val points = mutableListOf<LatLng>()
    val xml = Xml.newPullParser()
    xml.setInput(inputStream, null)
    var eventType = xml.eventType

    while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG && xml.name == "coordinates") {
            xml.next()
            val coordsText = xml.text.trim()
            val coords = coordsText.split(" ")
            coords.forEach {
                val parts = it.split(",")
                if (parts.size >= 2) {
                    val lng = parts[0].toDouble()
                    val lat = parts[1].toDouble()
                    points.add(LatLng(lat, lng))
                }
            }
        }
        eventType = xml.next()
    }
    return BusRoute("Ruta desde archivo KML", "KML", points, Color(0xFF1565C0).hashCode())
}

fun parseXML(inputStream: InputStream): BusRoute {
    val points = mutableListOf<LatLng>()
    val xml = Xml.newPullParser()
    xml.setInput(inputStream, null)
    var eventType = xml.eventType

    while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG && xml.name == "point") {
            val lat = xml.getAttributeValue(null, "lat")?.toDoubleOrNull()
            val lng = xml.getAttributeValue(null, "lng")?.toDoubleOrNull()
            if (lat != null && lng != null) points.add(LatLng(lat, lng))
        }
        eventType = xml.next()
    }
    return BusRoute("Ruta desde archivo XML", "XML", points, Color(0xFF4CAF50).hashCode())
}

// ================================================================
// 🔹 COMPONENTES AUXILIARES
// ================================================================
@Composable
fun LoadingIndicator() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Color(0xFF2196F3))
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequiredScreen(context: Context, permissionState: PermissionState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Permiso de ubicación requerido", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Necesitamos tu ubicación para mostrar la ruta correctamente.", textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (permissionState.status.shouldShowRationale) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", context.packageName, null)
                context.startActivity(intent)
            } else {
                permissionState.launchPermissionRequest()
            }
        }) {
            Text("Conceder permiso")
        }
    }
}

@Composable
fun RouteMapContent(
    busRouteData: BusRoute,
    cameraPositionState: CameraPositionState,
    onMapLoaded: () -> Unit
) {
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLoaded = onMapLoaded
    ) {
        Polyline(
            points = busRouteData.pathPoints,
            color = Color(busRouteData.routeColor),
            width = 8f,
            jointType = JointType.ROUND
        )

        busRouteData.pathPoints.firstOrNull()?.let {
            Marker(
                state = MarkerState(position = it),
                title = "Inicio de ruta",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            )
        }
    }
}

@Composable
fun NoRouteAvailableMessage() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("No hay ruta disponible", color = Color.Gray)
    }
}
