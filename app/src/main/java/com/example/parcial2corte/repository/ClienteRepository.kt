package com.example.parcial2corte.repository

import com.example.parcial2corte.data.dao.ClienteDao
import com.example.parcial2corte.data.model.Cliente
import kotlinx.coroutines.flow.Flow

class ClienteRepository(private val clienteDao: ClienteDao) {
    fun getAllClientes(): Flow<List<Cliente>> = clienteDao.getAllClientes()

    suspend fun getClienteById(id: Int): Cliente? = clienteDao.getClienteById(id)

    suspend fun getClienteByCorreo(correo: String): Cliente? = clienteDao.getClienteByCorreo(correo)

    suspend fun insertCliente(cliente: Cliente) = clienteDao.insertCliente(cliente)

    suspend fun updateCliente(cliente: Cliente) = clienteDao.updateCliente(cliente)

    suspend fun deleteCliente(cliente: Cliente) = clienteDao.deleteCliente(cliente)

    suspend fun actualizarSaldo(clienteId: Int, monto: Double) = clienteDao.actualizarSaldo(clienteId, monto)

    suspend fun autenticarCliente(correo: String, contrasena: String): Cliente? =
        clienteDao.autenticarCliente(correo, contrasena)

    suspend fun actualizarContrasena(clienteId: Int, nuevaContrasena: String) =
        clienteDao.actualizarContrasena(clienteId, nuevaContrasena)
}