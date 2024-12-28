package com.example.eventapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileSettingsActivity : ComponentActivity() {

    private lateinit var nicknameEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var menuButton: Button

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)

        // View bağlantıları
        nicknameEditText = findViewById(R.id.nicknameEditTextOnProfileSettings)
        nameEditText = findViewById(R.id.nameEditTextOnProfileSettings)
        surnameEditText = findViewById(R.id.surnameEditTextOnProfileSettings)
        saveButton = findViewById(R.id.saveButtonOnProfileSettings)
        cancelButton = findViewById(R.id.cancelButtonOnProfileSettings)
        menuButton = findViewById(R.id.menuButtonOnProfileSettings)

        // Buton dinleyicileri
        cancelButton.setOnClickListener { navigateToCancel() }
        menuButton.setOnClickListener { navigateToMenu() }
        saveButton.setOnClickListener { saveUserData() }

        val currentUser = auth.currentUser
        if (currentUser != null) {
            loadUserData(currentUser.uid)
        }
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun loadUserData(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    nicknameEditText.setText(document.getString("userName") ?: "")
                    if (isGoogleLogin()) {
                        findViewById<TextView>(R.id.nameLabelOnProfileSettings).visibility = View.GONE
                        nameEditText.visibility = View.GONE
                        findViewById<TextView>(R.id.surnameLabelOnProfileSettings).visibility = View.GONE
                        surnameEditText.visibility = View.GONE
                    } else {
                        nameEditText.setText(document.getString("name") ?: "")
                        surnameEditText.setText(document.getString("surname") ?: "")
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "cant loaded user datas!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun isGoogleLogin(): Boolean {
        val user = auth.currentUser
        return user?.providerData?.any { it.providerId == "google.com" } == true
    }

    private fun saveUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val isGoogleLogin = isGoogleLogin()

            if (!isGoogleLogin) {
                if (!isValidInput(nicknameEditText.text.toString(), "Nickname") ||
                    !isValidInput(nameEditText.text.toString(), "Name") ||
                    !isValidInput(surnameEditText.text.toString(), "Surname")
                ) return
            } else {
                if (!isValidInput(nicknameEditText.text.toString(), "Nickname")) return
            }

            val userData = mutableMapOf<String, Any>(
                "userName" to nicknameEditText.text.toString()
            )
            if (!isGoogleLogin) {
                userData["name"] = nameEditText.text.toString()
                userData["surname"] = surnameEditText.text.toString()
            }

            db.collection("users").document(currentUser.uid)
                .update(userData)
                .addOnSuccessListener {
                    Toast.makeText(this, "changes are succesful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error changes!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun isValidInput(input: String, fieldName: String): Boolean {
        if (input.isBlank()) {
            Toast.makeText(this, "$fieldName can not be empty!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (input.length < 3) {
            Toast.makeText(this, "$fieldName minimum length is 3 character!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun navigateToCancel() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    private fun navigateToMenu() {
        startActivity(Intent(this, MenuActivity::class.java))
    }
}
