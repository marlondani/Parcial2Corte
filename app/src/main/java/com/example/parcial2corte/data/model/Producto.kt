package com.example.parcial2corte.data.model
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "descripcion") val descripcion: String,
    @ColumnInfo(name = "precio") val precio: Double,
    @ColumnInfo(name = "stock") val stock: Int,
    @ColumnInfo(name = "imagen") val imagen: String
) {
    init {
        require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
        require(precio >= 0) { "El precio no puede ser negativo" }
        require(stock >= 0) { "El stock no puede ser negativo" }
    }
}