package com.example.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

enum class BiometricAvailability(val message: String, val canPrompt: Boolean) {
    AVAILABLE("Biometrics enrolled and ready", true),
    NO_HARDWARE("No biometric sensor found on this device", false),
    HW_UNAVAILABLE("Biometric sensor is currently unavailable", false),
    NOT_ENROLLED("No biometric credentials registered on device", false),
    SECURITY_UPDATE_REQUIRED("Security update required for biometric hardware", false),
    UNKNOWN("Biometric status unknown", false)
}

object BiometricAuthManager {

    fun checkAvailability(context: Context): BiometricAvailability {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricAvailability.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAvailability.NO_HARDWARE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAvailability.HW_UNAVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.NOT_ENROLLED
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricAvailability.SECURITY_UPDATE_REQUIRED
            else -> BiometricAvailability.UNKNOWN
        }
    }

    fun promptBiometricAuthentication(
        activity: FragmentActivity,
        title: String = "Family Vault Security",
        subtitle: String = "Confirm your identity with Fingerprint or Face Unlock",
        negativeText: String = "Enter Vault PIN",
        onSuccess: () -> Unit,
        onError: (errorCode: Int, errString: CharSequence) -> Unit,
        onFailed: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errorCode, errString)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onFailed()
            }
        }

        val biometricPrompt = BiometricPrompt(activity, executor, callback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription("Encrypted authorization for accessing family cards and accounts.")
            .setNegativeButtonText(negativeText)
            .setConfirmationRequired(true)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    fun authenticateForSecret(
        activity: FragmentActivity,
        secretName: String = "PIN / Password",
        onSuccess: () -> Unit
    ) {
        val availability = checkAvailability(activity)
        if (availability.canPrompt) {
            promptBiometricAuthentication(
                activity = activity,
                title = "Fingerprint Authentication",
                subtitle = "Authenticate to view $secretName",
                negativeText = "Cancel",
                onSuccess = onSuccess,
                onError = { _, _ -> },
                onFailed = {}
            )
        } else {
            // If biometric sensor is not enrolled/present, grant access
            onSuccess()
        }
    }
}
