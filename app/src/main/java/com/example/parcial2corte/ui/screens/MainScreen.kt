package com.example.parcial2corte.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.parcial2corte.data.model.Producto
import com.example.parcial2corte.viewmodel.ClienteViewModel
import com.example.parcial2corte.viewmodel.ProductoViewModel
import com.example.parcial2corte.viewmodel.VentaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    clienteViewModel: ClienteViewModel,
    productoViewModel: ProductoViewModel,
    ventaViewModel: VentaViewModel,
    clienteId: Int
) {
    val clienteState by clienteViewModel.clienteActual.collectAsState()
    val productos by productoViewModel.productos.collectAsState(initial = emptyList())
    val stockActualizado by ventaViewModel.stockActualizado.collectAsState()

    LaunchedEffect(stockActualizado) {
        if (stockActualizado) {
            productoViewModel.refreshProductos()
            ventaViewModel.resetStockActualizado()
        }
    }

    LaunchedEffect(clienteId) {
        clienteViewModel.cargarCliente(clienteId)
        productoViewModel.refreshProductos()
    }

    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tienda Virtual", color = colors.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.primary
                ),
                actions = {
                    IconButton(onClick = { navController.navigate("carrito/$clienteId") }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito", tint = colors.onPrimary)
                    }
                    IconButton(
                        onClick = {
                            clienteViewModel.cerrarSesion()
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión", tint = colors.onPrimary)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bienvenido, ${clienteState?.nombre ?: ""}",
                style = MaterialTheme.typography.headlineSmall,
                color = colors.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Saldo: $${String.format("%.2f", clienteState?.saldo ?: 0.0)}",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Productos disponibles:",
                style = MaterialTheme.typography.titleLarge,
                color = colors.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(productos) { producto ->
                    ProductoItem(producto, navController, clienteId)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoItem(producto: Producto, navController: NavController, clienteId: Int) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = { navController.navigate("compra/$clienteId/${producto.id}") },
        colors = CardDefaults.cardColors(
            containerColor = colors.surface,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = producto.imagen,
                contentDescription = "Imagen de ${producto.nombre}",
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface
                )
                Text(
                    text = "$${String.format("%.2f", producto.precio)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.primary
                )
                Text(
                    text = "Stock: ${producto.stock}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
                Text(
                    text = producto.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}