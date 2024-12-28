package com.example.eventapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Intent
import android.os.PersistableBundle
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ForgotPassword: ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userCheck = Firebase.auth.currentUser
        if(userCheck != null){
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_forgot_password)

        auth = Firebase.auth

        val emailInput = findViewById<EditText>(R.id.emialInputForgotpassword)
        val sendResetButton = findViewById<Button>(R.id.resetButton)
        sendResetButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            if(email.isEmpty()){
                Toast.makeText(this, "Please enter your email adress", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendPasswprdResetEmail(email)
        }

        val navigateLogin = findViewById<TextView>(R.id.navigateLoginFromForgotPassword)
        navigateLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun sendPasswprdResetEmail(email: String){
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    Toast.makeText(this, "Password reset link send to your email", Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Toast.makeText(this, "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}