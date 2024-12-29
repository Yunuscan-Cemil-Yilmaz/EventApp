package com.example.eventapp

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class ListNotificationsActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_notifications)
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
}