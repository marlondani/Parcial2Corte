package com.example.parcial2corte.repository

import com.example.parcial2corte.data.dao.ProductoDao
import com.example.parcial2corte.data.model.Producto
import kotlinx.coroutines.flow.Flow

class ProductoRepository(private val productoDao: ProductoDao) {
    fun getAllProductos(): Flow<List<Producto>> = productoDao.getAllProductos()

    suspend fun getProductoById(id: Int): Producto? = productoDao.getProductoById(id)

    suspend fun insertProducto(producto: Producto): Int {
        val id = productoDao.insertProducto(producto)
        return id.toInt()
    }

    suspend fun updateProducto(producto: Producto) = productoDao.updateProducto(producto)

    suspend fun deleteProducto(producto: Producto) = productoDao.deleteProducto(producto)

    suspend fun decrementarStock(productoId: Int, cantidad: Int) = productoDao.decrementarStock(productoId, cantidad)

    suspend fun actualizarStockProducto(productoId: Int, nuevoStock: Int) {
        productoDao.actualizarStockProducto(productoId, nuevoStock)
    }
}