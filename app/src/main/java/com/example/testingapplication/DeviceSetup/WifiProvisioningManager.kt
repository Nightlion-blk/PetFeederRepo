package com.example.testingapplication.DeviceSetup

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.coroutines.resume

class WifiProvisioningManager(private val context: Context) {

    private val client = OkHttpClient()
    private val ARDUINO_AP_SSID = "MyDevice-Setup"
    private val ARDUINO_AP_PASS = "setup1234"
    private val ARDUINO_IP = "192.168.4.1"

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun connectToArduinoAP(): Boolean =
        suspendCancellableCoroutine { cont ->
            val specifier = WifiNetworkSpecifier.Builder()
                .setSsid(ARDUINO_AP_SSID)
                .setWpa2Passphrase(ARDUINO_AP_PASS)
                .build()

            val request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .setNetworkSpecifier(specifier)
                .build()

            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager

            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    cm.bindProcessToNetwork(network)
                    cont.resume(true)
                }
                override fun onUnavailable() {
                    cont.resume(false)
                }
            }

            cm.requestNetwork(request, callback)
            cont.invokeOnCancellation { cm.unregisterNetworkCallback(callback) }
        }

    suspend fun sendCredentials(ssid: String, password: String): Boolean {
        return try {
            val body = FormBody.Builder()
                .add("ssid", ssid)
                .add("pass", password)
                .build()

            val request = Request.Builder()
                .url("http://$ARDUINO_IP/connect")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}