package com.lowlauncher.app.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "low_launcher_settings")

class LauncherPreferences(private val context: Context) {

    companion object {
        val KEY_FAVORITE_PACKAGES = stringSetPreferencesKey("favorite_packages")
        val KEY_FRICTION_PACKAGES = stringSetPreferencesKey("friction_packages")
        val KEY_FRICTION_DELAY_SECONDS = intPreferencesKey("friction_delay_seconds")
        val KEY_SHOW_SCREEN_TIME = booleanPreferencesKey("show_screen_time")
        val KEY_PURE_TEXT_MODE = booleanPreferencesKey("pure_text_mode")
        val KEY_ENABLE_WALLPAPER = booleanPreferencesKey("enable_wallpaper")
        val KEY_WALLPAPER_DIM_PERCENT = intPreferencesKey("wallpaper_dim_percent")

        // Default: WhatsApp is favorited by default if installed
        val DEFAULT_FAVORITES = setOf("com.whatsapp", "com.whatsapp.w4b")
        val DEFAULT_FRICTION = setOf("com.instagram.android", "com.zhiliaoapp.musically", "com.google.android.youtube")
    }

    val favoritePackagesFlow: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[KEY_FAVORITE_PACKAGES] ?: DEFAULT_FAVORITES
    }

    val frictionPackagesFlow: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[KEY_FRICTION_PACKAGES] ?: DEFAULT_FRICTION
    }

    val frictionDelaySecondsFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_FRICTION_DELAY_SECONDS] ?: 4
    }

    val showScreenTimeFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_SHOW_SCREEN_TIME] ?: true
    }

    val pureTextModeFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_PURE_TEXT_MODE] ?: true
    }

    val enableWallpaperFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_ENABLE_WALLPAPER] ?: false
    }

    val wallpaperDimPercentFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_WALLPAPER_DIM_PERCENT] ?: 60
    }

    suspend fun toggleFavorite(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = (prefs[KEY_FAVORITE_PACKAGES] ?: DEFAULT_FAVORITES).toMutableSet()
            if (current.contains(packageName)) {
                current.remove(packageName)
            } else {
                current.add(packageName)
            }
            prefs[KEY_FAVORITE_PACKAGES] = current
        }
    }

    suspend fun toggleFriction(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = (prefs[KEY_FRICTION_PACKAGES] ?: DEFAULT_FRICTION).toMutableSet()
            if (current.contains(packageName)) {
                current.remove(packageName)
            } else {
                current.add(packageName)
            }
            prefs[KEY_FRICTION_PACKAGES] = current
        }
    }

    suspend fun setFrictionDelay(seconds: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_FRICTION_DELAY_SECONDS] = seconds.coerceIn(2, 10)
        }
    }

    suspend fun setShowScreenTime(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SHOW_SCREEN_TIME] = enabled
        }
    }

    suspend fun setPureTextMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_PURE_TEXT_MODE] = enabled
        }
    }

    suspend fun setEnableWallpaper(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ENABLE_WALLPAPER] = enabled
        }
    }

    suspend fun setWallpaperDimPercent(percent: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_WALLPAPER_DIM_PERCENT] = percent.coerceIn(10, 95)
        }
    }
}
