package com.example.zad9

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.zad9.ui.theme.Zad9Theme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun LogRegScreen(modifier: Modifier, onLoginSuccess: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Haslo") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onLoginSuccess()
                    } else {
                    }
                }
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text("Zaloguj")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onLoginSuccess()
                    } else {
                    }
                }
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text("Zarejestruj")
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier) {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    var accelerometerValues by remember { mutableStateOf(floatArrayOf(0f, 0f, 0f)) }
    var proximityValue by remember { mutableStateOf(0f) }
    var lightValue by remember { mutableStateOf(0f) }
    var magnetometerValues by remember { mutableStateOf(floatArrayOf(0f, 0f, 0f)) }

    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    when (it.sensor.type) {
                        Sensor.TYPE_ACCELEROMETER -> accelerometerValues = it.values.clone()
                        Sensor.TYPE_PROXIMITY -> proximityValue = it.values[0]
                        Sensor.TYPE_LIGHT -> lightValue = it.values[0]
                        Sensor.TYPE_MAGNETIC_FIELD -> magnetometerValues = it.values.clone()
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    LaunchedEffect(Unit) {
        listOfNotNull(accelerometer, proximitySensor, lightSensor, magnetometer).forEach { sensor ->
            sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    val firestore = Firebase.firestore
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Akcelerometr: x=${accelerometerValues[0]} y=${accelerometerValues[1]} z=${accelerometerValues[2]}")
        Text("Zbliżeniowy: $proximityValue")
        Text("Światło: $lightValue")
        Text("Magnetometr: x=${magnetometerValues[0]} y=${magnetometerValues[1]} z=${magnetometerValues[2]}")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val user = auth.currentUser
            if (user != null) {
                val sensorData = mapOf(
                    "accelerometer" to accelerometerValues.toList(),
                    "proximity" to proximityValue,
                    "light" to lightValue,
                    "magnetometer" to magnetometerValues.toList(),
                    "timestamp" to System.currentTimeMillis()
                )
                firestore.collection("users").document(user.uid).collection("sensors")
                    .add(sensorData)
                    .addOnSuccessListener { /* Sukces */ }
                    .addOnFailureListener { /* Obsługa błędów */ }
            }
        }) {
            Text("Zapisz do Firestore")
        }
    }
}


class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContent {
            Zad9Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding
                    ->
                    AppContent(
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        FirebaseAuth.getInstance().signOut()
    }

    @Composable
    fun AppContent(modifier: Modifier) {
        var isLoggedIn by remember {
            mutableStateOf(
                auth.currentUser !=
                        null
            )
        }
        if (!isLoggedIn) {
            LogRegScreen(
                modifier = modifier,
                onLoginSuccess = {
                    isLoggedIn = true
                }
            )
        } else {
            MainScreen(modifier)
        }
    }
}
