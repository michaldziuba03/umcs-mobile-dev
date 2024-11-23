package com.example.zad3

import android.os.Bundle
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
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Find place")
        }
        // 2.
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send SMS")
        }
        // 3.
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send email")
        }
        // 4.
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open calendar")
        }
        // 5.
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Make call")
        }
        // 6.
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open music player")
        }
        // 7.
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open docs")
        }
        // 8.
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open B activity")
        }
        // 9.
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Exit")
        }
    }
}
