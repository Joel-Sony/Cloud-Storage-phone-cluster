package com.phonecluster.app.screens

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.phonecluster.app.network.ApiClient
import com.phonecluster.app.network.DeviceRegistrationRequest
import com.phonecluster.app.storage.PreferencesManager
import com.phonecluster.app.utils.DeviceInfoProvider
import kotlinx.coroutines.launch

@Composable
fun RegistrationScreen(onRegistered: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var statusText by remember { mutableStateOf("not registered") }
    var isLoading by remember { mutableStateOf(false) }

    // Check if already registered on first load
    LaunchedEffect(Unit) {
        if (PreferencesManager.isRegistered(context)) {
            val deviceId = PreferencesManager.getDeviceId(context)
            statusText = "registered (device_id = $deviceId)"
            onRegistered() // Auto-navigate if already registered
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = statusText, style = MaterialTheme.typography.bodyLarge)

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

                            val response = ApiClient.apiService.registerDevice(request)

                            PreferencesManager.saveDeviceId(context, response.device_id)
                            PreferencesManager.setRegistered(context, true)

                            statusText = "registered (device_id = ${response.device_id})"

                            // Navigate to mode selection
                            onRegistered()

                        } catch (e: Exception) {
                            statusText = "registration failed: ${e.message}"
                            e.printStackTrace()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text(if (isLoading) "Registering..." else "Register Device")
            }
        }
    }
}
