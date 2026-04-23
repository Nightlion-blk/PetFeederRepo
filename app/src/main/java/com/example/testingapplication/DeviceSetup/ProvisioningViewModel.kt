package com.example.testingapplication.DeviceSetup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class ProvisioningState {
    object Idle : ProvisioningState()
    object ConnectingToDevice : ProvisioningState()
    object SendingCredentials : ProvisioningState()
    object WaitingForDeviceOnline : ProvisioningState()
    object Success : ProvisioningState()
    data class Error(val message: String) : ProvisioningState()
}

class ProvisioningViewModel(
    private val manager: WifiProvisioningManager
) : ViewModel() {

    private val _state = MutableStateFlow<ProvisioningState>(ProvisioningState.Idle)
    val state: StateFlow<ProvisioningState> = _state

    fun startProvisioning(homeSSID: String, homePassword: String) {
        viewModelScope.launch {
            // 1. Connect to Arduino AP
            _state.value = ProvisioningState.ConnectingToDevice
            val connected = manager.connectToArduinoAP()
            if (!connected) {
                _state.value = ProvisioningState.Error("Could not find device. Is it powered on?")
                return@launch
            }

            // 2. Send home WiFi credentials
            _state.value = ProvisioningState.SendingCredentials
            val sent = manager.sendCredentials(homeSSID, homePassword)
            if (!sent) {
                _state.value = ProvisioningState.Error("Failed to send credentials to device.")
                return@launch
            }

            // 3. Wait for Arduino to appear on Firebase
            _state.value = ProvisioningState.WaitingForDeviceOnline
            waitForDeviceOnFirebase()
        }
    }

    private suspend fun waitForDeviceOnFirebase() {
        val db = Firebase.database.reference
        val deviceRef = db.child("devices/device_001/status")

        repeat(20) { // Poll for up to ~20 seconds
            delay(1000)
            try {
                val snapshot = deviceRef.get().await()
                if (snapshot.getValue(String::class.java) == "online") {
                    _state.value = ProvisioningState.Success
                    return
                }
            }catch (e: Exception) {
                _state.value = ProvisioningState.Error("Device did not come online. Check WiFi credentials.")
            }

        }

        if (_state.value !is ProvisioningState.Success) {
            _state.value = ProvisioningState.Error("Device did not come online. Check WiFi credentials.")
        }
    }

    fun reset() {
        _state.value = ProvisioningState.Idle
    }
}