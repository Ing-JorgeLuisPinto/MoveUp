package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import com.google.firebase.firestore.ListenerRegistration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRoutesScreen(onNavigateBack: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: "guest"

    var savedRoutes by remember { mutableStateOf(listOf<RouteInfo>()) }
    var listener: ListenerRegistration? by remember { mutableStateOf(null) }

    // 🔥 Escucha en tiempo real los cambios de Firestore
    LaunchedEffect(Unit) {
        listener = db.collection("users")
            .document(userId)
            .collection("saved_routes")
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    val routes = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(RouteInfo::class.java)
                    }
                    savedRoutes = routes
                }
            }
    }

    // Limpieza del listener
    DisposableEffect(Unit) {
        onDispose {
            listener?.remove()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rutas Guardadas") },
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
        if (savedRoutes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes rutas guardadas aún.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF5F6FA))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(savedRoutes) { route ->
                    SavedRouteCard(route, userId, db)
                }
            }
        }
    }
}

@Composable
fun SavedRouteCard(route: RouteInfo, userId: String, db: FirebaseFirestore) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                Text(
                    text = route.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF212121),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                IconButton(onClick = {
                    db.collection("users")
                        .document(userId)
                        .collection("saved_routes")
                        .document(route.fileName)
                        .delete()
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color.Red
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            RouteDetails(route)
        }
    }
}
