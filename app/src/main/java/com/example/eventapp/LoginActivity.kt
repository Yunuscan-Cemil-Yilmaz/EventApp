package com.example.eventapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Intent
import android.widget.TextView
import android.widget.EditText

class LoginActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val navigateSignup = findViewById<TextView>(R.id.navigateSignupFromLogin)
        navigateSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        val navigateForgotPassword = findViewById<TextView>(R.id.navigateForgotPasswordFromLogin)
        navigateForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }
    }
}