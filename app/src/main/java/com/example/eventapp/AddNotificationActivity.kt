package com.example.eventapp

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

object CalendarHolder {
    var calendar: Calendar = Calendar.getInstance()
}

class AddNotificationActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddNotificationScreen()
        }
        createNotificationChannel(this)
        checkAndRequestNotificationPermission(this)
    }

    override fun onStart() {
        super.onStart()

        val user = Firebase.auth.currentUser
        if (user != null) {
            Log.i("user", "${user.uid}")
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}

@Composable
fun AddNotificationScreen() {
    var notificationName by remember { mutableStateOf("") }
    var notificationDescription by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("No time selected") }

    val calendar = CalendarHolder.calendar
    Log.d("calendar init", calendar.toString())
    val context = LocalContext.current
    val user = Firebase.auth.currentUser
    val db = Firebase.firestore

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Add New Notification", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = notificationName,
            onValueChange = { notificationName = it },
            label = { Text("Notification Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = notificationDescription,
            onValueChange = { notificationDescription = it },
            label = { Text("Notification Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                calendar.set(Calendar.MINUTE, minute)
                                selectedTime = String.format(
                                    "%04d-%02d-%02d %02d:%02d",
                                    year, month + 1, dayOfMonth, hourOfDay, minute
                                )
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Date & Time")
        }

        Text(text = selectedTime)

        Button(
            onClick = {
                if (user != null && notificationName.isNotEmpty()) {
                    db.collection("users").document(user.uid)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                val notifPerAddedNotif = document.getBoolean("notifPerAddedNotif") ?: false
                                if (notifPerAddedNotif) {
                                    val notif = hashMapOf(
                                        "name" to notificationName,
                                        "description" to notificationDescription,
                                        "time" to calendar.timeInMillis,
                                        "type" to "notifPerAddedNotif"
                                    )

                                    db.collection("users").document(user.uid)
                                        .update("notifList", FieldValue.arrayUnion(notif))
                                        .addOnSuccessListener {
                                            Log.d("Firestore", "Notification added successfully!")
                                            val intent = Intent(context, NotificationsSettingsActivity::class.java)
                                            context.startActivity(intent)
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("Firestore", "Error adding notification", e)
                                        }

                                    Log.d("calendar show log", calendar.toString())
                                    scheduleNotification(context, calendar.timeInMillis, notificationDescription, notificationName)
                                } else {
                                    Log.d("Firestore", "User cannot add notifications as notifPerAddedNotif is false.")
                                }
                            } else {
                                Log.d("Firestore", "No such document")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error fetching document", e)
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }

        Button(
            onClick = { /* Cancel Logic Here */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}

fun scheduleNotification(context: Context, timeInMillis: Long, message: String, title: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        context.startActivity(intent)
        return
    }

    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("notification_message", message)
        putExtra("notification_title", title)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
        }
    } catch (e: SecurityException) {
        e.printStackTrace()
        // Kullanıcıya izin gerektiğini bildir
    }
}


fun checkAndRequestNotificationPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                (context as Activity),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }
    }
}

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Default Channel"
        val descriptionText = "This is the default notification channel."
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("default", name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
