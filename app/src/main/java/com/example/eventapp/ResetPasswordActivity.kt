package com.example.eventapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Intent
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ResetPasswordActivity: ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        auth = Firebase.auth
        val emailInput = findViewById<EditText>(R.id.emialInputResetPassword)
        findViewById<Button>(R.id.resetButtonResetPassword).setOnClickListener {
            val email = emailInput.text.toString().trim()
            if (email.isEmpty()){
                Toast.makeText(this, "Please enter your email adress", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val currentUser = auth.currentUser
            val userEmail = currentUser!!.email
            if (userEmail == email){
                sendPasswprdResetEmail(email)
            }else{
                Toast.makeText(this, "Please enter your email address correctly.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if (user != null) {
            Log.i("user", "${user.uid}")
        } else {
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