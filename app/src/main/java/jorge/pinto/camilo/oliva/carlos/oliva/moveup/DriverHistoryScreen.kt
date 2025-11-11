package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Modelo de datos para el historial
data class Trip(
    val id: String,
    val date: String,
    val routeNumber: String,
    val duration: String,
    val distanceKm: Double,
    val stopsCount: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverHistoryScreen(onNavigateBack: () -> Unit) {
    var trips by remember { mutableStateOf(emptyList<Trip>()) }
    var isLoading by remember { mutableStateOf(true) }

    // SIMULACIÓN DE CARGA DE DATOS (REEMPLAZAR CON FIREBASE)
    LaunchedEffect(Unit) {
        // En un proyecto real: db.collection("driverTrips").whereEqualTo("driverId", userId).get()
        trips = listOf(
            Trip("001", "10 Nov 2025", "R4", "2h 15m", 45.2, 18),
            Trip("002", "09 Nov 2025", "R2", "1h 50m", 38.5, 15),
            Trip("003", "08 Nov 2025", "R4", "2h 20m", 46.1, 19),
            Trip("004", "07 Nov 2025", "R5", "1h 40m", 32.9, 14),
            Trip("005", "06 Nov 2025", "R1", "2h 00m", 40.0, 16)
        )
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Viajes", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2196F3))
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF2196F3))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        "Resumen de tus últimos viajes completados",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(trips) { trip ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(36.dp))
                            Spacer(Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Ruta ${trip.routeNumber} - ${trip.date}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text("Duración: ${trip.duration} | Distancia: ${String.format("%.1f", trip.distanceKm)} km", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Text("Paradas visitadas: ${trip.stopsCount}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }

                            Icon(Icons.Default.ChevronRight, contentDescription = "Detalle", tint = Color.LightGray)
                        }
                    }
                }
            }
        }
    }
}