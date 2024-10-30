package com.example.parcial2corte.data.dao

import android.util.Log
import androidx.room.*
import com.example.parcial2corte.data.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Query("SELECT * FROM productos")
    fun getAllProductos(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getProductoById(id: Int): Producto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducto(producto: Producto): Long

    @Update
    suspend fun updateProducto(producto: Producto) {
        Log.d("ProductoDao", "Updating producto: $producto")
    }

    @Delete
    suspend fun deleteProducto(producto: Producto) {
        Log.d("ProductoDao", "Deleting producto: $producto")
    }

    @Query("UPDATE productos SET stock = stock - :cantidad WHERE id = :productoId")
    suspend fun decrementarStock(productoId: Int, cantidad: Int) {
        Log.d("ProductoDao", "Decrementing stock for producto ID: $productoId by $cantidad")
    }

    @Query("UPDATE productos SET stock = stock + :cantidad WHERE id = :productoId")
    suspend fun incrementarStock(productoId: Int, cantidad: Int) {
        Log.d("ProductoDao", "Incrementing stock for producto ID: $productoId by $cantidad")
    }

    @Query("SELECT * FROM productos WHERE stock > 0")
    fun getProductosEnStock(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :searchQuery || '%'")
    fun searchProductos(searchQuery: String): Flow<List<Producto>>

    @Query("SELECT * FROM productos ORDER BY precio ASC")
    fun getProductosOrderedByPriceAsc(): Flow<List<Producto>>

    @Query("SELECT * FROM productos ORDER BY precio DESC")
    fun getProductosOrderedByPriceDesc(): Flow<List<Producto>>

    @Transaction
    suspend fun actualizarStockMultiple(updates: List<Pair<Int, Int>>) {
        for ((productoId, cantidad) in updates) {
            decrementarStock(productoId, cantidad)
        }
    }
    @Query("UPDATE productos SET stock = :nuevoStock WHERE id = :productoId")
    suspend fun actualizarStockProducto(productoId: Int, nuevoStock: Int)
}