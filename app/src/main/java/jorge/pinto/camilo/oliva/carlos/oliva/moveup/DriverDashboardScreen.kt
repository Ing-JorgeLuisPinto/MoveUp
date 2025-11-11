package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboardScreen(
    onLogout: () -> Unit,
    onNavigateToReport: () -> Unit // <-- ¡AGREGADO! Función para navegar a reportes
) {
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    var isServiceActive by remember { mutableStateOf(false) }

    // Estados de los datos del conductor
    var fullName by remember { mutableStateOf("") }
    var busNumber by remember { mutableStateOf("") }
    var routeAssigned by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Obtener datos del usuario actual
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("drivers").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        fullName = document.getString("fullName") ?: ""
                        busNumber = document.getString("busNumber") ?: ""
                        routeAssigned = document.getString("routeAssigned") ?: ""
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al cargar datos del conductor", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel del Conductor", color = Color.White) },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Cerrar sesión",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2196F3))
            )
        }
    ) { paddingValues ->
        if (isLoading) {
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
                    .padding(16.dp)
            ) {
                // Estado del Servicio
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isServiceActive) Color(0xFF64B5F6) else Color(0xFF90CAF9)
                    ),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isServiceActive) "Servicio Activo" else "Servicio Inactivo",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (isServiceActive) "En ruta" else "Fuera de servicio",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                        Switch(
                            checked = isServiceActive,
                            onCheckedChange = {
                                isServiceActive = it
                                Toast.makeText(
                                    context,
                                    if (it) "Servicio iniciado" else "Servicio detenido",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF1976D2),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFF42A5F5)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Información del Conductor
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
                        Text(
                            text = "Información",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        InfoRow(Icons.Default.Person, "Conductor", fullName)
                        InfoRow(Icons.Default.DirectionsBus, "Bus", busNumber)
                        InfoRow(Icons.Default.Route, "Ruta", routeAssigned)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Opciones del Dashboard
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardOption(
                        icon = Icons.Default.Map,
                        title = "Mi Ruta",
                        description = "Ver ruta asignada",
                        onClick = { Toast.makeText(context, "Mi Ruta", Toast.LENGTH_SHORT).show() }
                    )
                    DashboardOption(
                        icon = Icons.Default.Warning,
                        title = "Reportes",
                        description = "Reportar incidentes",
                        onClick = onNavigateToReport // <-- ¡USO DE LA FUNCIÓN DE NAVEGACIÓN!
                    )
                    DashboardOption(
                        icon = Icons.Default.History,
                        title = "Historial",
                        description = "Ver historial de viajes",
                        onClick = { Toast.makeText(context, "Historial", Toast.LENGTH_SHORT).show() }
                    )
                    DashboardOption(
                        icon = Icons.Default.Settings,
                        title = "Configuración",
                        description = "Ajustes de la cuenta",
                        onClick = { Toast.makeText(context, "Configuración", Toast.LENGTH_SHORT).show() }
                    )
                }
            }
        }
    }

    // Diálogo de confirmación para cerrar sesión
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFF2196F3)) },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro que deseas cerrar sesión?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Text("Sí, cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF2196F3),
            modifier = Modifier.size(20.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun DashboardOption(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFBBDEFB)) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(12.dp),
                    tint = Color(0xFF2196F3)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}