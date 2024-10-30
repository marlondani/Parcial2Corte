package com.example.parcial2corte.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.parcial2corte.data.model.Cliente
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Query("SELECT * FROM clientes")
    fun getAllClientes(): Flow<List<Cliente>>

    @Query("SELECT * FROM clientes WHERE id = :id")
    suspend fun getClienteById(id: Int): Cliente?

    @Query("SELECT * FROM clientes WHERE correo = :correo LIMIT 1")
    suspend fun getClienteByCorreo(correo: String): Cliente?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCliente(cliente: Cliente)

    @Update
    suspend fun updateCliente(cliente: Cliente)

    @Delete
    suspend fun deleteCliente(cliente: Cliente)

    @Query("UPDATE clientes SET saldo = saldo - :monto WHERE id = :clienteId")
    suspend fun actualizarSaldo(clienteId: Int, monto: Double)

    @Query("SELECT * FROM clientes WHERE correo = :correo AND contrasena = :contrasena LIMIT 1")
    suspend fun autenticarCliente(correo: String, contrasena: String): Cliente?

    @Query("UPDATE clientes SET contrasena = :nuevaContrasena WHERE id = :clienteId")
    suspend fun actualizarContrasena(clienteId: Int, nuevaContrasena: String)
}