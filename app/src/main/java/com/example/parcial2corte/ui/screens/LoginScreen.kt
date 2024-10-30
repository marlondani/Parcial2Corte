package com.example.parcial2corte.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.parcial2corte.viewmodel.ClienteViewModel
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController, 
    clienteViewModel: ClienteViewModel,
    isDarkTheme: Boolean,
    onThemeChanged: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Login") },
                actions = {
                    IconButton(onClick = onThemeChanged) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Cambiar tema"
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = colors.surface,
                    titleContentColor = colors.onSurface,
                    actionIconContentColor = colors.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.headlineMedium,
                color = colors.onBackground
            )
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.onSurface.copy(alpha = 0.12f),
                    focusedLabelColor = colors.primary,
                    cursorColor = colors.primary
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.onSurface.copy(alpha = 0.12f),
                    focusedLabelColor = colors.primary,
                    cursorColor = colors.primary
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    scope.launch {
                        try {
                            if (correo.isBlank() || password.isBlank()) {
                                error = "Por favor, complete todos los campos"
                            } else {
                                val cliente = clienteViewModel.getClienteByCorreo(correo)
                                if (cliente != null) {
                                    Log.d("LoginScreen", "Cliente encontrado: ${cliente.nombre}")
                                    if (password == cliente.contrasena) {
                                        error = ""
                                        Log.d("LoginScreen", "Autenticación exitosa para ${cliente.nombre}")
                                        navController.navigate("main/${cliente.id}")
                                    } else {
                                        error = "Contraseña incorrecta"
                                        Log.d("LoginScreen", "Contraseña incorrecta para ${cliente.nombre}")
                                    }
                                } else {
                                    error = "No se encontró un usuario con este correo"
                                    Log.d("LoginScreen", "Usuario no encontrado para correo: $correo")
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("LoginScreen", "Error durante el inicio de sesión", e)
                            error = "Error al iniciar sesión: ${e.message}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                )
            ) {
                Text("Iniciar Sesión")
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = { navController.navigate("register") },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = colors.primary
                )
            ) {
                Text("¿No tienes una cuenta? Regístrate")
            }
            if (error.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = colors.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}