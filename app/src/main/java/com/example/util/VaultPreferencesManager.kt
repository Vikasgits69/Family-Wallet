package com.example.util

import android.content.Context
import android.content.SharedPreferences
import com.example.data.AppThemeMode
import com.example.data.DisplayMode
import com.example.data.NavigationTab

class VaultPreferencesManager(private val context: Context) {

    private fun getPrefs(): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveThemeMode(mode: AppThemeMode) {
        getPrefs().edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    fun getThemeMode(): AppThemeMode {
        val name = getPrefs().getString(KEY_THEME_MODE, AppThemeMode.LIGHT.name)
        return try {
            AppThemeMode.valueOf(name ?: AppThemeMode.LIGHT.name)
        } catch (e: Exception) {
            AppThemeMode.LIGHT
        }
    }

    fun saveCustomAccentColor(hex: String?) {
        if (hex.isNullOrBlank()) {
            getPrefs().edit().remove(KEY_CUSTOM_ACCENT_HEX).apply()
        } else {
            getPrefs().edit().putString(KEY_CUSTOM_ACCENT_HEX, hex).apply()
        }
    }

    fun getCustomAccentColor(): String? {
        return getPrefs().getString(KEY_CUSTOM_ACCENT_HEX, null)
    }

    fun saveDarkTheme(isDark: Boolean) {
        getPrefs().edit().putBoolean(KEY_DARK_THEME, isDark).apply()
    }

    fun isDarkTheme(): Boolean {
        return getPrefs().getBoolean(KEY_DARK_THEME, false)
    }

    fun saveDisplayMode(mode: DisplayMode) {
        getPrefs().edit().putString(KEY_DISPLAY_MODE, mode.name).apply()
    }

    fun getDisplayMode(): DisplayMode {
        val name = getPrefs().getString(KEY_DISPLAY_MODE, DisplayMode.LIST.name)
        return try {
            DisplayMode.valueOf(name ?: DisplayMode.LIST.name)
        } catch (e: Exception) {
            DisplayMode.LIST
        }
    }

    fun saveSectionDisplayMode(tab: NavigationTab, mode: DisplayMode) {
        getPrefs().edit().putString("section_display_mode_${tab.name}", mode.name).commit()
    }

    fun getSectionDisplayMode(tab: NavigationTab, defaultMode: DisplayMode = DisplayMode.LIST): DisplayMode {
        val name = getPrefs().getString("section_display_mode_${tab.name}", null) ?: return defaultMode
        return try {
            DisplayMode.valueOf(name)
        } catch (e: Exception) {
            defaultMode
        }
    }

    fun getAllSectionDisplayModes(): Map<NavigationTab, DisplayMode> {
        return NavigationTab.values().associateWith { tab ->
            getSectionDisplayMode(tab, DisplayMode.LIST)
        }
    }

    fun saveBiometricEnabled(enabled: Boolean) {
        getPrefs().edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    fun isBiometricEnabled(): Boolean {
        return getPrefs().getBoolean(KEY_BIOMETRIC_ENABLED, true)
    }

    fun saveGlobalMask(isMasked: Boolean) {
        getPrefs().edit().putBoolean(KEY_GLOBAL_MASK, isMasked).apply()
    }

    fun isGlobalMask(): Boolean {
        return getPrefs().getBoolean(KEY_GLOBAL_MASK, true)
    }

    fun saveMasterPin(pin: String) {
        getPrefs().edit().putString(KEY_MASTER_PIN, pin).apply()
    }

    fun getMasterPin(): String {
        return getPrefs().getString(KEY_MASTER_PIN, "8421") ?: "8421"
    }

    fun saveAutoBackupFrequency(frequency: String) {
        getPrefs().edit().putString(KEY_AUTO_BACKUP_FREQ, frequency).apply()
    }

    fun getAutoBackupFrequency(): String {
        return getPrefs().getString(KEY_AUTO_BACKUP_FREQ, "Daily") ?: "Daily"
    }

    fun saveAutoBackupOnOpen(enabled: Boolean) {
        getPrefs().edit().putBoolean(KEY_AUTO_BACKUP_OPEN, enabled).apply()
    }

    fun isAutoBackupOnOpen(): Boolean {
        return getPrefs().getBoolean(KEY_AUTO_BACKUP_OPEN, true)
    }

    fun saveAutoBackupOnClose(enabled: Boolean) {
        getPrefs().edit().putBoolean(KEY_AUTO_BACKUP_CLOSE, enabled).apply()
    }

    fun isAutoBackupOnClose(): Boolean {
        return getPrefs().getBoolean(KEY_AUTO_BACKUP_CLOSE, true)
    }

    fun saveIncludePhotosInBackup(include: Boolean) {
        getPrefs().edit().putBoolean(KEY_INCLUDE_PHOTOS_IN_BACKUP, include).commit()
    }

    fun isIncludePhotosInBackup(): Boolean {
        return getPrefs().getBoolean(KEY_INCLUDE_PHOTOS_IN_BACKUP, true)
    }

    fun saveClipboardAutoClearEnabled(enabled: Boolean) {
        getPrefs().edit().putBoolean(KEY_CLIPBOARD_AUTO_CLEAR, enabled).apply()
    }

    fun isClipboardAutoClearEnabled(): Boolean {
        return getPrefs().getBoolean(KEY_CLIPBOARD_AUTO_CLEAR, true)
    }

    fun saveClipboardClearTimeout(seconds: Int) {
        getPrefs().edit().putInt(KEY_CLIPBOARD_CLEAR_TIMEOUT, seconds).apply()
    }

    fun getClipboardClearTimeout(): Int {
        return getPrefs().getInt(KEY_CLIPBOARD_CLEAR_TIMEOUT, 30)
    }

    fun saveVisualDensityMode(mode: com.example.data.VisualDensityMode) {
        getPrefs().edit().putString(KEY_VISUAL_DENSITY, mode.name).apply()
    }

    fun getVisualDensityMode(): com.example.data.VisualDensityMode {
        val name = getPrefs().getString(KEY_VISUAL_DENSITY, com.example.data.VisualDensityMode.SPACIOUS.name)
        return try {
            com.example.data.VisualDensityMode.valueOf(name ?: com.example.data.VisualDensityMode.SPACIOUS.name)
        } catch (e: Exception) {
            com.example.data.VisualDensityMode.SPACIOUS
        }
    }

    fun saveCardSurfaceShader(shader: com.example.data.CardSurfaceShader) {
        getPrefs().edit().putString(KEY_CARD_SURFACE_SHADER, shader.name).apply()
    }

    fun getCardSurfaceShader(): com.example.data.CardSurfaceShader {
        val name = getPrefs().getString(KEY_CARD_SURFACE_SHADER, com.example.data.CardSurfaceShader.CLASSIC_GRADIENT.name)
        return try {
            com.example.data.CardSurfaceShader.valueOf(name ?: com.example.data.CardSurfaceShader.CLASSIC_GRADIENT.name)
        } catch (e: Exception) {
            com.example.data.CardSurfaceShader.CLASSIC_GRADIENT
        }
    }

    fun saveWifiServerPort(port: Int) {
        getPrefs().edit().putInt(KEY_WIFI_SERVER_PORT, port).apply()
    }

    fun getWifiServerPort(): Int {
        return getPrefs().getInt(KEY_WIFI_SERVER_PORT, 8080)
    }

    fun saveWifiServerRequirePin(require: Boolean) {
        getPrefs().edit().putBoolean(KEY_WIFI_SERVER_REQUIRE_PIN, require).apply()
    }

    fun isWifiServerRequirePin(): Boolean {
        return getPrefs().getBoolean(KEY_WIFI_SERVER_REQUIRE_PIN, true)
    }

    fun saveWifiServerPin(pin: String) {
        getPrefs().edit().putString(KEY_WIFI_SERVER_PIN, pin).apply()
    }

    fun getWifiServerPin(): String {
        return getPrefs().getString(KEY_WIFI_SERVER_PIN, "8492") ?: "8492"
    }

    companion object {
        private const val PREF_NAME = "family_wallet_vault_prefs"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_CUSTOM_ACCENT_HEX = "custom_accent_hex"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_DISPLAY_MODE = "display_mode"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_GLOBAL_MASK = "global_mask"
        private const val KEY_MASTER_PIN = "master_pin"
        private const val KEY_AUTO_BACKUP_FREQ = "auto_backup_freq"
        private const val KEY_AUTO_BACKUP_OPEN = "auto_backup_open"
        private const val KEY_AUTO_BACKUP_CLOSE = "auto_backup_close"
        private const val KEY_INCLUDE_PHOTOS_IN_BACKUP = "include_photos_in_backup"
        private const val KEY_CLIPBOARD_AUTO_CLEAR = "clipboard_auto_clear"
        private const val KEY_CLIPBOARD_CLEAR_TIMEOUT = "clipboard_clear_timeout"
        private const val KEY_VISUAL_DENSITY = "visual_density"
        private const val KEY_CARD_SURFACE_SHADER = "card_surface_shader"
        private const val KEY_WIFI_SERVER_PORT = "wifi_server_port"
        private const val KEY_WIFI_SERVER_REQUIRE_PIN = "wifi_server_require_pin"
        private const val KEY_WIFI_SERVER_PIN = "wifi_server_pin"
    }
}
