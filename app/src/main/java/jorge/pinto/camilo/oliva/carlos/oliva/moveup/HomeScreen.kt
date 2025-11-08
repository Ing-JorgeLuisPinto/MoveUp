package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    val items = listOf(
        BottomNavItem("Mapa", Icons.Default.Map),
        BottomNavItem("Buscar", Icons.Default.Search),
        BottomNavItem("Perfil", Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index }
                    )
                }
            }
        }
    ) { paddingValues ->
        when (selectedIndex) {
            0 -> MapScreen(modifier = Modifier.padding(paddingValues))
            1 -> SearchScreen(modifier = Modifier.padding(paddingValues))
            2 -> ProfileScreen(
                modifier = Modifier.padding(paddingValues),
                onLogout = onLogout
            )
        }
    }
}