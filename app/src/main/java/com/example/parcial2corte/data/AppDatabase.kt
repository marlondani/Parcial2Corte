package com.example.parcial2corte.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.parcial2corte.data.dao.ClienteDao
import com.example.parcial2corte.data.dao.ProductoDao
import com.example.parcial2corte.data.dao.VentaDao
import com.example.parcial2corte.data.model.Cliente
import com.example.parcial2corte.data.model.Producto
import com.example.parcial2corte.data.model.Venta

@Database(entities = [Producto::class, Cliente::class, Venta::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun clienteDao(): ClienteDao
    abstract fun ventaDao(): VentaDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Log.d("AppDatabase", "Creating new database instance")
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "parcial2corte_database"
                ).build().also { Instance = it }
            }
        }
    }
}