package com.example.eventapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import kotlinx.coroutines.launch

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

class LoginActivity: ComponentActivity() {
    private lateinit var credentialManager: CredentialManager
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

        setContentView(R.layout.activity_login)
        // WITH EMAIL - PASSWORD
        auth = Firebase.auth
        val emailInput = findViewById<EditText>(R.id.emailInputLogin)
        val passwordInput = findViewById<EditText>(R.id.passwordInputLogin)
        val loginButton = findViewById<Button>(R.id.logginButton)
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MenuActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(
                            this,
                            "Login failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        // WITH GOOGLE
        credentialManager = CredentialManager.create(this)

        val webClientıd = BuildConfig.WEB_CLIENT_ID
        findViewById<Button>(R.id.loginWithGoogleButton).setOnClickListener {
            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientıd)
                .build()

            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            lifecycleScope.launch {
                try{
                    val result = credentialManager.getCredential(
                        request = request,
                        context = this@LoginActivity
                    )
                    handleSignIn(result)
                }catch (e: GetCredentialException) {
                    Log.e("error lifecycleScope", "error lifecycle ${e.message}", e)
                }
            }
        }

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

    private fun handleSignIn(result: GetCredentialResponse) {
        val credential = result.credential

        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        when {
                            googleIdTokenCredential != null -> {
                                val idToken = googleIdTokenCredential.idToken
                                val fireBaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                                auth.signInWithCredential(fireBaseCredential)
                                    .addOnCompleteListener(this) {
                                            task ->
                                        if (task.isSuccessful) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d("GoogleFirebase", "signInWithCredential:success")
                                            val user = auth.currentUser
                                            val intent = Intent(this, MenuActivity::class.java)
                                            startActivity(intent)

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w("GoogleFirebase", "signInWithCredential:failure", task.exception)

                                        }
                                    }
                            }
                            else -> {
                                // Shouldn't happen.
                                Log.d("GoogleFirebase", "No ID token!")
                            }
                        }

                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("s", "Received an invalid google id token response", e)
                    }
                }
                else {
                    Log.e("GoogleSignFail", "Unexpected credential")
                }
            }
            else -> {
                Log.e("GoogleSignFail", "Unexpected credential")
            }
        }
    }
}