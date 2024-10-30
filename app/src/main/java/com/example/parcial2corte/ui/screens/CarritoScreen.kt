package com.example.parcial2corte.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.parcial2corte.data.model.Producto
import com.example.parcial2corte.viewmodel.ClienteViewModel
import com.example.parcial2corte.viewmodel.VentaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    ventaViewModel: VentaViewModel,
    clienteViewModel: ClienteViewModel,
    onNavigateToProductos: () -> Unit,
    onNavigateToMain: () -> Unit,
    clienteId: Int
) {
    val carrito by ventaViewModel.carrito.collectAsState()
    val clienteActual by clienteViewModel.clienteActual.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val colors = MaterialTheme.colorScheme

    LaunchedEffect(clienteId) {
        clienteViewModel.cargarCliente(clienteId)
    }

    Log.d("CarritoScreen", "Cantidad de productos en carrito: ${carrito.size}")
    Log.d("CarritoScreen", "Cliente actual: $clienteActual")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito de Compras", color = colors.onSurface) },
                actions = {
                    IconButton(onClick = onNavigateToProductos) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver a Productos",
                            tint = colors.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = colors.surface
                )
            )
        },
        containerColor = colors.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .background(colors.surface, shape = RoundedCornerShape(8.dp))
            ) {
                items(carrito.toList()) { (producto, cantidad) ->
                    ProductoEnCarritoItem(
                        producto = producto,
                        cantidad = cantidad,
                        onIncrementarCantidad = { ventaViewModel.agregarAlCarrito(producto) },
                        onDecrementarCantidad = { ventaViewModel.quitarDelCarrito(producto) },
                        colors = colors
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Total: $${String.format("%.2f", ventaViewModel.calcularTotal())}",
                style = MaterialTheme.typography.headlineSmall,
                color = colors.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        when {
                            carrito.isEmpty() -> {
                                Toast.makeText(context, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                            }
                            clienteActual == null -> {
                                Toast.makeText(context, "No hay cliente seleccionado", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                val total = ventaViewModel.calcularTotal()
                                val cliente = clienteActual!!
                                if (cliente.saldo >= total) {
                                    ventaViewModel.realizarCompra(
                                        cliente.id,
                                        total,
                                        onSuccess = {
                                            val nuevoSaldo = cliente.saldo - total
                                            clienteViewModel.actualizarSaldo(cliente.id, nuevoSaldo)
                                            ventaViewModel.limpiarCarrito()
                                            clienteViewModel.cargarCliente(cliente.id) // Recargar el cliente para actualizar el saldo
                                            Toast.makeText(context, "Compra realizada con éxito", Toast.LENGTH_SHORT).show()
                                            onNavigateToMain()
                                        },
                                        onError = { errorMessage ->
                                            Toast.makeText(context, "Error al realizar la compra: $errorMessage", Toast.LENGTH_LONG).show()
                                        }
                                    )
                                } else {
                                    Toast.makeText(context, "Saldo insuficiente", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
            ) {
                Text("Realizar Compra", color = colors.onPrimary)
            }
        }
    }
}

@Composable
fun ProductoEnCarritoItem(
    producto: Producto,
    cantidad: Int,
    onIncrementarCantidad: () -> Unit,
    onDecrementarCantidad: () -> Unit,
    colors: ColorScheme
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = producto.nombre, style = MaterialTheme.typography.titleMedium, color = colors.onSurfaceVariant)
                Text(text = "Precio: $${String.format("%.2f", producto.precio)}", style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrementarCantidad) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Decrementar", tint = colors.primary)
                }
                Text(text = cantidad.toString(), modifier = Modifier.padding(horizontal = 8.dp), color = colors.onSurfaceVariant)
                IconButton(onClick = onIncrementarCantidad) {
                    Icon(Icons.Default.Add, contentDescription = "Incrementar", tint = colors.primary)
                }
            }
        }
    }
}