package com.example.parcial2corte.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.Parcial2Corte.R
import com.example.parcial2corte.data.model.Cliente
import com.example.parcial2corte.data.model.Producto
import com.example.parcial2corte.data.model.Venta
import com.example.parcial2corte.viewmodel.ClienteViewModel
import com.example.parcial2corte.viewmodel.ProductoViewModel
import com.example.parcial2corte.viewmodel.VentaViewModel
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompraScreen(
    navController: NavController,
    clienteViewModel: ClienteViewModel,
    productoViewModel: ProductoViewModel,
    ventaViewModel: VentaViewModel,
    clienteId: Int,
    productoId: Int
) {
    var cliente by remember { mutableStateOf<Cliente?>(null) }
    var producto by remember { mutableStateOf<Producto?>(null) }
    var cantidad by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val colors = MaterialTheme.colorScheme

    LaunchedEffect(clienteId, productoId) {
        isLoading = true
        try {
            cliente = clienteViewModel.getClienteById(clienteId)
            producto = productoViewModel.getProductoById(productoId)
        } catch (e: Exception) {
            error = "Error al cargar los datos: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = colors.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.compra_de, producto?.nombre ?: ""),
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(24.dp))

                val precioUnitario = producto?.precio ?: 0.0
                Text(
                    text = stringResource(R.string.precio_unitario, precioUnitario),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onSurface
                )
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { if (cantidad > 1) cantidad-- },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                    ) {
                        Text("-", color = colors.onPrimary)
                    }
                    Text(
                        text = cantidad.toString(),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.onBackground
                    )
                    Button(
                        onClick = { if (cantidad < (producto?.stock ?: 0)) cantidad++ },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                    ) {
                        Text("+", color = colors.onPrimary)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                val total = precioUnitario * cantidad
                Text(
                    text = stringResource(R.string.total, total),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = colors.primary
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (cliente == null || producto == null) {
                            error = "Cliente o producto no encontrado"
                        } else if (cantidad > producto!!.stock) {
                            error = "Stock insuficiente"
                        } else {
                            try {
                                val nuevaVenta = Venta(
                                    clienteId = cliente!!.id,
                                    productoId = producto!!.id,
                                    cantidad = cantidad,
                                    total = total,
                                    fecha = Date()
                                )

                                ventaViewModel.agregarVenta(nuevaVenta)

                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Producto agregado al carrito")
                                }

                                navController.navigate("main/${clienteId}")

                            } catch (e: Exception) {
                                error = "Error al agregar al carrito: ${e.message}"
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary,
                        disabledContainerColor = colors.onSurface.copy(alpha = 0.12f),
                        disabledContentColor = colors.onSurface.copy(alpha = 0.38f)
                    ),
                    enabled = cliente != null && producto != null && cantidad <= producto!!.stock
                ) {
                    Text(
                        stringResource(R.string.agregar_al_carrito),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = colors.primary
                )
            }
        }
    }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            error = null
        }
    }
}