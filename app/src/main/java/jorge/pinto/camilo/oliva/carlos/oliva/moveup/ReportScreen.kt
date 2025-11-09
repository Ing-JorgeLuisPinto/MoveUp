package jorge.pinto.camilo.oliva.carlos.oliva.moveup.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var selectedRoute by remember { mutableStateOf("") }
    var selectedReportType by remember { mutableStateOf<ReportType?>(null) }
    var description by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportar Problema", color = Color.White) },
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
                    containerColor = Color(0xFFFF9800)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Título
            Text(
                text = "¿Qué problema deseas reportar?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Seleccionar Ruta
            Text(
                text = "Ruta afectada",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            var expanded by remember { mutableStateOf(false) }
            val routes = listOf("R1", "R2", "R3", "R4", "R5")

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedRoute,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Selecciona una ruta") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    routes.forEach { route ->
                        DropdownMenuItem(
                            text = { Text(route) },
                            onClick = {
                                selectedRoute = route
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tipos de Reportes
            Text(
                text = "Tipo de problema",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ReportTypeCard(
                    reportType = ReportType.RETRASO,
                    isSelected = selectedReportType == ReportType.RETRASO,
                    onClick = { selectedReportType = ReportType.RETRASO }
                )

                ReportTypeCard(
                    reportType = ReportType.LLENO,
                    isSelected = selectedReportType == ReportType.LLENO,
                    onClick = { selectedReportType = ReportType.LLENO }
                )

                ReportTypeCard(
                    reportType = ReportType.NO_LLEGO,
                    isSelected = selectedReportType == ReportType.NO_LLEGO,
                    onClick = { selectedReportType = ReportType.NO_LLEGO }
                )

                ReportTypeCard(
                    reportType = ReportType.MAL_ESTADO,
                    isSelected = selectedReportType == ReportType.MAL_ESTADO,
                    onClick = { selectedReportType = ReportType.MAL_ESTADO }
                )

                ReportTypeCard(
                    reportType = ReportType.OTRO,
                    isSelected = selectedReportType == ReportType.OTRO,
                    onClick = { selectedReportType = ReportType.OTRO }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Descripción adicional
            Text(
                text = "Descripción (opcional)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Agrega más detalles sobre el problema...") },
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
                    when {
                        selectedRoute.isEmpty() -> {
                            Toast.makeText(context, "Selecciona una ruta", Toast.LENGTH_SHORT).show()
                        }
                        selectedReportType == null -> {
                            Toast.makeText(context, "Selecciona un tipo de problema", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            showSuccessDialog = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Enviar Reporte",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Diálogo de éxito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text("¡Reporte Enviado!") },
            text = { Text("Tu reporte ha sido enviado exitosamente. Gracias por ayudarnos a mejorar el servicio.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@Composable
private fun ReportTypeCard(
    reportType: ReportType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFE0B2) else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = reportType.icon,
                    contentDescription = null,
                    tint = if (isSelected) Color(0xFFFF9800) else Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = reportType.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color(0xFFFF9800) else Color.Black
                    )
                    Text(
                        text = reportType.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Seleccionado",
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

enum class ReportType(
    val title: String,
    val description: String,
    val icon: ImageVector
) {
    RETRASO(
        "Retraso en la ruta",
        "El bus no llegó a tiempo",
        Icons.Default.Schedule
    ),
    LLENO(
        "Bus lleno",
        "No hay espacio para más pasajeros",
        Icons.Default.Group
    ),
    NO_LLEGO(
        "Bus no llegó",
        "El bus no pasó por el paradero",
        Icons.Default.Warning
    ),
    MAL_ESTADO(
        "Mal estado del bus",
        "Problemas con el vehículo",
        Icons.Default.BuildCircle
    ),
    OTRO(
        "Otro problema",
        "Describe el problema que observaste",
        Icons.Default.Info
    )
}