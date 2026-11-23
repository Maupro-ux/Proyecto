package com.proyecto.gyrocampro.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoCaptureDao {
    @Query("SELECT * FROM photo_captures ORDER BY captured_at DESC")
    fun observeAll(): Flow<List<PhotoCaptureEntity>>

    @Query("SELECT * FROM photo_captures WHERE id = :captureId LIMIT 1")
    suspend fun findById(captureId: Long): PhotoCaptureEntity?

    @Insert
    suspend fun insert(capture: PhotoCaptureEntity): Long

    @Update
    suspend fun update(capture: PhotoCaptureEntity)

    @Delete
    suspend fun delete(capture: PhotoCaptureEntity)

    @Query("DELETE FROM photo_captures WHERE id = :captureId")
    suspend fun deleteById(captureId: Long)
}
