package com.phonecluster.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.phonecluster.app.screens.ModeSelectionScreen
import com.phonecluster.app.screens.RegistrationScreen
import com.phonecluster.app.screens.UserModeScreen
import com.phonecluster.app.screens.StorageModeScreen
import com.phonecluster.app.ui.theme.CloudStorageAppTheme

// Navigation destinations
sealed class Screen {
    object Registration : Screen()
    object ModeSelection : Screen()
    object UserMode : Screen()
    object StorageMode : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CloudStorageAppTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Registration) }

    when (currentScreen) {
        Screen.Registration -> {
            RegistrationScreen(
                onRegistered = {
                    currentScreen = Screen.ModeSelection
                }
            )
        }

        Screen.ModeSelection -> {
            ModeSelectionScreen(
                onUserModeClick = {
                    currentScreen = Screen.UserMode
                },
                onStorageModeClick = {
                    currentScreen = Screen.StorageMode
                }
            )
        }

        Screen.UserMode -> {
            UserModeScreen(
                onBackClick = {
                    currentScreen = Screen.ModeSelection
                }
            )
        }

        Screen.StorageMode -> {
            StorageModeScreen(
                onBackClick = {
                    currentScreen = Screen.ModeSelection
                }
            )
        }
    }
}