package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Route(
    val id: String,
    val name: String,
    val origin: String,
    val destination: String,
    val duration: String,
    val price: String,
    var isFavorite: Boolean = false
)

@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }
    var routes by remember {
        mutableStateOf(
            listOf(
                Route("1", "Ruta 1", "Centro", "Norte", "25 min", "$2.500"),
                Route("2", "Ruta 2", "Sur", "Centro", "30 min", "$2.500"),
                Route("3", "Ruta 3", "Este", "Oeste", "20 min", "$2.000"),
                Route("4", "Ruta 4", "Terminal", "Universidad", "35 min", "$3.000"),
                Route("5", "Ruta 5", "Cabecera", "Centro", "15 min", "$2.000")
            )
        )
    }

    val filteredRoutes = routes.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.origin.contains(searchQuery, ignoreCase = true) ||
                it.destination.contains(searchQuery, ignoreCase = true)
    }

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Buscar Rutas",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar ruta, origen o destino") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Buscar")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredRoutes) { route ->
                RouteCard(
                    route = route,
                    onFavoriteClick = {
                        val index = routes.indexOf(route)
                        val updatedRoutes = routes.toMutableList()
                        updatedRoutes[index] = route.copy(isFavorite = !route.isFavorite)
                        routes = updatedRoutes

                        // Guardar en Firestore
                        scope.launch {
                            try {
                                val userId = auth.currentUser?.uid ?: return@launch
                                val favoriteData = hashMapOf(
                                    "routeId" to route.id,
                                    "routeName" to route.name,
                                    "isFavorite" to !route.isFavorite,
                                    "timestamp" to System.currentTimeMillis()
                                )

                                firestore.collection("users")
                                    .document(userId)
                                    .collection("favorites")
                                    .document(route.id)
                                    .set(favoriteData)
                                    .await()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun RouteCard(
    route: Route,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Navegar a detalles */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsBus,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = route.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${route.origin} → ${route.destination}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Text(
                        text = route.duration,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = route.price,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (route.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (route.isFavorite) Color.Red else Color.Gray
                )
            }
        }
    }
}