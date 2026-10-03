package com.example.util

import android.content.Context
import android.content.SharedPreferences
import com.example.data.AppThemeMode

object VaultPreferencesManager {
    private const val PREF_NAME = "family_wallet_vault_prefs"
    private const val KEY_THEME_MODE = "theme_mode"
    private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    private const val KEY_GLOBAL_MASK = "global_mask"
    private const val KEY_MASTER_PIN = "master_pin"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveThemeMode(context: Context, mode: AppThemeMode) {
        getPrefs(context).edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    fun loadThemeMode(context: Context): AppThemeMode {
        val name = getPrefs(context).getString(KEY_THEME_MODE, AppThemeMode.DARK.name)
        return try {
            AppThemeMode.valueOf(name ?: AppThemeMode.DARK.name)
        } catch (e: Exception) {
            AppThemeMode.DARK
        }
    }

    fun saveBiometricEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    fun loadBiometricEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_BIOMETRIC_ENABLED, true)
    }

    fun saveGlobalMask(context: Context, isMasked: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_GLOBAL_MASK, isMasked).apply()
    }

    fun loadGlobalMask(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_GLOBAL_MASK, true)
    }

    fun saveMasterPin(context: Context, pin: String) {
        getPrefs(context).edit().putString(KEY_MASTER_PIN, pin).apply()
    }

    fun loadMasterPin(context: Context): String {
        return getPrefs(context).getString(KEY_MASTER_PIN, "8421") ?: "8421"
    }
}
