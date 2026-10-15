package com.lowlauncher.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.lowlauncher.app.ui.calendar.CalendarViewModel
import com.lowlauncher.app.ui.home.HomeScreen
import com.lowlauncher.app.ui.home.HomeViewModel
import com.lowlauncher.app.ui.theme.LowLauncherTheme
import com.lowlauncher.app.ui.theme.PureBlack

class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val calendarViewModel: CalendarViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val state by homeViewModel.uiState.collectAsState()

            // Handle back press intentionally: home screen cannot exit
            BackHandler(enabled = state.isDrawerOpen || state.isSettingsOpen || state.pendingFrictionApp != null) {
                when {
                    state.pendingFrictionApp != null -> homeViewModel.dismissFrictionDialog()
                    state.isDrawerOpen -> homeViewModel.closeDrawer()
                    state.isSettingsOpen -> homeViewModel.closeSettings()
                }
            }

            val bgAlpha = if (state.enableWallpaper) (state.wallpaperDimPercent / 100f) else 1f
            val backgroundColor = PureBlack.copy(alpha = bgAlpha)

            LowLauncherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = backgroundColor
                ) {
                    HomeScreen(
                        homeViewModel = homeViewModel,
                        calendarViewModel = calendarViewModel
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh usage stats each time the user returns to the launcher
        homeViewModel.refreshUsageStats()
    }
}
