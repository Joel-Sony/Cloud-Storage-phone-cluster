package com.phonecluster.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.phonecluster.app.ml.EmbeddingEngine
import com.phonecluster.app.screens.FileBrowserScreen
import com.phonecluster.app.screens.ModeSelectionScreen
import com.phonecluster.app.screens.RegistrationScreen
import com.phonecluster.app.screens.SearchScreen
import com.phonecluster.app.screens.StorageModeScreen
import com.phonecluster.app.screens.UserModeScreen
import com.phonecluster.app.ui.theme.CloudStorageAppTheme

// Navigation destinations
sealed class Screen {
    object Registration : Screen()
    object ModeSelection : Screen()
    object UserMode : Screen()
    object StorageMode : Screen()
    object Search : Screen()
    object FileBrowser : Screen()
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }

        setContent {
            val engine = remember { EmbeddingEngine(this) }

            CloudStorageAppTheme {
                AppNavigation(engine = engine)
            }
        }
    }
}

@Composable
fun AppNavigation(engine: EmbeddingEngine) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Registration) }

    when (currentScreen) {
        Screen.Registration -> {
            RegistrationScreen(
                onRegistered = { currentScreen = Screen.ModeSelection }
            )
        }

        Screen.ModeSelection -> {
            ModeSelectionScreen(
                onUserModeClick = { currentScreen = Screen.UserMode },
                onStorageModeClick = { currentScreen = Screen.StorageMode }
            )
        }

        Screen.UserMode -> {
            UserModeScreen(
                engine = engine,
                onBackClick = { currentScreen = Screen.ModeSelection },
                onSearchClick = { currentScreen = Screen.Search },
                onBrowseClick = { currentScreen = Screen.FileBrowser }
            )
        }

        Screen.StorageMode -> {
            StorageModeScreen(
                onBackClick = { currentScreen = Screen.ModeSelection }
            )
        }

        Screen.Search -> {
            SearchScreen(
                engine = engine,
                onBackClick = { currentScreen = Screen.UserMode }
            )
        }

        Screen.FileBrowser -> {
            FileBrowserScreen(
                onBackClick = { currentScreen = Screen.UserMode }
            )
        }
    }
}