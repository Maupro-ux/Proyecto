package com.proyecto.gyrocampro.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_captures")
data class PhotoCaptureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "image_path")
    val imagePath: String,
    val title: String,
    val note: String,
    @ColumnInfo(name = "tilt_degrees")
    val tiltDegrees: Float,
    @ColumnInfo(name = "force_g")
    val forceG: Float,
    @ColumnInfo(name = "gyro_x_rad_s")
    val gyroX: Float,
    @ColumnInfo(name = "gyro_y_rad_s")
    val gyroY: Float,
    @ColumnInfo(name = "gyro_z_rad_s")
    val gyroZ: Float,
    @ColumnInfo(name = "accel_x_m_s2")
    val accelX: Float,
    @ColumnInfo(name = "accel_y_m_s2")
    val accelY: Float,
    @ColumnInfo(name = "accel_z_m_s2")
    val accelZ: Float,
    @ColumnInfo(name = "captured_at")
    val capturedAt: Long
)
