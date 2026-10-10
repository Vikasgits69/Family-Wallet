package com.example.server

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.Collections
import java.util.Locale

object NetworkUtils {

    /**
     * Finds the device's local IPv4 address on the connected Wi-Fi or local network.
     */
    fun getLocalIpAddress(context: Context): String? {
        // Method 1: Check WifiManager connection info
        try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            val ipInt = wifiManager?.connectionInfo?.ipAddress ?: 0
            if (ipInt != 0) {
                return String.format(
                    Locale.US,
                    "%d.%d.%d.%d",
                    ipInt and 0xff,
                    ipInt shr 8 and 0xff,
                    ipInt shr 16 and 0xff,
                    ipInt shr 24 and 0xff
                )
            }
        } catch (_: Exception) {}

        // Method 2: Inspect active NetworkInterfaces (Wi-Fi wlan0, hotspot, eth0)
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            // Prioritize wlan and ap interfaces
            val sortedInterfaces = interfaces.sortedByDescending {
                when {
                    it.name.startsWith("wlan", ignoreCase = true) -> 3
                    it.name.startsWith("ap", ignoreCase = true) -> 2
                    it.name.startsWith("eth", ignoreCase = true) -> 1
                    else -> 0
                }
            }

            for (intf in sortedInterfaces) {
                if (intf.isLoopback || !intf.isUp) continue
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        val hostAddress = addr.hostAddress
                        if (hostAddress != null && !hostAddress.startsWith("127.")) {
                            return hostAddress
                        }
                    }
                }
            }
        } catch (_: Exception) {}

        return null
    }

    /**
     * Checks if the device is currently connected to Wi-Fi.
     */
    fun isConnectedToWifi(context: Context): Boolean {
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
            val activeNetwork = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(activeNetwork) ?: return false
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } catch (_: Exception) {
            return false
        }
    }

    /**
     * Gets the connected Wi-Fi SSID if available.
     */
    fun getWifiName(context: Context): String {
        try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            val info = wifiManager?.connectionInfo
            val ssid = info?.ssid?.replace("\"", "") ?: ""
            if (ssid.isNotBlank() && ssid != "<unknown ssid>") {
                return ssid
            }
        } catch (_: Exception) {}
        return if (isConnectedToWifi(context)) "Wi-Fi Network" else "Local Hotspot / Network"
    }
}
