package com.example.zad4

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.zad4.ui.theme.Zad4Theme
import java.util.Calendar

class MainActivity : ComponentActivity() {
    companion object {
        const val CHANNEL_ID = "default_channel"
        const val NOTIFICATION_ID = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel()
        setContent {
            Zad4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Sample(Modifier.padding(innerPadding))
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "default"
            val descriptionText = "Kanał domyślny"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)}
    }

    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun sendSimpleNotification(context: Context, title: String, message: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.clock1)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Sample(modifier: Modifier = Modifier) {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var details by remember { mutableStateOf("") }
        var permissionGranted by remember { mutableStateOf(checkNotificationPermission()) }
        val requestPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            permissionGranted = isGranted
            if (!isGranted) {
                Toast.makeText(
                    this,
                    "Brak zezwolenia ! Nie można wyświetlić powiadomienia.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val icons = listOf(
            R.drawable.clock800,
            R.drawable.clock1,
        )
        var selectedIcon by remember { mutableStateOf(icons[0]) }

        val styles = listOf(
            "Default",
            "BigText",
            "BigPicture",
        )
        var selectedStyle by remember { mutableStateOf(styles[0]) }

        Column(
            modifier = modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                label = { Text("Title") },
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                label = { Text("Description") },
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                label = { Text("Extended description") },
                value = details,
                onValueChange = { details = it },
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                modifier = Modifier.background(Color(0xFFF0F0F0))
            ) {
                icons.forEach { iconEl ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE3E3E3))
                            .selectable(
                                selected = selectedIcon == iconEl,
                                onClick = { selectedIcon = iconEl },
                                role = Role.RadioButton
                            )
                            .padding(8.dp)
                    ) {
                        RadioButton(
                            selected = selectedIcon == iconEl,
                            onClick = { selectedIcon = iconEl }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Image(
                            painter = painterResource(id = iconEl),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.background(Color(0xFFF0F0F0))
            ) {
                styles.forEach { style ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE3E3E3))
                            .selectable(
                                selected = selectedStyle.equals(style),
                                onClick = { selectedStyle = style },
                                role = Role.RadioButton
                            )
                            .padding(8.dp)
                    ) {
                        RadioButton(
                            selected = selectedStyle.equals(style),
                            onClick = { selectedStyle = style }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(style)
                    }
                }
            }

            var showDialog by remember { mutableStateOf(false) }
            if (showDialog) {
                val currentTime = Calendar.getInstance()
                val timePickerState = rememberTimePickerState(
                    initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                    initialMinute = currentTime.get(Calendar.MINUTE),
                    is24Hour = true,
                )

                TimePickerDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = { showDialog = false }
                ) {
                    TimePicker(
                        state = timePickerState,
                    )
                }
            }

            Button(onClick = { showDialog = true }) {
                Text("Pick alarm time")
            }

            val context = LocalContext.current
            Button(onClick = {
                if (permissionGranted) {
                    sendSimpleNotification(context, title, description)
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }) {
                Text("Send notification")
            }
        }
    }

    @Composable
    fun TimePickerDialog(
        onDismiss: () -> Unit,
        onConfirm: () -> Unit,
        content: @Composable () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Dismiss")
                }
            },
            confirmButton = {
                TextButton(onClick = { onConfirm() }) {
                    Text("OK")
                }
            },
            text = { content() }
        )
    }
}
