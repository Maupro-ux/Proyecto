/**
 * VERSIÓN COMENTADA DE REFERENCIA. La lógica coincide con la versión limpia compilable.
 * Las explicaciones documentan responsabilidad, ciclo de vida, fórmulas y acceso a Room.
 */
package com.proyecto.gyrocampro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PhotoCaptureEntity::class],
    version = 1,
    exportSchema = false
)
/** Base Room singleton: evita crear conexiones duplicadas durante la vida del proceso. */
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoCaptureDao(): PhotoCaptureDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /** Double-checked locking y applicationContext evitan fugas de Activities/Fragments. */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gyrocam_pro.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
