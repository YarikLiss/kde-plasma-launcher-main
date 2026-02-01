package com.example.myapplication

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PlasmaTaskbar(
    viewModel: LauncherViewModel,
    settingsViewModel: DesktopSettingsViewModel,
    onKButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pinnedApps by viewModel.pinnedApps.collectAsState()
    val settings by settingsViewModel.settings.collectAsState()
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFF232629).copy(alpha = settings.taskbarOpacity))
            .border(width = 1.dp, color = Color(0xFF3daee9)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            KButton(onClick = onKButtonClick)
            Spacer(modifier = Modifier.width(8.dp))
            PinnedApps(apps = pinnedApps, settings = settings, onAppClick = { viewModel.launchApp(it) })
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (settings.showClock) {
                DigitalClock()
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .focusProperties { canFocus = false }
                    .clickable { context.startActivity(Intent(context, SettingsActivity::class.java)) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.MoreVert, // Placeholder for widgets
                contentDescription = "Widgets",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .focusProperties { canFocus = false }
                    .clickable { context.startActivity(Intent(context, WidgetActivity::class.java)) }
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@Composable
fun KButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .padding(start = 8.dp)
            .size(40.dp)
            .focusProperties { canFocus = false }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "K",
            color = Color(0xFF3daee9),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PinnedApps(apps: List<AppInfo>, settings: DesktopSettings, onAppClick: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(apps) { app ->
            AppIcon(app = app, settings = settings, onClick = { onAppClick(app.packageName) })
        }
    }
}

@Composable
fun AppIcon(app: AppInfo, settings: DesktopSettings, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Image(
        bitmap = app.icon.toBitmap(96, 96).asImageBitmap(),
        contentDescription = app.label.toString(),
        modifier = Modifier
            .size((48 * settings.iconScale).dp)
            .padding(4.dp)
            .clip(RoundedCornerShape(settings.cornerRadius.dp))
            .focusProperties { canFocus = false }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick)
    )
}

@Composable
fun DigitalClock() {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }

    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    Text(
        text = timeFormat.format(Date(currentTime)),
        color = Color.White,
        fontSize = 14.sp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}
