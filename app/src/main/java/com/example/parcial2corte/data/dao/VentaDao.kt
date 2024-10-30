package com.example.parcial2corte.data.dao

import androidx.room.*
import com.example.parcial2corte.data.model.Venta
import kotlinx.coroutines.flow.Flow

@Dao
interface VentaDao {
    @Query("SELECT * FROM ventas")
    fun getAllVentas(): Flow<List<Venta>>

    @Query("SELECT * FROM ventas WHERE cliente_id = :clienteId")
    fun getVentasByCliente(clienteId: Int): Flow<List<Venta>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVenta(venta: Venta)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVentas(ventas: List<Venta>)

    @Update
    suspend fun updateVenta(venta: Venta)

    @Delete
    suspend fun deleteVenta(venta: Venta)

    @Query("SELECT * FROM ventas WHERE cliente_id = :clienteId ORDER BY fecha DESC LIMIT :limit")
    fun getRecentVentasByCliente(clienteId: Int, limit: Int): Flow<List<Venta>>

    @Query("SELECT SUM(total) FROM ventas WHERE cliente_id = :clienteId")
    fun getTotalGastadoByCliente(clienteId: Int): Flow<Double?>

    @Query("SELECT COUNT(*) FROM ventas WHERE cliente_id = :clienteId")
    fun getCountVentasByCliente(clienteId: Int): Flow<Int>

    @Transaction
    suspend fun realizarCompra(ventas: List<Venta>) {
        insertVentas(ventas)
    }
}