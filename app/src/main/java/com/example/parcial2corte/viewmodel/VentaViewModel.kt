package com.example.parcial2corte.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial2corte.data.model.Cliente
import com.example.parcial2corte.data.model.Producto
import com.example.parcial2corte.data.model.Venta
import com.example.parcial2corte.repository.ClienteRepository
import com.example.parcial2corte.repository.ProductoRepository
import com.example.parcial2corte.repository.VentaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class VentaViewModel(
    private val ventaRepository: VentaRepository,
    private val productoRepository: ProductoRepository,
    private val clienteRepository: ClienteRepository
) : ViewModel() {

    private val _carrito = MutableStateFlow<Map<Producto, Int>>(emptyMap())
    val carrito: StateFlow<Map<Producto, Int>> = _carrito

    private val _clienteActual = MutableStateFlow<Cliente?>(null)

    fun agregarAlCarrito(producto: Producto) {
        val cantidadActual = _carrito.value[producto] ?: 0
        _carrito.value += (producto to (cantidadActual + 1))
        Log.d("VentaViewModel", "Carrito actualizado: $_carrito")
    }

    fun quitarDelCarrito(producto: Producto) {
        val cantidadActual = _carrito.value[producto]
        if (cantidadActual != null && cantidadActual > 1) {
            _carrito.value += (producto to (cantidadActual - 1))
        } else {
            _carrito.value -= producto
        }
    }

    fun limpiarCarrito() {
        _carrito.value = emptyMap()
    }

    fun calcularTotal(): Double {
        return _carrito.value.entries.sumOf { (producto, cantidad) -> producto.precio * cantidad }
    }

    private val _stockActualizado = MutableStateFlow(false)
    val stockActualizado: StateFlow<Boolean> = _stockActualizado

    fun realizarCompra(clienteId: Int, total: Double, onSuccess: () -> Unit, onError: (String) -> Unit) {
    viewModelScope.launch {
        try {
            val cliente = clienteRepository.getClienteById(clienteId) ?: throw Exception("Cliente no encontrado")

            if (cliente.saldo < total) {
                throw Exception("Saldo insuficiente")
            }

            val ventas = _carrito.value.map { (producto, cantidad) ->
                Venta(
                    clienteId = clienteId,
                    productoId = producto.id,
                    cantidad = cantidad,
                    total = producto.precio * cantidad,
                    fecha = Date()
                )
            }

            ventaRepository.realizarCompra(ventas)

            _carrito.value.forEach { (producto, cantidad) ->
                val nuevoStock = producto.stock - cantidad
                productoRepository.actualizarStockProducto(producto.id, nuevoStock)
            }

            val nuevoSaldo = cliente.saldo - total
            clienteRepository.updateCliente(cliente.copy(saldo = nuevoSaldo))

            _clienteActual.value = cliente.copy(saldo = nuevoSaldo)

            limpiarCarrito()

            _stockActualizado.value = true

            onSuccess()
        } catch (e: Exception) {
            onError(e.message ?: "Error desconocido al realizar la compra")
        }
    }
}
        fun resetStockActualizado() {
            _stockActualizado.value = false
        }

    fun agregarVenta(venta: Venta) {
        viewModelScope.launch {
            try {
                val productoActual = productoRepository.getProductoById(venta.productoId) ?: throw Exception("Producto no encontrado")
                val cantidadActual = _carrito.value[productoActual] ?: 0
                _carrito.value += (productoActual to (cantidadActual + venta.cantidad))
            } catch (e: Exception) {
                Log.e("VentaViewModel", "Error al agregar venta: ${e.message}")
            }
        }
    }
}