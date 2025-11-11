package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

// Función para guardar el reporte en Firestore
fun saveDriverIncidentToFirestore(
    incidentType: String,
    busNumber: String,
    routeId: String,
    description: String,
    onComplete: (Boolean) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: "unknown"

    val incidentData = hashMapOf(
        "driverId" to userId,
        "busNumber" to busNumber,
        "routeId" to routeId,
        "incidentType" to incidentType,
        "description" to description,
        "timestamp" to Date(),
        "status" to "Pending Review"
    )

    db.collection("driverIncidents")
        .add(incidentData)
        .addOnSuccessListener {
            onComplete(true)
        }
        .addOnFailureListener {
            onComplete(false)
        }
}

// Enum para tipos de incidentes comunes del conductor
enum class DriverIncidentType(val title: String) {
    ACCIDENTE("Accidente / Colisión"),
    FALLA_MECANICA("Falla Mecánica del Bus"),
    BLOQUEO_RUTA("Bloqueo de Ruta / Tráfico Severo"),
    PELEA_PASAJEROS("Pelea / Incidente con Pasajeros"),
    OTRO("Otro Incidente")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverReportScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Datos del conductor obtenidos del dashboard (se asume que se cargaron previamente)
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    // Estados para el formulario
    var selectedIncidentType by remember { mutableStateOf<DriverIncidentType?>(null) }
    var description by remember { mutableStateOf("") }
    var busNumber by remember { mutableStateOf("Cargando...") }
    var routeAssigned by remember { mutableStateOf("Cargando...") }
    var isLoadingData by remember { mutableStateOf(true) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Cargar datos del conductor (Bus y Ruta)
    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("drivers").document(userId).get()
                .addOnSuccessListener { document ->
                    busNumber = document.getString("busNumber") ?: "N/A"
                    routeAssigned = document.getString("routeAssigned") ?: "N/A"
                    isLoadingData = false
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al cargar datos del bus", Toast.LENGTH_SHORT).show()
                    isLoadingData = false
                }
        } else {
            isLoadingData = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reporte de Incidente", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2196F3))
            )
        }
    ) { paddingValues ->
        if (isLoadingData) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF2196F3))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5))
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Información del Bus/Ruta
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Detalles del Servicio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Bus Asignado: $busNumber", style = MaterialTheme.typography.bodyLarge)
                        Text("Ruta Asignada: $routeAssigned", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Selección de Tipo de Incidente
                Text(
                    text = "Selecciona el Tipo de Incidente",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DriverIncidentType.entries.forEach { incidentType ->
                        IncidentTypeCard(
                            incidentType = incidentType,
                            isSelected = selectedIncidentType == incidentType,
                            onClick = { selectedIncidentType = incidentType }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Descripción adicional
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción Detallada del Incidente") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    placeholder = { Text("Describe brevemente lo ocurrido, ubicación y hora aproximada...") },
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botón Enviar
                Button(
                    onClick = {
                        if (selectedIncidentType == null) {
                            Toast.makeText(context, "Por favor, selecciona un tipo de incidente", Toast.LENGTH_SHORT).show()
                        } else {
                            // Llamada a la función de Firebase
                            saveDriverIncidentToFirestore(
                                incidentType = selectedIncidentType!!.title,
                                busNumber = busNumber,
                                routeId = routeAssigned,
                                description = description.ifEmpty { "Reporte sin descripción detallada." }
                            ) { success ->
                                if (success) {
                                    showSuccessDialog = true
                                } else {
                                    Toast.makeText(context, "Error al enviar el reporte a Firebase", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)), // Rojo para incidente
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ReportProblem, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enviar Reporte de Incidente", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Diálogo de éxito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(48.dp)) },
            title = { Text("¡Reporte Enviado!") },
            text = { Text("Tu reporte de incidente ha sido enviado exitosamente. Se tomarán las acciones necesarias.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@Composable
private fun IncidentTypeCard(
    incidentType: DriverIncidentType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFEF9A9A) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = if (isSelected) Color(0xFFB71C1C) else Color(0xFFD32F2F),
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = incidentType.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) Color(0xFFB71C1C) else Color.Black
            )
        }
    }
}