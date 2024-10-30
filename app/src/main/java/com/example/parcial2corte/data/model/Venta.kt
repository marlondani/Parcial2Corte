package com.example.parcial2corte.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "ventas",
    foreignKeys = [
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["id"],
            childColumns = ["producto_id"]
        ),
        ForeignKey(
            entity = Cliente::class,
            parentColumns = ["id"],
            childColumns = ["cliente_id"]
        )
    ]
)
data class Venta(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "producto_id") val productoId: Int,
    @ColumnInfo(name = "cliente_id") val clienteId: Int,
    @ColumnInfo(name = "cantidad") val cantidad: Int,
    @ColumnInfo(name = "total") val total: Double,
    @ColumnInfo(name = "fecha") val fecha: Date
)