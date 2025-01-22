package com.example.eventapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventapp.Event
import com.example.eventapp.EventMain
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.serialization.json.Json
import okhttp3.*
import java.io.IOException

class HomeActivity: AppCompatActivity() {
    private lateinit var mainEventsObject: MutableList<Event>
    private var adapter: EventMain_RecyclerViewAdapter = EventMain_RecyclerViewAdapter(this@HomeActivity, mutableListOf()){
        pos ->
            pos
    }
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mainEventsObject = mutableListOf()
        recyclerView = findViewById(R.id.event_recyclerview)
        val apiKey = BuildConfig.TICKET_MASTER_API
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://app.ticketmaster.com/discovery/v2/events?apikey=$apiKey")
            .build()

        findViewById<Button>(R.id.filterButton).setOnClickListener {
            val popupMenu = PopupMenu(this, findViewById(R.id.filterButton))
            popupMenu.menuInflater.inflate(R.menu.filter_menu, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
                    when(menuItem.itemId) {
                        // Music
                        R.id.option_1 -> {
                            adapter.updateData(mainEventsObject.filter { obj -> obj.classifications[0].segment.name.lowercase() == "music" })
                            true
                        }
                        // Sports
                        R.id.option_2 -> {
                            adapter.updateData(mainEventsObject.filter { obj -> obj.classifications[0].segment.name.lowercase() == "sports" })
//                            binding.removeFilterButton.visibility = View.VISIBLE
                            true
                        }
                        // Arts & Theatre
                        R.id.option_3 -> {
                            adapter.updateData(mainEventsObject.filter { obj -> obj.classifications[0].segment.name.lowercase() == "arts & theatre" })
                            true
                        }
                        // Film
                        R.id.option_4 -> {
                            adapter.updateData(mainEventsObject.filter { obj -> obj.classifications[0].segment.name.lowercase() == "film" })
                            true
                        }
                        // Non-ticket
                        R.id.option_5 -> {
                            adapter.updateData(mainEventsObject.filter { obj -> obj.classifications[0].segment.name.lowercase() == "nonticket" })
                            true
                        }
                        // Miscellaneous
                        R.id.option_6 -> {
                            adapter.updateData(mainEventsObject.filter { obj -> obj.classifications[0].segment.name.lowercase() == "miscellaneous" })
                            true
                        }
                        else -> {
                            false
                        }
                    }

            }
            popupMenu.show()
        }

        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if(response.isSuccessful){
                        val json = Json { ignoreUnknownKeys = true }
                        val res: List<Event> = json.decodeFromString<EventMain>(response.body!!.string())._embedded.events
                        mainEventsObject = res.toMutableList()
                        adapter = EventMain_RecyclerViewAdapter(this@HomeActivity, res.toMutableList()) {
                            position ->
                            val intent = Intent(this@HomeActivity, EventDetailsActivity::class.java)
                            intent.putExtra("event", json.encodeToString(Event.serializer(), res.get(position)))
                            startActivity(intent)
                        }
                        runOnUiThread{
                            recyclerView.adapter = adapter
                            recyclerView.layoutManager = LinearLayoutManager(this@HomeActivity)
                            mainEventsObject.addAll(res)
                        }
                    }
                }
            }

        })

        val searchBar = findViewById<EditText>(R.id.searchEditTextOnMenu)
        searchBar.addTextChangedListener{ query ->
            val myPredicate: (Event) -> Boolean = { obj -> query.toString().lowercase() in obj.name.lowercase() }
            val result: List<Event> = mainEventsObject.filter(myPredicate).toMutableList()
            adapter.updateData(result)
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