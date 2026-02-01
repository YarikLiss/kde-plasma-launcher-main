package com.example.myapplication

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun Desktop(launcherViewModel: LauncherViewModel, settingsViewModel: DesktopSettingsViewModel, contentPadding: androidx.compose.foundation.layout.PaddingValues) {
    val pinnedApps by launcherViewModel.pinnedApps.collectAsState()
    val settings by settingsViewModel.settings.collectAsState()
    val configuration = LocalConfiguration.current
    val columns = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 6 else 4
    val rows = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 3 else 5
    val appsPerPage = columns * rows
    val pages = if (pinnedApps.isEmpty()) {
        listOf(emptyList())
    } else {
        pinnedApps.chunked(appsPerPage)
    }
    val pagerState = rememberPagerState(pageCount = { pages.size })
    var selectedApp by remember { mutableStateOf<AppInfo?>(null) }
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(contentPadding)
            .consumeWindowInsets(contentPadding)
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { pageIndex ->
            val pageApps = pages[pageIndex]
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxSize()
            ) {
                items(pageApps) { app ->
                    Column(
                        modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    selectedApp = app
                                    showMenu = true
                                },
                                onTap = { launcherViewModel.launchApp(app.packageName) }
                            )
                        }
                    ) {
                        DesktopAppIcon(
                            app = app,
                            onClick = { launcherViewModel.launchApp(app.packageName) },
                            settings = settings
                        )
                    }
                }
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Unpin from Desktop") },
                onClick = {
                    selectedApp?.let { launcherViewModel.unpinApp(it) }
                    showMenu = false
                }
            )
        }
    }
}
