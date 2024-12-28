package com.example.eventapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileActivity : ComponentActivity() {

    private lateinit var nicknameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var nameTextView: TextView
    private lateinit var surnameTextView: TextView
    private lateinit var resetPasswordButton: Button

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // XML bileşenlerini bağla
        nicknameTextView = findViewById(R.id.nicknameTextViewOnProfile)
        emailTextView = findViewById(R.id.emailTextViewOnProfile)
        nameTextView = findViewById(R.id.nameTextViewOnProfile)
        surnameTextView = findViewById(R.id.surnameTextViewOnProfile)
        resetPasswordButton = findViewById(R.id.resetPasswordButtonOnProfile)

        // navigates:
        findViewById<Button>(R.id.resetPasswordButtonOnProfile).setOnClickListener { navigateResetPassword() }
        findViewById<Button>(R.id.designProfileButtonOnProfile).setOnClickListener { navigateProfileSettings() }
        findViewById<Button>(R.id.menuButtonOnProfile).setOnClickListener { navigateMenu() }
        findViewById<Button>(R.id.notificationsSettingsButtonOnProfile).setOnClickListener { navigateNotificationsSettings() }
        findViewById<Button>(R.id.logoutButtonOnProfile).setOnClickListener { logout() }
    }

    override fun onStart() {
        super.onStart()

        val user = auth.currentUser
        if (user != null) {
            Log.i("user", "${user.uid}")
            writeUserDatas(user.uid)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun writeUserDatas(uid: String) {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(this, "Kullanıcı oturumu kapalı!", Toast.LENGTH_SHORT).show()
            return
        }

        val email = currentUser.email
        emailTextView.text = "Email: ${email ?: "undefined"}"

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val nickname = document.getString("userName") ?: "undefined"
                    val name = document.getString("name") ?: "undefined"
                    val surname = document.getString("surname") ?: "undefined"

                    nicknameTextView.text = "Nickname: $nickname"
                    nameTextView.text = "Name: $name"
                    surnameTextView.text = "Surname: $surname"

                    // Giriş türüne göre resetPasswordButton görünürlüğü
                    val isEmailPasswordLogin = checkIfEmailPasswordLogin(currentUser)
                    resetPasswordButton.visibility = if (isEmailPasswordLogin) View.VISIBLE else View.GONE
                } else {
                    Toast.makeText(this, "Kullanıcı verileri bulunamadı!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkIfEmailPasswordLogin(user: FirebaseUser): Boolean {
        // Kullanıcının giriş yaptığı sağlayıcıları kontrol ediyoruz
        for (userInfo in user.providerData) {
            if (userInfo.providerId == "password") {
                // E-posta ve şifre ile giriş yapmış
                return true
            }
        }
        // E-posta ve şifre dışında bir yöntemle giriş yapılmış (örneğin Google)
        return false
    }

    private fun navigateResetPassword(){
        val intent = Intent(this, ResetPasswordActivity::class.java)
        startActivity(intent)
    }

    private fun navigateProfileSettings() {
        val intent = Intent(this, ProfileSettingsActivity::class.java)
        startActivity(intent)
    }

    private fun navigateNotificationsSettings(){
        val intent = Intent(this, NotificationsSettingsActivity::class.java)
        startActivity(intent)
    }

    private fun navigateMenu(){
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }

    private fun logout(){
        Firebase.auth.signOut()
        navigateToLogin()
    }

    private fun navigateToLogin(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
