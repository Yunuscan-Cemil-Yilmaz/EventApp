package com.example.eventapp

import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class FavoriteEventsActivity: AppCompatActivity() {
    private lateinit var mainEventsObject: MutableList<Event>
    private var adapter: EventMain_RecyclerViewAdapter = EventMain_RecyclerViewAdapter(this@FavoriteEventsActivity, mutableListOf()){
            pos ->
        pos
    }
    private lateinit var recyclerView: RecyclerView
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_events)

        var favs = mutableListOf<String>()
        var user = Firebase.auth.currentUser
        val userDocRef = db.collection("users").document(user!!.uid)
        userDocRef.get().addOnSuccessListener { response ->
            favs = response.get("favs") as MutableList<String>

            mainEventsObject = mutableListOf()
            recyclerView = findViewById(R.id.event_fav_recyclerview)
            val apiKey = BuildConfig.TICKET_MASTER_API
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://app.ticketmaster.com/discovery/v2/events?apikey=$apiKey")
                .build()

            client.newCall(request).enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if(response.isSuccessful){
                            val json = Json { ignoreUnknownKeys = true }
                            val res: List<Event> = json.decodeFromString<EventMain>(response.body!!.string())._embedded.events
                            mainEventsObject = res.toMutableList()
                            adapter = EventMain_RecyclerViewAdapter(this@FavoriteEventsActivity, res.filter{ obj -> favs.contains(obj.id) }.toMutableList()) {
                                    position ->
                                val intent = Intent(this@FavoriteEventsActivity, EventDetailsActivity::class.java)
                                intent.putExtra("event", json.encodeToString(Event.serializer(), res.get(position)))
                                startActivity(intent)
                            }
                            runOnUiThread{
                                recyclerView.adapter = adapter
                                recyclerView.layoutManager = LinearLayoutManager(this@FavoriteEventsActivity)
                                mainEventsObject.addAll(res)
                            }
                        }
                    }
                }
            })


        }.addOnFailureListener { err ->
            Toast.makeText(this, "user data cant found", Toast.LENGTH_SHORT).show()
        }

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