package com.example.parcial2corte

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.parcial2corte.data.AppDatabase
import com.example.parcial2corte.data.model.Producto
import com.example.parcial2corte.repository.ClienteRepository
import com.example.parcial2corte.repository.ProductoRepository
import com.example.parcial2corte.repository.VentaRepository
import com.example.parcial2corte.ui.screens.*
import com.example.parcial2corte.ui.theme.Parcial2CorteTheme
import com.example.parcial2corte.viewmodel.ClienteViewModel
import com.example.parcial2corte.viewmodel.ProductoViewModel
import com.example.parcial2corte.viewmodel.VentaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    private val ventaViewModel: VentaViewModel by viewModels {
        ViewModelFactory {
            VentaViewModel(
                VentaRepository(database.ventaDao()),
                ProductoRepository(database.productoDao()),
                ClienteRepository(database.clienteDao())
            )
        }
    }

    private val clienteViewModel: ClienteViewModel by viewModels {
        ViewModelFactory { ClienteViewModel(ClienteRepository(database.clienteDao())) }
    }

    private val productoViewModel: ProductoViewModel by viewModels {
        ViewModelFactory { ProductoViewModel(ProductoRepository(database.productoDao())) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            try {
                insertarProductosEjemplo()
            } catch (e: Exception) {
                Log.e("MainActivity", "Error al insertar productos", e)
            }
        }

        setContent {

            var isDarkTheme by remember { mutableStateOf(false) }

            Parcial2CorteTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation(
                        clienteViewModel,
                        productoViewModel,
                        ventaViewModel,
                        isDarkTheme = isDarkTheme,
                        onThemeChanged = { isDarkTheme = !isDarkTheme }
                    )
                }
            }
        }
    }

    private suspend fun insertarProductosEjemplo() {
        withContext(Dispatchers.IO) {
            val productos = listOf(
                Producto(1, "Funda Protectora", "Funda resistente y elegante para tu smartphone", 19900.0, 50, "https://imgur.com/QG2hgLt.jpg"),
                Producto(2, "Power Bank 10000mAh", "Batería externa de alta capacidad para tus dispositivos", 134900.0, 30, "https://imgur.com/EzbxbIU.jpg"),
                Producto(3, "Auriculares Bluetooth", "Auriculares inalámbricos con cancelación de ruido", 269900.0, 25, "https://m.media-amazon.com/images/I/61Zh467pKjL.AC_UL400.jpg"),
                Producto(4, "Soporte de Escritorio", "Soporte ajustable para tu teléfono o tablet", 40000.0, 40, "https://imgur.com/LPu3XA6.jpg"),
                Producto(5, "Vidrio Templado Pantalla", "Protección máxima contra rayones y golpes", 20900.0, 100, "https://imgur.com/1b6t9vQ.jpg"),
                Producto(6, "Cable USB-C Trenzado", "Cable de carga rápida y transferencia de datos", 58900.0, 75, "https://imgur.com/z64ktM5.jpg"),
                Producto(7, "Cable USB-C a J3.5mm", "Convierte tu puerto USB-C en un jack de audio", 19900.0, 60, "https://imgur.com/3RPrkPA.jpg"),
                Producto(8, "Selfie Stick con Bluetooth", "Palo de selfie extensible con control remoto", 112900.0, 35, "https://imgur.com/zexb0jS.jpg"),
                Producto(9, "Anillo Soporte Teléfono", "Soporte giratorio y adhesivo para tu smartphone", 35900.0, 80, "https://imgur.com/nlsksk8.jpg"),
                Producto(10, "Kit de Lentes de Cámara", "Set de lentes para mejorar tus fotos móviles", 157900.0, 20, "https://imgur.com/U1fqdYy.jpg")
            )

            productos.forEach { producto ->
                val existingProducto = productoViewModel.getProductoById(producto.id)
                if (existingProducto == null) {
                    productoViewModel.insertProducto(producto)
                } else {
                    productoViewModel.updateProducto(producto)
                }
            }
        }
    }
}

class ViewModelFactory<T>(private val creator: () -> T) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return creator() as T
    }
}

@Composable
fun MainNavigation(
    clienteViewModel: ClienteViewModel,
    productoViewModel: ProductoViewModel,
    ventaViewModel: VentaViewModel,
    isDarkTheme: Boolean,
    onThemeChanged: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController,
                clienteViewModel,
                isDarkTheme,
                onThemeChanged)
        }
        composable("register") {
            RegisterScreen(navController,
                clienteViewModel)
        }
        composable(
            "main/{clienteId}",
            arguments = listOf(navArgument("clienteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getInt("clienteId") ?: 0
            MainScreen(
                navController,
                clienteViewModel,
                productoViewModel,
                ventaViewModel,
                clienteId
            )
        }
        composable(
            "compra/{clienteId}/{productoId}",
            arguments = listOf(
                navArgument("clienteId") { type = NavType.IntType },
                navArgument("productoId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getInt("clienteId") ?: 0
            val productoId = backStackEntry.arguments?.getInt("productoId") ?: 0
            CompraScreen(
                navController = navController,
                clienteViewModel = clienteViewModel,
                productoViewModel = productoViewModel,
                ventaViewModel = ventaViewModel,
                clienteId = clienteId,
                productoId = productoId
            )
        }
        composable(
            "carrito/{clienteId}",
            arguments = listOf(navArgument("clienteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getInt("clienteId") ?: 0
            CarritoScreen(
                ventaViewModel = ventaViewModel,
                clienteViewModel = clienteViewModel, // Agregar esto
                onNavigateToProductos = { navController.popBackStack() },
                onNavigateToMain = { navController.navigate("main/$clienteId") },
                clienteId = clienteId
            )
        }
    }
}
