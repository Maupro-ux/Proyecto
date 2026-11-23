package com.proyecto.gyrocampro.ui.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.proyecto.gyrocampro.MainActivity
import com.proyecto.gyrocampro.R
import com.proyecto.gyrocampro.databinding.ActivityLoginBinding
import com.proyecto.gyrocampro.utils.AuthSession

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var biometricPrompt: BiometricPrompt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureBiometricPrompt()
        binding.loginAuthButton.setOnClickListener { authenticate() }
        binding.loginSettingsButton.setOnClickListener { openBiometricSettings() }
        showBiometricAvailability()
    }

    private fun configureBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    binding.loginStatus.text = getString(R.string.biometric_success)
                    AuthSession.markAuthenticated()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    binding.loginStatus.text = getString(R.string.biometric_not_recognized)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    val message = when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                        BiometricPrompt.ERROR_CANCELED -> getString(R.string.biometric_user_canceled)
                        BiometricPrompt.ERROR_LOCKOUT -> getString(R.string.biometric_lockout)
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> getString(R.string.biometric_lockout_permanent)
                        BiometricPrompt.ERROR_HW_UNAVAILABLE -> getString(R.string.biometric_hardware_unavailable)
                        BiometricPrompt.ERROR_HW_NOT_PRESENT -> getString(R.string.biometric_no_hardware)
                        BiometricPrompt.ERROR_NO_BIOMETRICS,
                        BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL -> getString(R.string.biometric_none_enrolled)
                        else -> getString(R.string.biometric_generic_error, errorCode)
                    }
                    binding.loginStatus.text = message
                    binding.loginSettingsButton.isVisible = errorCode == BiometricPrompt.ERROR_NO_BIOMETRICS || errorCode == BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL
                }
            }
        )
    }

    private fun showBiometricAvailability() {
        binding.loginSettingsButton.isVisible = false
        when (BiometricManager.from(this).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> binding.loginStatus.text = getString(R.string.login_initial_status)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> binding.loginStatus.text = getString(R.string.biometric_no_hardware)
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> binding.loginStatus.text = getString(R.string.biometric_hardware_unavailable)
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                binding.loginStatus.text = getString(R.string.biometric_none_enrolled)
                binding.loginSettingsButton.isVisible = true
            }
            else -> binding.loginStatus.text = getString(R.string.biometric_no_hardware)
        }
    }

    private fun authenticate() {
        when (BiometricManager.from(this).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.biometric_prompt_title))
                    .setSubtitle(getString(R.string.biometric_prompt_subtitle))
                    .setNegativeButtonText(getString(R.string.biometric_negative_button))
                    .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                    .build()
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> binding.loginStatus.text = getString(R.string.biometric_no_hardware)
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> binding.loginStatus.text = getString(R.string.biometric_hardware_unavailable)
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                binding.loginStatus.text = getString(R.string.biometric_none_enrolled)
                binding.loginSettingsButton.isVisible = true
            }
            else -> binding.loginStatus.text = getString(R.string.biometric_no_hardware)
        }
    }

    private fun openBiometricSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(Settings.ACTION_BIOMETRIC_ENROLL).putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BiometricManager.Authenticators.BIOMETRIC_STRONG
            )
        } else {
            Intent(Settings.ACTION_SECURITY_SETTINGS)
        }
        runCatching {
            startActivity(intent)
        }.onFailure {
            binding.loginStatus.text = getString(R.string.biometric_settings_unavailable)
        }
    }
}
