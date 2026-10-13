package com.lowlauncher.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lowlauncher.app.data.model.AppItem
import com.lowlauncher.app.data.preferences.LauncherPreferences
import com.lowlauncher.app.data.repository.AppRepository
import com.lowlauncher.app.data.usage.DailyUsageInfo
import com.lowlauncher.app.data.usage.UsageTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val allApps: List<AppItem> = emptyList(),
    val favoriteApps: List<AppItem> = emptyList(),
    val whatsAppApp: AppItem? = null,
    val usageInfo: DailyUsageInfo = DailyUsageInfo(),
    val frictionDelaySeconds: Int = 4,
    val showScreenTime: Boolean = true,
    val pureTextMode: Boolean = true,
    val pendingFrictionApp: AppItem? = null,
    val isDrawerOpen: Boolean = false,
    val isSettingsOpen: Boolean = false,
    val isLoading: Boolean = true
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    private val preferences = LauncherPreferences(application)
    private val usageTracker = UsageTracker(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observePreferencesAndLoadApps()
        refreshUsageStats()
    }

    private fun observePreferencesAndLoadApps() {
        combine(
            preferences.favoritePackagesFlow,
            preferences.frictionPackagesFlow,
            preferences.frictionDelaySecondsFlow,
            preferences.showScreenTimeFlow,
            preferences.pureTextModeFlow
        ) { favorites, frictionApps, delaySecs, showScreenTime, pureText ->
            val apps = repository.getInstalledApps(favorites, frictionApps)
            val whatsApp = apps.find { it.isWhatsApp }
            // Filter favorites, but keep WhatsApp in its dedicated priority slot so it doesn't duplicate
            val favoriteList = apps.filter { it.isFavorite && !it.isWhatsApp }

            _uiState.value = _uiState.value.copy(
                allApps = apps,
                favoriteApps = favoriteList,
                whatsAppApp = whatsApp,
                frictionDelaySeconds = delaySecs,
                showScreenTime = showScreenTime,
                pureTextMode = pureText,
                isLoading = false
            )
        }.launchIn(viewModelScope)
    }

    fun refreshUsageStats() {
        val usage = usageTracker.getTodayUsage()
        _uiState.value = _uiState.value.copy(usageInfo = usage)
    }

    fun onAppClicked(app: AppItem) {
        // WhatsApp is ALWAYS friction-free
        if (app.isWhatsApp || !app.hasFrictionDelay) {
            launchAppDirectly(app)
        } else {
            // Trigger mindful friction delay
            _uiState.value = _uiState.value.copy(pendingFrictionApp = app)
        }
    }

    fun launchAppDirectly(app: AppItem) {
        _uiState.value = _uiState.value.copy(
            pendingFrictionApp = null,
            isDrawerOpen = false
        )
        repository.launchApp(app)
    }

    fun dismissFrictionDialog() {
        _uiState.value = _uiState.value.copy(pendingFrictionApp = null)
    }

    fun openDrawer() {
        _uiState.value = _uiState.value.copy(isDrawerOpen = true)
    }

    fun closeDrawer() {
        _uiState.value = _uiState.value.copy(isDrawerOpen = false)
    }

    fun openSettings() {
        _uiState.value = _uiState.value.copy(isSettingsOpen = true)
    }

    fun closeSettings() {
        _uiState.value = _uiState.value.copy(isSettingsOpen = false)
        refreshUsageStats()
    }

    fun toggleFavorite(packageName: String) {
        viewModelScope.launch {
            preferences.toggleFavorite(packageName)
        }
    }

    fun toggleFriction(packageName: String) {
        viewModelScope.launch {
            preferences.toggleFriction(packageName)
        }
    }

    fun setFrictionDelay(seconds: Int) {
        viewModelScope.launch {
            preferences.setFrictionDelay(seconds)
        }
    }

    fun toggleShowScreenTime(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setShowScreenTime(enabled)
        }
    }
}
