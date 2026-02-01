package com.example.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(settingsViewModel: DesktopSettingsViewModel) {
    val settings by settingsViewModel.settings.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Icon Scaling", style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Slider(
                value = settings.iconScale,
                onValueChange = { settingsViewModel.updateIconScale(it) },
                valueRange = 0.5f..1.5f,
                modifier = Modifier.weight(1f)
            )
            Text(String.format("%.2fx", settings.iconScale))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Corner Radius", style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Slider(
                value = settings.cornerRadius,
                onValueChange = { settingsViewModel.updateCornerRadius(it) },
                valueRange = 0f..30f,
                modifier = Modifier.weight(1f)
            )
            Text(String.format("%.0fdp", settings.cornerRadius))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Grid Spacing", style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Slider(
                value = settings.gridSpacing,
                onValueChange = { settingsViewModel.updateGridSpacing(it) },
                valueRange = 0f..32f,
                modifier = Modifier.weight(1f)
            )
            Text(String.format("%.0fdp", settings.gridSpacing))
        }
    }
}
