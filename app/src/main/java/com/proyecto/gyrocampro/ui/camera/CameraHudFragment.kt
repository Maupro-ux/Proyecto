package com.proyecto.gyrocampro.ui.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.proyecto.gyrocampro.R
import com.proyecto.gyrocampro.data.local.AppDatabase
import com.proyecto.gyrocampro.data.local.PhotoCaptureEntity
import com.proyecto.gyrocampro.databinding.DialogEditCaptureBinding
import com.proyecto.gyrocampro.databinding.FragmentCameraHudBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlinx.coroutines.launch

class CameraHudFragment : Fragment(), SensorEventListener {
    private var _binding: FragmentCameraHudBinding? = null
    private val binding get() = _binding!!
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null
    private var sensorManager: SensorManager? = null
    private var gyroSensor: Sensor? = null
    private var accelerometerSensor: Sensor? = null
    private var gyroX = 0f
    private var gyroY = 0f
    private var gyroZ = 0f
    private var accelerationX = 0f
    private var accelerationY = 0f
    private var accelerationZ = 0f
    private var forceG = 1f
    private var tiltDegrees = 0f
    private var lastGyroTimestampNs = 0L
    private var gravityX = 0f
    private var gravityY = 0f
    private var gravityZ = 0f
    private var hasGravityEstimate = false
    private var hasInitialOrientation = false
    private var linearAccelerationX = 0f
    private var linearAccelerationY = 0f

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startCamera()
        } else if (_binding != null) {
            binding.cameraStatus.text = getString(R.string.camera_permission_denied)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraHudBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accelerometerSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        binding.captureButton.setOnClickListener { showCaptureForm() }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            binding.cameraStatus.text = getString(R.string.camera_permission_needed)
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        if (gyroSensor == null || accelerometerSensor == null) {
            binding.cameraStatus.text = getString(R.string.camera_sensor_missing)
        }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null && imageCapture == null &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        }
        lastGyroTimestampNs = 0L
        gyroSensor?.let { sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
        accelerometerSensor?.let { sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
    }

    override fun onPause() {
        sensorManager?.unregisterListener(this)
        super.onPause()
    }

    private fun startCamera() {
        if (_binding == null) return
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            if (_binding == null) return@addListener
            try {
                val provider = cameraProviderFuture.get()
                cameraProvider = provider
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = binding.cameraPreview.surfaceProvider
                }
                val captureUseCase = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
                imageCapture = captureUseCase
                provider.unbindAll()
                provider.bindToLifecycle(
                    viewLifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    captureUseCase
                )
                binding.cameraStatus.text = getString(R.string.sensor_diagnostics_ready)
            } catch (exception: Exception) {
                binding.cameraStatus.text = getString(R.string.camera_unavailable)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun showCaptureForm() {
        if (imageCapture == null) {
            binding.cameraStatus.text = getString(R.string.camera_unavailable)
            return
        }
        val form = DialogEditCaptureBinding.inflate(layoutInflater)
        val timeLabel = SimpleDateFormat("HHmmss", Locale.getDefault()).format(Date())
        form.titleInput.setText(getString(R.string.capture_title_format, timeLabel))
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_capture_title)
            .setView(form.root)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save, null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val title = form.titleInput.text?.toString()?.trim().orEmpty()
                val note = form.noteInput.text?.toString()?.trim().orEmpty()
                val valid = validateCaptureForm(form, title, note)
                if (valid) {
                    dialog.dismiss()
                    takePicture(title, note)
                }
            }
        }
        dialog.show()
    }

    private fun validateCaptureForm(form: DialogEditCaptureBinding, title: String, note: String): Boolean {
        form.titleInputLayout.error = null
        form.noteInputLayout.error = null
        if (title.isBlank()) {
            form.titleInputLayout.error = getString(R.string.field_title_required)
            return false
        }
        if (title.length > 40) {
            form.titleInputLayout.error = getString(R.string.field_title_too_long)
            return false
        }
        if (note.length > 280) {
            form.noteInputLayout.error = getString(R.string.field_note_too_long)
            return false
        }
        return true
    }

    private fun takePicture(title: String, note: String) {
        val captureUseCase = imageCapture ?: return
        val outputDirectory = File(requireContext().filesDir, "captures")
        if (!outputDirectory.exists()) outputDirectory.mkdirs()
        val outputFile = File(outputDirectory, "gyro_${System.currentTimeMillis()}.jpg")
        val capturedAt = System.currentTimeMillis()
        val appContext = requireContext().applicationContext
        val snapshot = SensorSnapshot(
            tiltDegrees = tiltDegrees,
            forceG = forceG,
            gyroX = gyroX,
            gyroY = gyroY,
            gyroZ = gyroZ,
            accelX = accelerationX,
            accelY = accelerationY,
            accelZ = accelerationZ
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
        captureUseCase.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    lifecycleScope.launch {
                        val entity = PhotoCaptureEntity(
                            imagePath = outputFile.absolutePath,
                            title = title,
                            note = note,
                            tiltDegrees = snapshot.tiltDegrees,
                            forceG = snapshot.forceG,
                            gyroX = snapshot.gyroX,
                            gyroY = snapshot.gyroY,
                            gyroZ = snapshot.gyroZ,
                            accelX = snapshot.accelX,
                            accelY = snapshot.accelY,
                            accelZ = snapshot.accelZ,
                            capturedAt = capturedAt
                        )
                        runCatching {
                            AppDatabase.getInstance(appContext).photoCaptureDao().insert(entity)
                        }.onSuccess {
                            if (isAdded) Toast.makeText(requireContext(), R.string.capture_saved, Toast.LENGTH_SHORT).show()
                        }.onFailure {
                            outputFile.delete()
                            if (isAdded) Toast.makeText(requireContext(), R.string.capture_db_error, Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    outputFile.delete()
                    if (isAdded) Toast.makeText(requireContext(), R.string.capture_save_error, Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_GYROSCOPE -> {
                gyroX = event.values[0]
                gyroY = event.values[1]
                gyroZ = event.values[2]
                if (lastGyroTimestampNs != 0L) {
                    val deltaNs = (event.timestamp - lastGyroTimestampNs).coerceIn(0L, 100_000_000L)
                    val deltaSeconds = deltaNs / 1_000_000_000f
                    tiltDegrees += Math.toDegrees((gyroZ * deltaSeconds).toDouble()).toFloat()
                    if (tiltDegrees > 180f) tiltDegrees -= 360f
                    if (tiltDegrees < -180f) tiltDegrees += 360f
                }
                lastGyroTimestampNs = event.timestamp
            }
            Sensor.TYPE_ACCELEROMETER -> {
                accelerationX = event.values[0]
                accelerationY = event.values[1]
                accelerationZ = event.values[2]
                if (!hasGravityEstimate) {
                    gravityX = accelerationX
                    gravityY = accelerationY
                    gravityZ = accelerationZ
                    hasGravityEstimate = true
                } else {
                    val gravityAlpha = 0.8f
                    gravityX = gravityAlpha * gravityX + (1f - gravityAlpha) * accelerationX
                    gravityY = gravityAlpha * gravityY + (1f - gravityAlpha) * accelerationY
                    gravityZ = gravityAlpha * gravityZ + (1f - gravityAlpha) * accelerationZ
                }
                linearAccelerationX = accelerationX - gravityX
                linearAccelerationY = accelerationY - gravityY
                val magnitude = sqrt(
                    accelerationX * accelerationX +
                        accelerationY * accelerationY +
                        accelerationZ * accelerationZ
                )
                forceG = magnitude / SensorManager.GRAVITY_EARTH
                val accelerometerTilt = Math.toDegrees(atan2(accelerationX.toDouble(), -accelerationY.toDouble())).toFloat()
                if (!hasInitialOrientation) {
                    tiltDegrees = accelerometerTilt
                    hasInitialOrientation = true
                } else {
                    var correction = accelerometerTilt - tiltDegrees
                    while (correction > 180f) correction -= 360f
                    while (correction < -180f) correction += 360f
                    tiltDegrees += correction * 0.02f
                }
            }
        }
        _binding?.sensorHudOverlay?.setTelemetry(tiltDegrees, forceG, linearAccelerationX, linearAccelerationY)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onDestroyView() {
        cameraProvider?.unbindAll()
        cameraProvider = null
        imageCapture = null
        _binding = null
        super.onDestroyView()
    }

    private data class SensorSnapshot(
        val tiltDegrees: Float,
        val forceG: Float,
        val gyroX: Float,
        val gyroY: Float,
        val gyroZ: Float,
        val accelX: Float,
        val accelY: Float,
        val accelZ: Float
    )
}
