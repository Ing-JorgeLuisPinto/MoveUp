package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class RouteInfo(
    val fileName: String = "",
    val name: String = "",
    val origin: String = "",
    val destination: String = "",
    val duration: String = "",
    val frequency: String = "",
    val price: String = "",
    val service: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchRouteScreen(
    onNavigateBack: () -> Unit,
    onRouteClick: (String) -> Unit
) {
    val routes = listOf(
        RouteInfo("ruta_1", "GALÁN - MODELO - ESTADIO - UIS (INTEGRADA)", "Galán", "UIS", "35 min", "Cada 10 min", "$2,800"),
        RouteInfo("ruta_2", "CUMBRE - EL CARMEN - TRANSVERSAL - CARRERA 33 - SANTOTO", "La Cumbre", "Santoto", "40 min", "Cada 12 min", "$2,800"),
        RouteInfo("ruta_3", "BOSCONIA - LOS ANGELES - CHAPINERO - CENTRO - REAL DE MINAS - LA LADRILLERA (INTEGRADA)", "Bosconia", "La Ladrillera", "45 min", "Cada 15 min", "$2,800"),
        RouteInfo("ruta_4", "LA JOYA - ESTACION SAN MATEO - DON BOSCO (INTEGRADA)", "La Joya", "Don Bosco", "30 min", "Cada 9 min", "$2,800"),
        RouteInfo("ruta_5", "CRISTAL BAJO - SAN LUIS - DIAMANTE II - ESTACION PROVENZA - CACIQUE - CARRERA 33 - ALVAREZ - BUENAVISTA (INTEGRADA)", "Cristal Bajo", "Buenavista", "50 min", "Cada 20 min", "$2,800")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar Ruta") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F6FA))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(routes) { route ->
                RouteCardFirestore(route = route, onClick = { onRouteClick(route.fileName) })
            }
        }
    }
}

@Composable
fun RouteCardFirestore(route: RouteInfo, onClick: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: "guest"

    var isSaved by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }

    // Leer desde Firestore (callback normal)
    LaunchedEffect(Unit) {
        db.collection("users")
            .document(userId)
            .collection("saved_routes")
            .document(route.fileName)
            .get()
            .addOnSuccessListener { snapshot ->
                isSaved = snapshot.exists()
                loading = false
            }
            .addOnFailureListener {
                loading = false
            }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF1565C0), shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "R" + route.fileName.takeLast(1),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (route.service) "En servicio" else "Fuera de servicio",
                        color = if (route.service) Color(0xFF4CAF50) else Color.Red,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (!loading) {
                    IconButton(onClick = {
                        if (isSaved) {
                            db.collection("users")
                                .document(userId)
                                .collection("saved_routes")
                                .document(route.fileName)
                                .delete()
                            isSaved = false
                        } else {
                            db.collection("users")
                                .document(userId)
                                .collection("saved_routes")
                                .document(route.fileName)
                                .set(route)
                            isSaved = true
                        }
                    }) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Guardar",
                            tint = if (isSaved) Color(0xFFFFA000) else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = route.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF212121),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))
            RouteDetails(route)
        }
    }
}

@Composable
fun RouteDetails(route: RouteInfo) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF4CAF50))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = route.origin, fontSize = 14.sp, color = Color.Gray)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Flag, contentDescription = null, tint = Color(0xFFE53935))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = route.destination, fontSize = 14.sp, color = Color.Gray)
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        InfoChip(Icons.Default.AccessTime, route.duration)
        InfoChip(Icons.Default.Refresh, route.frequency)
        InfoChip(Icons.Default.AttachMoney, route.price)
    }
}

@Composable
fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Surface(color = Color(0xFFE3F2FD), shape = RoundedCornerShape(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF1565C0), modifier = Modifier.size(16.dp))
            Text(text = text, fontSize = 13.sp, color = Color(0xFF1565C0))
        }
    }
}
