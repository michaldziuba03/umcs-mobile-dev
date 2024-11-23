package com.example.zad3

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.zad3.ui.theme.Zad3Theme
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Zad3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Sample(Modifier.padding(innerPadding))
                }
            }
        }
    }

    private fun openMap(location: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("geo:0,0?q=$location")
        }
        startActivity(intent)
    }

    private fun sendSms(phoneNumber: String, message: String) {
        if (checkSelfPermission(android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.SEND_SMS), 1)
        } else {
            val smsManager = android.telephony.SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        }
    }

    private fun openDocs() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            type = "application/*"
            putExtra(Intent.EXTRA_TITLE, "New Document")
        }
        startActivity(intent)
    }

    private fun sendEmail(address: String, subject: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$address")
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        startActivity(intent)
    }

    private fun makeCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(intent)
    }

    private fun openMusicPlayer() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            type = "audio/*"
        }
        startActivity(intent)
    }

    private fun openCalendar(dateTime: String, eventName: String) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = Uri.parse("content://com.android.calendar/events")
            putExtra(CalendarContract.Events.TITLE, eventName)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, parseDate(dateTime))
        }
        startActivity(intent)
    }

    private fun parseDate(dateTime: String): Long {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return formatter.parse(dateTime)?.time ?: System.currentTimeMillis()
    }

    private fun openSecondActivity(data: String) {
        val intent = Intent(this, SecondActivity::class.java).apply {
            putExtra("message", data)
        }
        startActivity(intent)
    }

    private fun exitApp() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    @Composable
    fun Sample(modifier: Modifier = Modifier) {
        var text1 by remember { mutableStateOf("") }
        var text2 by remember { mutableStateOf("") }

        Column(
            modifier = modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                label = { Text("Text 1") },
                value = text1,
                onValueChange = { text1 = it },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                label = { Text("Text 2") },
                value = text2,
                onValueChange = { text2 = it },
                modifier = Modifier.fillMaxWidth()
            )

            // 1.
            Button(
                onClick = { openMap(text1) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Find place")
            }
            // 2.
            Button(
                onClick = { sendSms(text1, text2) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send SMS")
            }
            // 3.
            Button(
                onClick = { sendEmail(text1, text2) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send email")
            }
            // 4.
            Button(
                onClick = { openCalendar(text1, text2) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open calendar")
            }
            // 5.
            Button(
                onClick = { makeCall(text2) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Make call")
            }
            // 6.
            Button(
                onClick = { openMusicPlayer() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open music player")
            }
            // 7.
            Button(
                onClick = { openDocs() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open docs")
            }
            // 8.
            Button(
                onClick = { openSecondActivity(text1) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open B activity")
            }
            // 9.
            Button(
                onClick = { exitApp() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Exit")
            }
        }
    }
}
