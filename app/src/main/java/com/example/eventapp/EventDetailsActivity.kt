package com.example.eventapp

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.serialization.json.Json
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.Cursor
import android.opengl.Visibility
import android.provider.CalendarContract
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.TimeZone

class EventDetailsActivity: ComponentActivity() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_datils)
//        getCalendars()




        val thisEvent = Json.decodeFromString<Event>(intent.getStringExtra("event") ?: "undefined")
        findViewById<TextView>(R.id.eventTitle).setText(thisEvent.name)
        findViewById<TextView>(R.id.eventDateTime).text = formatEventDateTime(
            thisEvent.dates.start.localDate,
            thisEvent.dates.start.localTime
        )
        val segment = thisEvent.classifications[0].segment.name
        val genre = thisEvent.classifications[0].genre.name
        val saleStart = formattedSaleTime(thisEvent.sales.public.startDateTime)
        val saleEnd = formattedSaleTime(thisEvent.sales.public.endDateTime)
        val venues = thisEvent._embedded.venues[0].city.name + "/" + thisEvent._embedded.venues[0].country.name + "\n" + thisEvent._embedded.venues[0].address.line1
        findViewById<TextView>(R.id.eventDescription).setText("$segment $genre \nstart: $saleStart | end: $saleEnd \n $venues")

        val joinButton = findViewById<Button>(R.id.joinButton)
        joinButton.setOnClickListener {
            val userDocRef = db.collection("users").document(auth.currentUser!!.uid)
            userDocRef.get()
                .addOnSuccessListener { response ->
                    var events = mutableListOf<String>()
                    userDocRef.get()
                        .addOnSuccessListener { response ->
                            if (response != null && response.exists()) {
                                events = response.get("events") as MutableList<String>
                            }

                            if(events.contains(thisEvent.id)){
                                Toast.makeText(this, "You have already joined this event!", Toast.LENGTH_SHORT).show()
                            }else {
                                events.add(thisEvent.id)

                                userDocRef.update("events", events)
                                    .addOnSuccessListener { response ->
                                        createReminderOnCalendar(thisEvent)
                                        Toast.makeText(this, "successfull join", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                    .addOnFailureListener { error ->
                                        Toast.makeText(
                                            this,
                                            "add event process is failed!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }.addOnFailureListener() {
                            Toast.makeText(this, "Cant get user datas!", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { error ->
                    Toast.makeText(this, "System error, please try again later or contact with officals", Toast.LENGTH_LONG).show()
                }
        }

        val addFavButton = findViewById<Button>(R.id.addFavButton)
        addFavButton.setOnClickListener {
            val userDocRef = db.collection("users").document(auth.currentUser!!.uid)
            userDocRef.get()
                .addOnSuccessListener { response ->
                    var favs = mutableListOf<String>()
                    userDocRef.get()
                        .addOnSuccessListener { response ->
                            if(response != null && response.exists()){
                                favs = response.get("favs") as MutableList<String>
                            }

                            if(favs.contains(thisEvent.id)){
                                Toast.makeText(this, "You already add favorites this event!", Toast.LENGTH_SHORT).show()
                            }else{
                                favs.add(thisEvent.id)

                                userDocRef.update("favs", favs)
                                    .addOnSuccessListener { res ->
                                        Toast.makeText(this, "Event added your favorites list with successfuly", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener{ error ->
                                        Toast.makeText(
                                            this,
                                            "add event process is failed!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }

                }
                .addOnFailureListener { error ->
                    Toast.makeText(this, "System error, please try again later or contact with officals", Toast.LENGTH_LONG).show()
                }
        }

        val removeFavButton = findViewById<Button>(R.id.removeAddFavButton)
        removeFavButton.setOnClickListener {
            val userDocRef = db.collection("users").document(auth.currentUser!!.uid)
            userDocRef.get().addOnSuccessListener { response ->
                if(response != null && response.exists()){
                    val favs = response.get("favs") as MutableList<String>
                    if(favs.contains(thisEvent.id)){
                        favs.remove(thisEvent.id)
                        userDocRef.update("favs", favs).addOnSuccessListener {
                            Toast.makeText(this, "remove process is successfull", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener{ error ->
                            Toast.makeText(this, "Process can't be success", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this, "You already doesn't add to favorites this event", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, "User datas doesn't exists please try again later", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "System error, please try again later or contact with officals", Toast.LENGTH_LONG).show()
            }
        }


        // show or hide buttons
        db.collection("users").document(auth.currentUser!!.uid).get().addOnSuccessListener { response ->
            if(response != null && response.exists()) {
                val favsCheck = response.get("favs") as MutableList<String>
                if(favsCheck.contains(thisEvent.id)){
                    removeFavButton.visibility = View.VISIBLE
                    addFavButton.visibility = View.GONE
                }else{
                    removeFavButton.visibility = View.GONE
                    addFavButton.visibility = View.VISIBLE
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "users data doesn't exists", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.nagiteToHomeOnEventDetails).setOnClickListener { navigateToCancel() }
        findViewById<Button>(R.id.nagiteToMenuOnEventDetails).setOnClickListener { navigateToMenu() }
    }

    override fun onStart() {
        super.onStart()
        val user = Firebase.auth.currentUser
        if(user != null) { }else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun formatEventDateTime(localDate: String, localTime: String): String {
        val parsedDate = LocalDate.parse(localDate)
        val formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        val parsedTime = LocalTime.parse(localTime)
        val formattedTime = parsedTime.format(DateTimeFormatter.ofPattern("HH:mm"))

        return "$formattedDate | $formattedTime"
    }

    private fun formattedSaleTime(datetimeString: String): String {
        val dateTime = java.time.LocalDateTime.parse(datetimeString, java.time.format.DateTimeFormatter.ISO_DATE_TIME)
        val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")
        return dateTime.format(formatter)
    }

    private fun createReminderOnCalendar(thisEvent: Event) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_CALENDAR),
                103
            )
        }
        else {
            val cr: ContentResolver = this.contentResolver
            val values = ContentValues()
            val startMillis = ZonedDateTime.parse(thisEvent.dates.start.dateTime, java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME).withZoneSameInstant(
                ZoneId.systemDefault()).toEpochSecond() * 1000// Example: 1 hour from now
            val endMillis = startMillis + 1860 * 1000 // 1-hour duration
            //

            values.put(CalendarContract.Events.DTSTART, startMillis)
            values.put(CalendarContract.Events.DTEND, endMillis)
            values.put(CalendarContract.Events.TITLE, thisEvent.name)
            values.put(CalendarContract.Events.DESCRIPTION, "Added directly to calendar")
            values.put(CalendarContract.Events.CALENDAR_ID, 2) // Replace with a valid calendar ID
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)


            // Insert the event into the calendar
            val uri = cr.insert(CalendarContract.Events.CONTENT_URI, values)
            val eventId = uri?.lastPathSegment?.toLongOrNull()

            if (eventId != null) {
                val reminderValues = ContentValues().apply {
                    put(CalendarContract.Reminders.EVENT_ID, eventId)
                    put(CalendarContract.Reminders.MINUTES, 30)
                    put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
                }

                val reminderUri = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues)
                if (reminderUri != null) {
                    Log.d("CalendarInsert", "Reminder added successfully for Event ID: $eventId")
                } else {
                    Log.e("CalendarInsert", "Failed to add reminder for Event ID: $eventId")
                }
            } else {
                Log.e("CalendarInsert", "Failed to insert event")
            }
        }
    }

    private fun navigateToMenu(){
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToCancel(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    // Find calendar id
//    private fun getCalendars() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
//            == PackageManager.PERMISSION_GRANTED) {
//            // Permission already granted
//            val resolver: ContentResolver = contentResolver
//            val uri = CalendarContract.Calendars.CONTENT_URI
//            val projection = arrayOf(
//                CalendarContract.Calendars._ID, // Calendar ID
//                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, // Calendar name
//                CalendarContract.Calendars.ACCOUNT_NAME, // Account name
//                CalendarContract.Calendars.ACCOUNT_TYPE // Account type (Google, Exchange, etc.)
//            )
//            val cursor: Cursor? = resolver.query(uri, projection, null, null, null)
//
//            cursor?.let {
//                if (it.moveToFirst()) {
//                    do {
//                        val calendarId = it.getLong(it.getColumnIndex(CalendarContract.Calendars._ID))
//                        val displayName = it.getString(it.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME))
//                        val accountName = it.getString(it.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME))
//                        val accountType = it.getString(it.getColumnIndex(CalendarContract.Calendars.ACCOUNT_TYPE))
//
//                        // Display or use the calendar data as needed
//                        Log.i("calendars asdasd","Calendar ID: $calendarId, Name: $displayName, Account: $accountName, Type: $accountType")
//                    } while (it.moveToNext())
//                } else {
//                    Toast.makeText(this, "No calendars found", Toast.LENGTH_SHORT).show()
//                }
//                it.close()
//            }
//        } else {
//            // Request permission
//            ActivityCompat.requestPermissions(this,
//                arrayOf(Manifest.permission.READ_CALENDAR), 103)
//        }
//    }
}