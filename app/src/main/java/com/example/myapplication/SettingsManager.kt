
package com.example.myapplication

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(val context: Context) {

    companion object {
        private var instance: SettingsManager? = null

        fun getInstance(context: Context): SettingsManager {
            if (instance == null) {
                instance = SettingsManager(context.applicationContext)
            }
            return instance!!
        }

        val ICON_SCALE = floatPreferencesKey("icon_scale")
        val CORNER_RADIUS = floatPreferencesKey("corner_radius")
        val GRID_SPACING = floatPreferencesKey("grid_spacing")
        val PINNED_APPS = stringSetPreferencesKey("pinned_apps")
        val IS_DEFAULT_LAUNCHER = booleanPreferencesKey("is_default_launcher")
    }

    val settingsFlow = context.dataStore.data.map { preferences ->
        DesktopSettings(
            iconScale = preferences[ICON_SCALE] ?: 1.0f,
            cornerRadius = preferences[CORNER_RADIUS] ?: 15.0f,
            gridSpacing = preferences[GRID_SPACING] ?: 8.0f,
            pinnedApps = preferences[PINNED_APPS] ?: emptySet(),
            taskbarOpacity = 0.9f, //These are not in the scope of the new SettingsManager
            showClock = true //These are not in the scope of the new SettingsManager
        )
    }

    suspend fun updateIconScale(iconScale: Float) {
        context.dataStore.edit { settings ->
            settings[ICON_SCALE] = iconScale
        }
    }

    suspend fun updateCornerRadius(cornerRadius: Float) {
        context.dataStore.edit { settings ->
            settings[CORNER_RADIUS] = cornerRadius
        }
    }

    suspend fun updateGridSpacing(gridSpacing: Float) {
        context.dataStore.edit { settings ->
            settings[GRID_SPACING] = gridSpacing
        }
    }

    suspend fun updatePinnedApps(pinnedApps: Set<String>) {
        context.dataStore.edit { settings ->
            settings[PINNED_APPS] = pinnedApps
        }
    }

    suspend fun updateIsDefaultLauncher(isDefault: Boolean) {
        context.dataStore.edit { settings ->
            settings[IS_DEFAULT_LAUNCHER] = isDefault
        }
    }
}
