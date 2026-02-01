package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties

@Composable
fun SetHomePrompt(context: Context, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Set Default Home") },
        text = { Text(text = "Would you like to set KDE Plasma as your default Home screen?") },
        confirmButton = {
            Button(
                onClick = {
                    val intent = Intent(Settings.ACTION_HOME_SETTINGS)
                    context.startActivity(intent)
                    onDismiss()
                },
                modifier = Modifier.focusProperties { canFocus = false }
            ) {
                Text("Open Settings")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.focusProperties { canFocus = false }
            ) {
                Text("Dismiss")
            }
        }
    )
}
