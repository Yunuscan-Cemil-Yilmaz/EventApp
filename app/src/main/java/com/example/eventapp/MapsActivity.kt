package com.example.eventapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.eventapp.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.Marker
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mainEventsObject: MutableList<Event>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))


        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://app.ticketmaster.com/discovery/v2/events.json?apikey=RNzwKYAq7igX6IKQACGDFCkHkcqaJc8j&countryCode=TR&geoPoint=szj7530u&radius=600&unit=km")
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
                        runOnUiThread {
                            for (event in res) {
                                val marker: Marker? = mMap.addMarker(
                                    MarkerOptions().position(
                                        LatLng(
                                            event._embedded.venues[0].location.latitude.toDouble(),
                                            event._embedded.venues[0].location.longitude.toDouble()
                                        )
                                    ).title(event.name)
                                )
                            }
                        }
                    }
                }
            }

        })
    }
}