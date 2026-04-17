package com.example.testingapplication
import okhttp3.OkHttpClient
import okhttp3.Request


class ArduinoConnection(private val ip: String) {

    private val client = OkHttpClient()

    fun sendCommand(endpoint: String): String {
        val request = Request.Builder()
            .url("http://$ip/$endpoint")
            .build()

        client.newCall(request).execute().use { response ->
            return response.body?.string() ?: "No response"
        }
    }

    fun ledOn()  = sendCommand("led/on")
    fun ledOff() = sendCommand("led/off")
    fun status() = sendCommand("status")
}