package com.phonecluster.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import kotlinx.coroutines.launch

import com.phonecluster.app.network.ApiClient
import com.phonecluster.app.network.DeviceRegistrationRequest
import com.phonecluster.app.storage.PreferencesManager
import com.phonecluster.app.utils.DeviceInfoProvider

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RegistrationScreen()
        }
    }

    @Composable
    fun RegistrationScreen() {
        val context = this
        val scope = rememberCoroutineScope()

        var statusText by remember { mutableStateOf("not registered") }
        var isLoading by remember { mutableStateOf(false) }

        // check registration on first composition
        LaunchedEffect(Unit) {
            if (PreferencesManager.isRegistered(context)) {
                val deviceId = PreferencesManager.getDeviceId(context)
                statusText = "registered (device_id = $deviceId)"
            }
        }

        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(text = statusText)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            statusText = "registering..."

                            try {
                                val request = DeviceRegistrationRequest(
                                    user_id = 1,
                                    device_name = DeviceInfoProvider.getDeviceName(),
                                    fingerprint = DeviceInfoProvider.getDeviceFingerprint(context),
                                    storage_capacity = DeviceInfoProvider.getTotalStorageBytes(),
                                    available_storage = DeviceInfoProvider.getAvailableStorageBytes()
                                )

                                val response =
                                    ApiClient.apiService.registerDevice(request)

                                PreferencesManager.saveDeviceId(
                                    context,
                                    response.device_id
                                )
                                PreferencesManager.setRegistered(context, true)

                                statusText =
                                    "registered (device_id = ${response.device_id})"

                            } catch (e: Exception) {
                                statusText = "registration failed"
                                e.printStackTrace()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading
                ) {
                    Text(
                        if (isLoading) "registering..."
                        else "register device"
                    )
                }
            }
        }
    }
}