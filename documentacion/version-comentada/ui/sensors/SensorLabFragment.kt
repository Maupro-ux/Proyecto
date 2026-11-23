/**
 * VERSIÓN COMENTADA DE REFERENCIA. La lógica coincide con la versión limpia compilable.
 * Las explicaciones documentan responsabilidad, ciclo de vida, fórmulas y acceso a Room.
 */
package com.proyecto.gyrocampro.ui.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.proyecto.gyrocampro.MainActivity
import com.proyecto.gyrocampro.R
import com.proyecto.gyrocampro.databinding.FragmentSensorLabBinding
import java.util.Locale
import kotlin.math.abs

/**
 * Diagnóstico de ejes y ajuste visual. Los listeners viven solo entre onResume/onPause;
 * la pantalla muestra los tres componentes del giroscopio (rad/s) y acelerómetro (m/s²).
 */
class SensorLabFragment : Fragment(), SensorEventListener {
    // Esta referencia se invalida en onDestroyView, no en onDestroy, porque Fragment puede recrear su vista.
    private var _binding: FragmentSensorLabBinding? = null
    private val binding get() = _binding!!
    private var sensorManager: SensorManager? = null
    private var gyroscope: Sensor? = null
    private var accelerometer: Sensor? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSensorLabBinding.inflate(inflater, container, false)
        return binding.root
    }

    /** Detecta hardware, restaura preferencia del tema y conecta el botón de cierre de sesión. */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        binding.sensorStatus.text = if (gyroscope == null || accelerometer == null) {
            getString(R.string.sensor_unavailable)
        } else {
            getString(R.string.sensor_diagnostics_ready)
        }

        val preferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        binding.darkModeSwitch.isChecked = preferences.getBoolean(KEY_DARK_MODE, false)
        binding.darkModeSwitch.setOnCheckedChangeListener { _, enabled ->
            preferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
        binding.sensorLogoutButton.setOnClickListener {
            (activity as? MainActivity)?.performLogout()
        }
    }

    /** Activa la recepción de eventos cuando el panel está en primer plano. */
    override fun onResume() {
        super.onResume()
        gyroscope?.let { sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        accelerometer?.let { sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    /** Desregistra ambos sensores antes de abandonar el panel. */
    override fun onPause() {
        sensorManager?.unregisterListener(this)
        super.onPause()
    }

    /** Actualiza únicamente el grupo de barras perteneciente al tipo de sensor que reportó el evento. */
    override fun onSensorChanged(event: SensorEvent) {
        val currentBinding = _binding ?: return
        val values = event.values
        when (event.sensor.type) {
            Sensor.TYPE_GYROSCOPE -> {
                renderAxis(values[0], currentBinding.gyroXProgress, currentBinding.gyroXValue, 5f)
                renderAxis(values[1], currentBinding.gyroYProgress, currentBinding.gyroYValue, 5f)
                renderAxis(values[2], currentBinding.gyroZProgress, currentBinding.gyroZValue, 5f)
            }
            Sensor.TYPE_ACCELEROMETER -> {
                renderAxis(values[0], currentBinding.accelXProgress, currentBinding.accelXValue, 20f)
                renderAxis(values[1], currentBinding.accelYProgress, currentBinding.accelYValue, 20f)
                renderAxis(values[2], currentBinding.accelZProgress, currentBinding.accelZValue, 20f)
            }
        }
    }

    /** Normaliza la magnitud absoluta a 0–100; el TextView conserva el signo y el valor numérico real. */
    private fun renderAxis(value: Float, progressBar: ProgressBar, valueView: TextView, scaleMaximum: Float) {
        progressBar.progress = ((abs(value) / scaleMaximum) * 100f).toInt().coerceIn(0, 100)
        valueView.text = String.format(Locale.getDefault(), getString(R.string.axis_value_format), value)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    /** Rompe la referencia al binding cuando el árbol de vistas deja de existir. */
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val PREFERENCES_NAME = "gyrocam_preferences"
        const val KEY_DARK_MODE = "dark_mode_enabled"
    }
}
