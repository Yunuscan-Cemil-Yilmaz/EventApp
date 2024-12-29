package com.example.eventapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.example.eventapp.databinding.ActivityNotificationsSettingsBinding
import com.google.firebase.firestore.FirebaseFirestore

class NotificationsSettingsActivity: ComponentActivity() {
    private lateinit var binding: ActivityNotificationsSettingsBinding
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val userId by lazy { auth.currentUser?.uid }
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications_settings)

        binding = ActivityNotificationsSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Switch referansları
        val notifSwitch = findViewById<Switch>(R.id.notificationsSwitchOnNotifSettings)
        val newEventsSwitch = findViewById<Switch>(R.id.newEventsSwitchOnNotifSettings)
        val customizedSwitch = findViewById<Switch>(R.id.customizedNotificationsSwitchOnNotifSettings)

        // Kullanıcı verilerini Firestore'dan getir
        userId?.let { uid ->
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Verileri oku ve Switch'leri ayarla
                        notifSwitch.isChecked = document.getBoolean("notifPerTimeComming") ?: false
                        newEventsSwitch.isChecked = document.getBoolean("notifPerNew") ?: false
                        customizedSwitch.isChecked = document.getBoolean("notifPerAddedNotif") ?: false
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error fetching data: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }

        // Save butonuna tıklama
        binding.saveButtonOnNotifSettings.setOnClickListener {
            val updatedData = mapOf(
                "notifPerTimeComming" to notifSwitch.isChecked,
                "notifPerNew" to newEventsSwitch.isChecked,
                "notifPerAddedNotif" to customizedSwitch.isChecked
            )

            userId?.let { uid ->
                firestore.collection("users").document(uid).update(updatedData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Settings updated successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error updating settings: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            } ?: run {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.listNotificationsButtonOnNotifSettings).setOnClickListener { navigateNotificationList() }
        findViewById<Button>(R.id.addNotificationButtonOnNotifSettings).setOnClickListener { navigateAddNotif() }
        findViewById<Button>(R.id.cancelButtonOnNotifSettings).setOnClickListener { navigateCancel() }
        findViewById<Button>(R.id.menuButtonOnNotifSettings).setOnClickListener { navigateMenu() }
    }

    override fun onStart() {
        super.onStart()

        val user = Firebase.auth.currentUser
        if(user != null) {
            Log.i("user", "${user.uid}")
        }else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateCancel(){
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun navigateMenu(){
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }

    private fun navigateAddNotif(){
        val intent = Intent(this, AddNotificationActivity::class.java)
        startActivity(intent)
    }

    private fun navigateNotificationList(){
        val intent = Intent(this, ListNotificationsActivity::class.java)
        startActivity(intent)
    }
}