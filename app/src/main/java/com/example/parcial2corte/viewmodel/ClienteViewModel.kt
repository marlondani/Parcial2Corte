package com.example.parcial2corte.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial2corte.data.model.Cliente
import com.example.parcial2corte.repository.ClienteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class ClienteViewModel(private val repository: ClienteRepository) : ViewModel() {
    private val _clienteActual = MutableStateFlow<Cliente?>(null)
    val clienteActual: StateFlow<Cliente?> = _clienteActual
    private val _clientes = MutableStateFlow<List<Cliente>>(emptyList())

    private val _clienteAutenticado = MutableStateFlow<Cliente?>(null)

    init {
        viewModelScope.launch {
            repository.getAllClientes().collect {
                _clientes.value = it
            }
        }
    }
    fun cargarCliente(clienteId: Int) {
        viewModelScope.launch {
            _clienteActual.value = repository.getClienteById(clienteId)
        }
    }

    suspend fun getClienteById(id: Int): Cliente? {
        return repository.getClienteById(id)
    }

    suspend fun getClienteByCorreo(correo: String): Cliente? {
        return repository.getClienteByCorreo(correo)
    }

    fun insertCliente(cliente: Cliente) {
        viewModelScope.launch {
            repository.insertCliente(cliente)
        }
    }

    fun actualizarSaldo(clienteId: Int, nuevoSaldo: Double): Flow<Boolean> = flow {
        try {
            repository.actualizarSaldo(clienteId, nuevoSaldo)
            emit(true)
        } catch (e: Exception) {
            Log.e("ClienteViewModel", "Error al actualizar saldo: ${e.message}")
            emit(false)
        }
    }

    fun cerrarSesion() {
        _clienteAutenticado.value = null
    }

}
