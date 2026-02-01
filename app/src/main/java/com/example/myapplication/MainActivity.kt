package com.example.myapplication

import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    private lateinit var launcherViewModel: LauncherViewModel
    private lateinit var settingsViewModel: DesktopSettingsViewModel

    private val requestRoleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (isDefaultLauncher()) {
            recreate()
        } else {
            Toast.makeText(this, "Plasma is not the default launcher.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setImmersiveMode()

        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when {
                    modelClass.isAssignableFrom(LauncherViewModel::class.java) ->
                        LauncherViewModel(applicationContext) as T
                    modelClass.isAssignableFrom(DesktopSettingsViewModel::class.java) ->
                        DesktopSettingsViewModel(applicationContext) as T
                    else -> throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
        launcherViewModel = ViewModelProvider(this, viewModelFactory).get(LauncherViewModel::class.java)
        settingsViewModel = ViewModelProvider(this, viewModelFactory).get(DesktopSettingsViewModel::class.java)

        setContent {
            if (isDefaultLauncher()) {
                var isMenuOpen by remember { mutableStateOf(false) }
                val taskbarHeight = 56.dp

                Box(modifier = Modifier.fillMaxSize()) {
                    Desktop(
                        launcherViewModel = launcherViewModel,
                        settingsViewModel = settingsViewModel,
                        contentPadding = PaddingValues(bottom = taskbarHeight)
                    )

                    AnimatedVisibility(
                        visible = isMenuOpen,
                        enter = slideIn(initialOffset = { IntOffset(-it.width, it.height) }),
                        exit = slideOut(targetOffset = { IntOffset(-it.width, it.height) })
                    ) {
                        KickoffMenu(
                            viewModel = launcherViewModel,
                            settingsViewModel = settingsViewModel,
                            onDismiss = { isMenuOpen = false }
                        )
                    }

                    PlasmaTaskbar(
                        viewModel = launcherViewModel,
                        settingsViewModel = settingsViewModel,
                        onKButtonClick = { isMenuOpen = !isMenuOpen },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            } else {
                WelcomeScreen { requestSetDefaultLauncher() }
            }
        }
    }

    private fun isDefaultLauncher(): Boolean {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo?.activityInfo?.packageName == packageName
    }

    private fun requestSetDefaultLauncher() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(ROLE_SERVICE) as RoleManager
            if (roleManager.isRoleAvailable(RoleManager.ROLE_HOME) && !roleManager.isRoleHeld(RoleManager.ROLE_HOME)) {
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
                requestRoleLauncher.launch(intent)
            }
        } else {
            val intent = Intent(Settings.ACTION_HOME_SETTINGS)
            startActivity(intent)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setImmersiveMode()
        }
    }

    private fun setImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

@Composable
fun WelcomeScreen(onStartClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Welcome to KDE Plasma",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "To get the full experience, please set Plasma as your default launcher.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onStartClick,
                modifier = Modifier.focusProperties { canFocus = false }
            ) {
                Text("Start")
            }
        }
    }
}
