package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Icon(
                imageVector = Icons.Default.DirectionsBus,
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp),
                tint = Color(0xFF2196F3)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "MoveUp",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2196F3)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Transporte Público",
                fontSize = 16.sp,
                color = Color(0xFF757575)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Card con formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Iniciar Sesión",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Ingresa tus credenciales",
                        fontSize = 14.sp,
                        color = Color(0xFF757575)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            focusedLabelColor = Color(0xFF2196F3),
                            focusedLeadingIconColor = Color(0xFF2196F3)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Ocultar" else "Mostrar"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            focusedLabelColor = Color(0xFF2196F3),
                            focusedLeadingIconColor = Color(0xFF2196F3)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        fontSize = 14.sp,
                        color = Color(0xFF2196F3),
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable {
                                Toast.makeText(context, "Función próximamente", Toast.LENGTH_SHORT).show()
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        isLoading = true
                        scope.launch {
                            try {
                                auth.signInWithEmailAndPassword(email, password).await()
                                Toast.makeText(context, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                                onNavigateToHome()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("Iniciar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text(
                    text = "¿No tienes cuenta? ",
                    color = Color(0xFF757575),
                    fontSize = 14.sp
                )
                Text(
                    text = "Regístrate",
                    color = Color(0xFF2196F3),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(enabled = !isLoading) {
                        onNavigateToRegister()
                    }
                )
            }
        }
    }
}