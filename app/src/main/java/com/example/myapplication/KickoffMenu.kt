package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap

@Composable
fun KickoffMenu(viewModel: LauncherViewModel, settingsViewModel: DesktopSettingsViewModel, onDismiss: () -> Unit) {
    val apps by viewModel.apps.collectAsState()
    val settings by settingsViewModel.settings.collectAsState()
    val interactionSource = remember { MutableInteractionSource() }
    var selectedApp by remember { mutableStateOf<AppInfo?>(null) }
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onDismiss() })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.9f)
                .background(Color(0xFF232629).copy(alpha = 0.9f))
                .padding(16.dp)
                .focusProperties { canFocus = false }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {}
                )
        ) {
            LazyColumn {
                items(apps) { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusProperties { canFocus = false }
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        selectedApp = app
                                        showMenu = true
                                    },
                                    onTap = { viewModel.launchApp(app.packageName) }
                                )
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            bitmap = app.icon.toBitmap(48, 48).asImageBitmap(),
                            contentDescription = app.label.toString(),
                            modifier = Modifier
                                .size((32 * settings.iconScale).dp)
                                .clip(RoundedCornerShape(settings.cornerRadius.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = app.label.toString(), color = Color.White)
                    }
                }
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Pin to Desktop") },
                onClick = {
                    selectedApp?.let { viewModel.pinApp(it) }
                    showMenu = false
                }
            )
        }
    }
}
