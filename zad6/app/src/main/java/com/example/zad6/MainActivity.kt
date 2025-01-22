package com.example.zad6

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.zad6.ui.theme.Zad6Theme

class BookReceiver(private val onDataReceived: (Book) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "com.example.BOOK_DOWNLOADED") {
            val title = intent.getStringExtra("title") ?: "Unknown"
            val wordCount = intent.getIntExtra("wordCount", 0)
            val charCount = intent.getIntExtra("charCount", 0)
            val mostCommonWord = intent.getStringExtra("mostCommonWord") ?: "None"
            onDataReceived(Book(title, wordCount, charCount, mostCommonWord))
        }
    }
}

data class Book(
    val title: String,
    val wordCount: Int,
    val charCount: Int,
    val mostCommonWord: String
)

class MainActivity : ComponentActivity() {
    companion object {
        const val CHANNEL_ID = "default_channel"
        const val NOTIFICATION_ID = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        setContent {
            Zad6Theme {
                BookApp()
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
    private fun sendSimpleNotification(context: Context, title: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("$title has been downloaded.")
            .setContentText("Book downloaded")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    @Composable
    fun BookApp() {
        val context = LocalContext.current
        var bookList by remember { mutableStateOf(listOf<Book>()) }
        val bookServiceIntent = Intent(context, BookService::class.java)
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

        DisposableEffect(Unit) {
            val receiver = BookReceiver { book ->
                Log.d("BookApp", "Received book")
                if (bookList.none { it.title == book.title }) {
                    bookList = bookList + book
                    if (permissionGranted) {
                        sendSimpleNotification(context, book.title)
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
            val intentFilter = IntentFilter("com.example.BOOK_DOWNLOADED")
            context.registerReceiver(receiver, intentFilter, Context.RECEIVER_EXPORTED)

            onDispose {
                context.unregisterReceiver(receiver)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Button(onClick = { context.startService(bookServiceIntent) }) {
                Text("Start Book Download Service")
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(bookList) { book ->
                    Text("Title: ${book.title}", modifier = Modifier.padding(8.dp))
                    Text("Word Count: ${book.wordCount}", modifier = Modifier.padding(8.dp))
                    Text("Char Count: ${book.charCount}", modifier = Modifier.padding(8.dp))
                    Text("Most Common Word: ${book.mostCommonWord}", modifier = Modifier.padding(8.dp))
                    HorizontalDivider()
                }
            }
        }
    }
}
