package com.example.eventapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Intent
import android.os.PersistableBundle
import android.widget.TextView
import android.widget.EditText

class ForgotPassword: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val navigateLogin = findViewById<TextView>(R.id.navigateLoginFromForgotPassword)
        navigateLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}