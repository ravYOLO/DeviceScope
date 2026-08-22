package com.devicescope.app.data

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import java.net.Inet4Address

object NetworkProvider {

    private const val UnknownSsid = "<unknown ssid>"

    private val defaults = NetworkInfo(
        operatorName = "",
        networkType = "UNKNOWN",
        signalStrengthDbm = 0,
        ipAddress = "",
        simCount = 0,
        roaming = false
    )

    fun collect(context: Context): NetworkInfo = runCatching {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val wifiFrequencyMhz = wifiFrequencyMhzOf(context)
        NetworkInfo(
            operatorName = operatorNameOf(telephonyManager),
            networkType = networkTypeOf(context, telephonyManager),
            signalStrengthDbm = signalStrengthDbmOf(telephonyManager),
            ipAddress = ipAddressOf(context),
            simCount = simCountOf(context),
            roaming = roamingOf(telephonyManager),
            wifiSsid = wifiSsidOf(context),
            wifiFrequencyMhz = wifiFrequencyMhz,
            wifiChannel = wifiChannelOf(wifiFrequencyMhz)
        )
    }.getOrDefault(defaults)

    @SuppressLint("MissingPermission")
    private fun simCountOf(context: Context): Int = runCatching {
        val subMgr = context.getSystemService(SubscriptionManager::class.java)
        subMgr.activeSubscriptionInfoList?.size ?: 0
    }.getOrDefault(0)

    private fun operatorNameOf(telephonyManager: TelephonyManager): String = runCatching {
        telephonyManager.networkOperatorName ?: ""
    }.getOrDefault("")

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    private fun networkTypeOf(context: Context, telephonyManager: TelephonyManager): String {
        if (isWifiConnected(context)) return "WIFI"
        return runCatching { networkTypeNameOf(telephonyManager.dataNetworkType) }.getOrDefault("UNKNOWN")
    }

    private fun networkTypeNameOf(type: Int): String = when (type) {
        TelephonyManager.NETWORK_TYPE_LTE -> "LTE"
        TelephonyManager.NETWORK_TYPE_NR -> "5G NR"
        TelephonyManager.NETWORK_TYPE_UMTS -> "UMTS"
        TelephonyManager.NETWORK_TYPE_GSM -> "GSM"
        TelephonyManager.NETWORK_TYPE_CDMA -> "CDMA"
        TelephonyManager.NETWORK_TYPE_EVDO_0 -> "EVDO"
        TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE"
        TelephonyManager.NETWORK_TYPE_HSPA -> "HSPA"
        else -> "UNKNOWN"
    }

    private fun isWifiConnected(context: Context): Boolean = runCatching {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return@runCatching false
        connectivityManager.getNetworkCapabilities(network)?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
    }.getOrDefault(false)

    private fun signalStrengthDbmOf(telephonyManager: TelephonyManager): Int = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            telephonyManager.signalStrength?.cellSignalStrengths?.firstOrNull()?.dbm ?: 0
        } else {
            0
        }
    }.getOrDefault(0)

    private fun ipAddressOf(context: Context): String = runCatching {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val linkAddresses = connectivityManager.activeNetwork?.let { connectivityManager.getLinkProperties(it)?.linkAddresses }
        linkAddresses?.firstOrNull { it.address is Inet4Address }?.address?.hostAddress ?: ""
    }.getOrDefault("")

    private fun roamingOf(telephonyManager: TelephonyManager): Boolean = runCatching {
        telephonyManager.isNetworkRoaming
    }.getOrDefault(false)

    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    private fun wifiSsidOf(context: Context): String = runCatching {
        val rawSsid = (context.getSystemService(Context.WIFI_SERVICE) as WifiManager).connectionInfo?.ssid.orEmpty()
        if (rawSsid.isBlank() || rawSsid == UnknownSsid) "" else rawSsid.removeSurrounding("\"")
    }.getOrDefault("")

    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    private fun wifiFrequencyMhzOf(context: Context): Int = runCatching {
        (context.getSystemService(Context.WIFI_SERVICE) as WifiManager).connectionInfo?.frequency ?: 0
    }.getOrDefault(0)

    private fun wifiChannelOf(frequencyMhz: Int): Int = when {
        frequencyMhz in 2412..2484 -> (frequencyMhz - 2412) / 5 + 1
        frequencyMhz in 5170..5885 -> (frequencyMhz - 5170) / 5 + 34
        else -> 0
    }
}