package com.example.parcial2corte.repository

import com.example.parcial2corte.data.dao.VentaDao
import com.example.parcial2corte.data.model.Venta

class VentaRepository(private val ventaDao: VentaDao) {
    suspend fun realizarCompra(ventas: List<Venta>) = ventaDao.realizarCompra(ventas)
}