package com.example.myapplication

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DesktopSettings(
    val iconScale: Float,
    val cornerRadius: Float,
    val gridSpacing: Float,
    val pinnedApps: Set<String>,
    val taskbarOpacity: Float,
    val showClock: Boolean
)

class DesktopSettingsViewModel(context: Context) : ViewModel() {

    private val settingsManager = SettingsManager.getInstance(context)

    val settings: StateFlow<DesktopSettings> = settingsManager.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = DesktopSettings(1.0f, 15.0f, 8.0f, emptySet(), 0.9f, true)
        )

    fun updateIconScale(iconScale: Float) {
        viewModelScope.launch {
            settingsManager.updateIconScale(iconScale)
        }
    }

    fun updateCornerRadius(cornerRadius: Float) {
        viewModelScope.launch {
            settingsManager.updateCornerRadius(cornerRadius)
        }
    }

    fun updateGridSpacing(gridSpacing: Float) {
        viewModelScope.launch {
            settingsManager.updateGridSpacing(gridSpacing)
        }
    }
}
