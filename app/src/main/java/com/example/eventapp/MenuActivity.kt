package com.example.eventapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MenuActivity: ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
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
            db = FirebaseFirestore.getInstance()
            checkAndSaveUserData(user.uid, user.displayName)
        }else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun logout(){
        Firebase.auth.signOut()
        navigateToLogin()
    }

    private fun checkAndSaveUserData(uid: String, displayName: String?){
        val userDocRef = db.collection("users").document(uid)

        userDocRef.get()
            .addOnSuccessListener { document ->
                if(!document.exists()){
                    val nameParts = displayName?.split(" ") ?: listOf("name", "surname")
                    val name = nameParts.dropLast(1).joinToString(" ") // Son kelime hariç tüm kelimeler isim
                    val surname = nameParts.last() // Son kelime soyisim
                    val userData = hashMapOf(
                        "name" to name,
                        "surname" to surname,
                        "userName" to "user",
                        "favs" to emptyList<String>(),
                        "commends" to emptyList<Map<String, Any>>(),
                        "scors" to emptyList<Map<String, Any>>(),
                        "notifPerTimeComming" to false,
                        "notifPerNew" to false,
                        "notifPerAddedNotif" to false,
                        "notifList" to emptyList<Map<String, Any>>()
                    )

                    userDocRef.set(userData)
                        .addOnSuccessListener {
                            Log.i("Firesote", "User saved successful")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Kullanıcı verisi kaydedilirken hata oluştu: ${e.message}")
                            logout()
                        }
                }else{
                    Log.i("Firestore", "User datas is already exists")
                }

                val userName = document.getString("userName") ?: "User" // Varsayılan olarak "User"
                val helloTextView = findViewById<TextView>(R.id.helloUserOnMenu)
                helloTextView.text = "Hello $userName"
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Firestore'dan veri alınırken hata oluştu: ${e.message}")
            }
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