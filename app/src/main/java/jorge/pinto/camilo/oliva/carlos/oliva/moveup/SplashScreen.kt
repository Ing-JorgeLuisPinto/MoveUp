package jorge.pinto.camilo.oliva.carlos.oliva.moveup.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToMainMenu: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2000)
        onNavigateToMainMenu()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2196F3)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(alphaAnim.value)
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsBus,
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp),
                tint = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MoveUp",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Transporte Público",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}