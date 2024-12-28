package com.example.eventapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MenuActivity: ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val logoutButton = findViewById<Button>(R.id.logOutButtonOnMenu)
        logoutButton.setOnClickListener {
            logout()
        }

        // Navigate Events:
        findViewById<Button>(R.id.navigateHomeFromMenu).setOnClickListener { navigateToHome() }
        findViewById<Button>(R.id.navigateMapFromMenu).setOnClickListener { navigateToMap() }
        findViewById<Button>(R.id.navigateToProfileFromMenu).setOnClickListener { navigateToProfile() }
        findViewById<Button>(R.id.navigateToNotifications).setOnClickListener { navigateToNotifications() }
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

    private fun logout(){
        Firebase.auth.signOut()
        navigateToLogin()
    }
    private fun navigateToLogin(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHome(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToMap(){
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToProfile(){
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToNotifications(){
        val intent = Intent(this, NotificationsSettingsActivity::class.java)
        startActivity(intent)
    }
}