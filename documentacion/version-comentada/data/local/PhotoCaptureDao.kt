/**
 * VERSIÓN COMENTADA DE REFERENCIA. La lógica coincide con la versión limpia compilable.
 * Las explicaciones documentan responsabilidad, ciclo de vida, fórmulas y acceso a Room.
 */
package com.proyecto.gyrocampro.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/** Contrato de acceso a SQLite: Flow para observación reactiva y suspend para operaciones CRUD. */
@Dao
interface PhotoCaptureDao {
    // Room invalida y vuelve a emitir la consulta cuando cambia la tabla; la galería no necesita refresco manual.
    @Query("SELECT * FROM photo_captures ORDER BY captured_at DESC")
    fun observeAll(): Flow<List<PhotoCaptureEntity>>

    @Query("SELECT * FROM photo_captures WHERE id = :captureId LIMIT 1")
    suspend fun findById(captureId: Long): PhotoCaptureEntity?

    // INSERT devuelve el identificador autogenerado, útil para futuras operaciones sobre la captura.
    @Insert
    suspend fun insert(capture: PhotoCaptureEntity): Long

    // UPDATE modifica la fila por su clave primaria; se usa para editar título y nota sin alterar telemetría.
    @Update
    suspend fun update(capture: PhotoCaptureEntity)

    // DELETE elimina la entidad seleccionada tras la confirmación del usuario.
    @Delete
    suspend fun delete(capture: PhotoCaptureEntity)

    @Query("DELETE FROM photo_captures WHERE id = :captureId")
    suspend fun deleteById(captureId: Long)
}
