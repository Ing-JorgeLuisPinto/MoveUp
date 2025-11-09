package jorge.pinto.camilo.oliva.carlos.oliva.moveup.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MainMenuScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToMyRoutes: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToDriverLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Icon(
            imageVector = Icons.Default.DirectionsBus,
            contentDescription = "Logo",
            modifier = Modifier.size(80.dp),
            tint = Color(0xFF2196F3)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "MoveUp",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2196F3)
        )

        Text(
            text = "Tu transporte público",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Grid de opciones 2x2
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuOption(
                    icon = Icons.Default.Search,
                    title = "Buscar Ruta",
                    onClick = onNavigateToSearch,
                    modifier = Modifier.weight(1f)
                )
                MenuOption(
                    icon = Icons.Default.Map,
                    title = "Ver Mapa",
                    onClick = onNavigateToMap,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuOption(
                    icon = Icons.Default.Bookmark,
                    title = "Mis Rutas",
                    onClick = onNavigateToMyRoutes,
                    modifier = Modifier.weight(1f)
                )
                MenuOption(
                    icon = Icons.Default.Warning,
                    title = "Reportar",
                    onClick = onNavigateToReport,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón Portal de Conductores
        Button(
            onClick = onNavigateToDriverLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF9800)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Portal de Conductores",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MenuOption(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = Color(0xFF2196F3)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF424242)
            )
        }
    }
}