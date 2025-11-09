package jorge.pinto.camilo.oliva.carlos.oliva.moveup.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import jorge.pinto.camilo.oliva.carlos.oliva.moveup.data.BusStop
import jorge.pinto.camilo.oliva.carlos.oliva.moveup.data.getRouteById

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailScreen(
    routeId: String,
    onNavigateBack: () -> Unit
) {
    val route = remember { getRouteById(routeId) }

    if (route == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray
                )
                Text(
                    text = "Ruta no encontrada",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Gray
                )
                Button(onClick = onNavigateBack) {
                    Text("Volver")
                }
            }
        }
        return
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            route.pathPoints.firstOrNull() ?: LatLng(7.1193, -73.1227),
            13f
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(route.name, color = Color.White) },
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
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Mapa de la ruta
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            scrollGesturesEnabled = true,
                            zoomGesturesEnabled = true
                        )
                    ) {
                        // Dibujar polilínea
                        Polyline(
                            points = route.pathPoints,
                            color = Color(0xFF2196F3),
                            width = 10f,
                            geodesic = true
                        )

                        // Marcador de inicio
                        route.stops.firstOrNull()?.let { firstStop ->
                            Marker(
                                state = MarkerState(position = firstStop.location),
                                title = firstStop.name,
                                snippet = "Origen",
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                            )
                        }

                        // Marcador de fin
                        route.stops.lastOrNull()?.let { lastStop ->
                            Marker(
                                state = MarkerState(position = lastStop.location),
                                title = lastStop.name,
                                snippet = "Destino",
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                            )
                        }

                        // Paraderos intermedios
                        route.stops.drop(1).dropLast(1).forEach { stop ->
                            Marker(
                                state = MarkerState(position = stop.location),
                                title = stop.name,
                                snippet = "Parada #${stop.order}",
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                            )
                        }
                    }
                }
            }

            // Información de la ruta
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Número de ruta y estado
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF2196F3)
                            ) {
                                Text(
                                    text = route.number,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            StatusBadge(status = route.status)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Información en cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            InfoCard(
                                icon = Icons.Default.Schedule,
                                title = "Duración",
                                value = route.duration,
                                modifier = Modifier.weight(1f)
                            )
                            InfoCard(
                                icon = Icons.Default.AccessTime,
                                title = "Frecuencia",
                                value = route.frequency,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            InfoCard(
                                icon = Icons.Default.AttachMoney,
                                title = "Tarifa",
                                value = route.fare,
                                modifier = Modifier.weight(1f)
                            )
                            InfoCard(
                                icon = Icons.Default.Business,
                                title = "Empresa",
                                value = route.company,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Horario
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFFF5F5F5),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = Color(0xFF2196F3),
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "Horario de servicio",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                Text(
                                    text = route.schedule,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Lista de paraderos
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF2196F3)
                            )
                            Text(
                                text = "Paraderos (${route.stops.size})",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // Items de paraderos
            itemsIndexed(route.stops) { index, stop ->
                StopItem(
                    stop = stop,
                    isFirst = index == 0,
                    isLast = index == route.stops.size - 1
                )
            }

            // Espaciado final
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val backgroundColor = when (status.lowercase()) {
        "en servicio" -> Color(0xFFE8F5E9)
        "demorado" -> Color(0xFFFFF3E0)
        "fuera de servicio" -> Color(0xFFFFEBEE)
        else -> Color(0xFFE0E0E0)
    }

    val textColor = when (status.lowercase()) {
        "en servicio" -> Color(0xFF4CAF50)
        "demorado" -> Color(0xFFFF9800)
        "fuera de servicio" -> Color(0xFFF44336)
        else -> Color(0xFF757575)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF5F5F5)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF424242)
            )
        }
    }
}

@Composable
private fun StopItem(
    stop: BusStop,
    isFirst: Boolean,
    isLast: Boolean
) {
    val iconColor = when {
        isFirst -> Color(0xFF4CAF50)
        isLast -> Color(0xFFF44336)
        else -> Color(0xFF2196F3)
    }

    val icon = when {
        isFirst -> Icons.Default.RadioButtonChecked
        isLast -> Icons.Default.LocationOn
        else -> Icons.Default.Circle
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            if (!isFirst) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(20.dp)
                        .background(Color(0xFFE0E0E0))
                )
            }

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(if (isFirst || isLast) 24.dp else 16.dp)
            )

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(20.dp)
                        .background(Color(0xFFE0E0E0))
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Parada #${stop.order}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = stop.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF212121)
            )
        }

        if (isFirst) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFE8F5E9)
            ) {
                Text(
                    text = "Inicio",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4CAF50)
                )
            }
        } else if (isLast) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFFEBEE)
            ) {
                Text(
                    text = "Final",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFF44336)
                )
            }
        }
    }
}