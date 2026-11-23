/**
 * VERSIÓN COMENTADA DE REFERENCIA. La lógica coincide con la versión limpia compilable.
 * Las explicaciones documentan responsabilidad, ciclo de vida, fórmulas y acceso a Room.
 */
package com.proyecto.gyrocampro.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.proyecto.gyrocampro.R
import java.util.Locale
import kotlin.math.abs

class SensorHudOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { strokeCap = Paint.Cap.ROUND }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER }
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var tiltDegrees = 0f
    private var forceG = 1f
    private var accelerationX = 0f
    private var accelerationY = 0f
    private val emerald = ContextCompat.getColor(context, R.color.color_emerald)
    private val amber = ContextCompat.getColor(context, R.color.color_amber)
    private val alert = ContextCompat.getColor(context, R.color.color_alert)
    private val white = ContextCompat.getColor(context, R.color.color_white)
    private val hudBackground = ContextCompat.getColor(context, R.color.color_hud_background)

    /**
     * Recibe una instantánea ya calculada por CameraHudFragment. La vista no registra
     * sensores: solo representa valores, evitando que el ciclo de vida del Canvas
     * duplique listeners o mantenga referencias a una pantalla destruida.
     */
    fun setTelemetry(tilt: Float, gForce: Float, accelX: Float, accelY: Float) {
        tiltDegrees = tilt
        forceG = gForce
        accelerationX = accelX
        accelerationY = accelY
        invalidate()
    }

    /**
     * Dibuja el HUD en coordenadas de la vista. El horizonte rota por el ángulo filtrado;
     * ±2 grados se considera nivelado. El retículo recibe aceleración lineal (gravedad
     * sustraída) para que no quede desplazado permanentemente por la gravedad terrestre.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val density = resources.displayMetrics.density
        val centerX = width / 2f
        val centerY = height / 2f
        val halfLine = width * 0.34f
        // La condición pedida por la rúbrica es estricta: dentro de ±2° el horizonte es esmeralda.
        val isLevel = abs(tiltDegrees) <= 2f
        val horizonColor = when {
            isLevel -> emerald
            abs(tiltDegrees) <= 8f -> amber
            else -> alert
        }

        canvas.save()
        canvas.rotate(-tiltDegrees, centerX, centerY)
        linePaint.color = horizonColor
        linePaint.strokeWidth = 2.5f * density
        canvas.drawLine(centerX - halfLine, centerY, centerX + halfLine, centerY, linePaint)
        linePaint.strokeWidth = 1.5f * density
        canvas.drawLine(centerX - halfLine, centerY - 10f * density, centerX - halfLine, centerY + 10f * density, linePaint)
        canvas.drawLine(centerX + halfLine, centerY - 10f * density, centerX + halfLine, centerY + 10f * density, linePaint)
        canvas.restore()

        // Se escala la aceleración lineal en unidades de g y se limita el desplazamiento para conservar el retículo visible.
        val reticleOffsetX = (accelerationX / 9.80665f * 28f * density).coerceIn(-42f * density, 42f * density)
        val reticleOffsetY = (-accelerationY / 9.80665f * 28f * density).coerceIn(-42f * density, 42f * density)
        val reticleX = centerX + reticleOffsetX
        val reticleY = centerY + reticleOffsetY
        linePaint.color = white
        linePaint.strokeWidth = 1.4f * density
        val radius = 12f * density
        canvas.drawCircle(reticleX, reticleY, radius, linePaint)
        canvas.drawLine(reticleX - 20f * density, reticleY, reticleX - 5f * density, reticleY, linePaint)
        canvas.drawLine(reticleX + 5f * density, reticleY, reticleX + 20f * density, reticleY, linePaint)
        canvas.drawLine(reticleX, reticleY - 20f * density, reticleX, reticleY - 5f * density, linePaint)
        canvas.drawLine(reticleX, reticleY + 5f * density, reticleX, reticleY + 20f * density, linePaint)

        val status = if (isLevel) {
            resources.getString(R.string.hud_status_level)
        } else {
            String.format(Locale.getDefault(), resources.getString(R.string.hud_status_tilt), tiltDegrees)
        }
        textPaint.color = horizonColor
        textPaint.textSize = 14f * density
        textPaint.isFakeBoldText = true
        canvas.drawText(status, centerX, centerY + 42f * density, textPaint)

        val panelWidth = width * 0.72f
        val panelLeft = centerX - panelWidth / 2f
        val barTop = height - 76f * density
        val barHeight = 9f * density
        fillPaint.color = hudBackground
        canvas.drawRoundRect(
            RectF(panelLeft - 12f * density, barTop - 31f * density, panelLeft + panelWidth + 12f * density, barTop + 22f * density),
            10f * density,
            10f * density,
            fillPaint
        )
        textPaint.color = white
        textPaint.isFakeBoldText = false
        textPaint.textSize = 11f * density
        val forceLabel = String.format(Locale.getDefault(), resources.getString(R.string.hud_force_format), forceG)
        canvas.drawText(forceLabel, centerX, barTop - 10f * density, textPaint)
        // En reposo el módulo de aceleración ronda 1 g; la desviación respecto a 1 g se normaliza para la barra.
        val vibration = (abs(forceG - 1f) / 0.35f).coerceIn(0f, 1f)
        fillPaint.color = ContextCompat.getColor(context, R.color.color_white)
        canvas.drawRoundRect(RectF(panelLeft, barTop, panelLeft + panelWidth, barTop + barHeight), barHeight, barHeight, fillPaint)
        val vibrationColor = when {
            vibration < 0.2f -> emerald
            vibration < 0.55f -> amber
            else -> alert
        }
        fillPaint.color = vibrationColor
        canvas.drawRoundRect(
            RectF(panelLeft, barTop, panelLeft + panelWidth * vibration, barTop + barHeight),
            barHeight,
            barHeight,
            fillPaint
        )
        textPaint.color = white
        canvas.drawText(resources.getString(R.string.hud_vibration_label), centerX, barTop + 18f * density, textPaint)
    }
}
