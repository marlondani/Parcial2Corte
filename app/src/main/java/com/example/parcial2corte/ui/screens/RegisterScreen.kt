package com.example.parcial2corte.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.parcial2corte.data.model.Cliente
import com.example.parcial2corte.viewmodel.ClienteViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, clienteViewModel: ClienteViewModel) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var saldo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Registro", 
            style = MaterialTheme.typography.headlineMedium,
            color = colors.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.onSurface.copy(alpha = 0.12f),
                focusedLabelColor = colors.primary,
                unfocusedLabelColor = colors.onSurface.copy(alpha = 0.6f),
                cursorColor = colors.primary,
                focusedTextColor = colors.onSurface,
                unfocusedTextColor = colors.onSurface
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.onSurface.copy(alpha = 0.12f),
                focusedLabelColor = colors.primary,
                unfocusedLabelColor = colors.onSurface.copy(alpha = 0.6f),
                cursorColor = colors.primary,
                focusedTextColor = colors.onSurface,
                unfocusedTextColor = colors.onSurface
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.onSurface.copy(alpha = 0.12f),
                focusedLabelColor = colors.primary,
                unfocusedLabelColor = colors.onSurface.copy(alpha = 0.6f),
                cursorColor = colors.primary,
                focusedTextColor = colors.onSurface,
                unfocusedTextColor = colors.onSurface
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = saldo,
            onValueChange = { saldo = it },
            label = { Text("Saldo inicial") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.onSurface.copy(alpha = 0.12f),
                focusedLabelColor = colors.primary,
                unfocusedLabelColor = colors.onSurface.copy(alpha = 0.6f),
                cursorColor = colors.primary,
                focusedTextColor = colors.onSurface,
                unfocusedTextColor = colors.onSurface
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                scope.launch {
                    try {
                        if (nombre.isBlank() || correo.isBlank() || password.isBlank() || saldo.isBlank()) {
                            error = "Por favor, complete todos los campos"
                        } else {
                            val saldoDouble = saldo.toDoubleOrNull()
                            if (saldoDouble == null) {
                                error = "El saldo debe ser un número válido"
                            } else {
                                val nuevoCliente = Cliente(
                                    nombre = nombre,
                                    correo = correo,
                                    saldo = saldoDouble,
                                    contrasena = password
                                )
                                clienteViewModel.insertCliente(nuevoCliente)
                                Log.d("RegisterScreen", "Cliente insertado correctamente")
                                navController.navigate("login")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("RegisterScreen", "Error al registrar cliente", e)
                        error = "Error al registrar: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primary,
                contentColor = colors.onPrimary
            )
        ) {
            Text("Registrarse")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(
            onClick = { navController.navigate("login") },
            colors = ButtonDefaults.textButtonColors(
                contentColor = colors.primary
            )
        ) {
            Text("¿Ya tienes una cuenta? Inicia sesión")
        }
        if (error.isNotEmpty()) {
            Text(text = error, color = colors.error)
        }
    }
}