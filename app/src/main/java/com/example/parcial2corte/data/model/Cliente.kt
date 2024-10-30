package com.example.parcial2corte.data.model
import android.util.Patterns
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clientes")
data class Cliente(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "correo") val correo: String,
    @ColumnInfo(name = "contrasena") val contrasena: String,
    @ColumnInfo(name = "saldo") var saldo: Double
){
    init {
        require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
        require(Patterns.EMAIL_ADDRESS.matcher(correo).matches()) { "Formato de correo inválido" }
        require(contrasena.length >= 6) { "La contraseña debe tener al menos 6 caracteres" }
        require(saldo >= 0) { "El saldo no puede ser negativo" }
    }
}