package com.example.myapplication

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LauncherViewModel(private val context: Context) : ViewModel() {

    private val settingsManager = SettingsManager.getInstance(context)
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    private val _pinnedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val pinnedApps: StateFlow<List<AppInfo>> = _pinnedApps.asStateFlow()

    private val _pinnedAppPackages = MutableStateFlow<Set<String>>(emptySet())

    init {
        fetchInstalledApps()
        observePinnedApps()
    }

    private fun fetchInstalledApps() {
        viewModelScope.launch {
            val packageManager = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val activities = packageManager.queryIntentActivities(mainIntent, PackageManager.GET_META_DATA)

            val appInfos = activities
                .filter { it.activityInfo.packageName != context.packageName }
                .map { resolveInfo ->
                    AppInfo(
                        label = resolveInfo.loadLabel(packageManager),
                        packageName = resolveInfo.activityInfo.packageName,
                        icon = resolveInfo.loadIcon(packageManager)
                    )
                }
            _apps.value = appInfos
        }
    }

    private fun observePinnedApps() {
        viewModelScope.launch {
            settingsManager.settingsFlow.collect { settings ->
                _pinnedAppPackages.value = settings.pinnedApps
                updatePinnedAppInfos(settings.pinnedApps)
            }
        }
    }

    private fun updatePinnedAppInfos(pinnedAppPackages: Set<String>) {
        val packageManager = context.packageManager
        val pinnedAppInfos = pinnedAppPackages.mapNotNull { packageName ->
            try {
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                AppInfo(
                    label = appInfo.loadLabel(packageManager),
                    packageName = appInfo.packageName,
                    icon = appInfo.loadIcon(packageManager)
                )
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
        }.sortedBy { it.label.toString().lowercase() }
        _pinnedApps.value = pinnedAppInfos
    }

    fun pinApp(app: AppInfo) {
        viewModelScope.launch {
            val updated = _pinnedAppPackages.value + app.packageName
            settingsManager.updatePinnedApps(updated)
        }
    }

    fun unpinApp(app: AppInfo) {
        viewModelScope.launch {
            val updated = _pinnedAppPackages.value - app.packageName
            settingsManager.updatePinnedApps(updated)
        }
    }

    fun launchApp(packageName: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // Handle the case where the activity is not found
                e.printStackTrace()
            }
        }
    }
}
