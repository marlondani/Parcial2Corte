package com.example.parcial2corte.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial2corte.data.model.Producto
import com.example.parcial2corte.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductoViewModel(private val repository: ProductoRepository) : ViewModel() {
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    init {
        refreshProductos()
        viewModelScope.launch {
            repository.getAllProductos().collect {
                _productos.value = it
            }
        }
    }

    fun refreshProductos() {
        viewModelScope.launch {
            repository.getAllProductos().collect {
                _productos.value = it
            }
        }
    }

    suspend fun getProductoById(id: Int): Producto? {
        return repository.getProductoById(id)
    }

    suspend fun insertProducto(producto: Producto): Int {
        return repository.insertProducto(producto)
    }

    fun updateProducto(producto: Producto) {
        viewModelScope.launch {
            repository.updateProducto(producto)
        }
    }

}