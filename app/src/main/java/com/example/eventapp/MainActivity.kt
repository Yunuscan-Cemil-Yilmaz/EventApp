package com.example.eventapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import android.provider.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class MainActivity : ComponentActivity() {

    private val REQUEST_CODE = 123 // Sabit bir değer
    private val permissionAl = Manifest.permission.SCHEDULE_EXACT_ALARM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // LoginActivity'e yönlendirme
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

        // Android 13 ve sonrasında izin istemek
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (!isGranted) {
                    // Kullanıcı izni reddetti, uygun bir işlem yapabilirsiniz
                }
            }

            // POST_NOTIFICATIONS iznini kontrol et ve talep et
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            if (ActivityCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(permission)
            }
        }


        // SCHEDULE_EXACT_ALARM izni kontrolü
//        if (ContextCompat.checkSelfPermission(this, permissionAl) != PackageManager.PERMISSION_GRANTED) {
//            // Kullanıcıdan izin iste
//            ActivityCompat.requestPermissions(this, arrayOf(permissionAl), REQUEST_CODE)
//        } else {
//            // İzin zaten var, alarmı ayarla
//            val calendar = Calendar.getInstance()
//            calendar.set(Calendar.HOUR_OF_DAY, 9)
//            calendar.set(Calendar.MINUTE, 30)
//            calendar.set(Calendar.SECOND, 0)
//
//            val notificationName = "My Notification"
//            scheduleNotification(this, calendar.timeInMillis, notificationName)
//        }

        // SCHEDULE_EXACT_ALARM izni kontrolü
        if (ContextCompat.checkSelfPermission(this, permissionAl) != PackageManager.PERMISSION_GRANTED) {
            // Kullanıcıdan izin iste
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            startActivity(intent)
        } else {
            // İzin zaten var, alarmı ayarla
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 9)
            calendar.set(Calendar.MINUTE, 30)
            calendar.set(Calendar.SECOND, 0)

            val notificationName = "My Notification"
            scheduleNotification(this, calendar.timeInMillis, notificationName)
        }
    }

    // Bildirim zamanlamasını yapacak fonksiyon
    private fun scheduleNotification(context: Context, timeInMillis: Long, message: String) {
        // Burada alarm ayarlama işlemi yapılacak (AlarmManager veya WorkManager kullanabilirsiniz)
        // AlarmManager ya da WorkManager kullanarak bildirim zamanlamasını gerçekleştirebilirsiniz
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MaterialTheme {
        Greeting("Android")
    }
}
