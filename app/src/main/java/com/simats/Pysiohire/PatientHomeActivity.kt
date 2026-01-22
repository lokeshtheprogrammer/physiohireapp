package com.simats.Pysiohire

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientHomeActivity : AppCompatActivity() {

    private lateinit var btnPhysios: MaterialCardView
    private lateinit var btnAppointments: MaterialCardView
    private lateinit var btnProfileHome: MaterialCardView
    private lateinit var btnSettings: MaterialCardView
    private lateinit var ivProfileAvatar: android.widget.ImageView
    private lateinit var tvViewAll: TextView
    private lateinit var rvRecentBookings: RecyclerView
    private lateinit var llEmptyBookings: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_home)

        btnPhysios = findViewById(R.id.btnPhysios)
        btnAppointments = findViewById(R.id.btnAppointments)
        btnProfileHome = findViewById(R.id.btnProfileHome)
        btnSettings = findViewById(R.id.btnSettings)
        ivProfileAvatar = findViewById(R.id.ivProfileAvatar)
        tvViewAll = findViewById(R.id.tvViewAll)
        rvRecentBookings = findViewById(R.id.rvRecentBookings)
        llEmptyBookings = findViewById(R.id.llEmptyBookings)

        // Setup RecyclerView
        rvRecentBookings.layoutManager = LinearLayoutManager(this)

        // Load recent bookings
        loadRecentBookings()

        ivProfileAvatar.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        btnPhysios.setOnClickListener {
            startActivity(Intent(this, PhysioListActivity::class.java))
        }

        btnAppointments.setOnClickListener {
            startActivity(Intent(this, AppointmentsActivity::class.java))
        }

        btnProfileHome.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        tvViewAll.setOnClickListener {
            startActivity(Intent(this, AppointmentsActivity::class.java))
        }

        // Search logic
        val etSearchHome = findViewById<android.widget.EditText>(R.id.etSearchHome)
        etSearchHome.setOnEditorActionListener { v, actionId, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val query = v.text.toString().trim()
                if (query.isNotEmpty()) {
                    val intent = Intent(this, PhysioListActivity::class.java)
                    intent.putExtra("SEARCH_QUERY", query)
                    startActivity(intent)
                }
                true
            } else {
                false
            }
        }
    }

    private fun loadRecentBookings() {
        val session = SessionManager(this)
        val api = ApiClient.retrofit.create(ApiService::class.java)

        api.listAppointments(session.getToken()).enqueue(object : Callback<List<Appointment>> {
            override fun onResponse(call: Call<List<Appointment>>, response: Response<List<Appointment>>) {
                if (response.isSuccessful && response.body() != null) {
                    val appointments = response.body()!!
                    if (appointments.isNotEmpty()) {
                        // Show only first 3 recent bookings
                        val recentAppointments = appointments.take(3)
                        rvRecentBookings.visibility = View.VISIBLE
                        llEmptyBookings.visibility = View.GONE
                        rvRecentBookings.adapter = AppointmentAdapter(recentAppointments)
                    } else {
                        rvRecentBookings.visibility = View.GONE
                        llEmptyBookings.visibility = View.VISIBLE
                    }
                } else {
                    rvRecentBookings.visibility = View.GONE
                    llEmptyBookings.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<List<Appointment>>, t: Throwable) {
                rvRecentBookings.visibility = View.GONE
                llEmptyBookings.visibility = View.VISIBLE
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Reload bookings and profile when returning to this screen
        loadRecentBookings()
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val session = SessionManager(this)
        val api = ApiClient.retrofit.create(ApiService::class.java)

        api.getProfile(session.getToken()).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    val data = result["data"] as? Map<*, *>
                    
                    data?.get("profile_image")?.let { imageUrl ->
                         val url = imageUrl.toString()
                         if (url.isNotEmpty()) {
                             DownloadImageTask(ivProfileAvatar).execute(url)
                         }
                    }
                }
            }
            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                // Ignore profile load errors here
            }
        })
    }

    private class DownloadImageTask(val bmImage: android.widget.ImageView) : android.os.AsyncTask<String, Void, android.graphics.Bitmap?>() {
        override fun doInBackground(vararg urls: String): android.graphics.Bitmap? {
            val urldisplay = urls[0]
            var mIcon11: android.graphics.Bitmap? = null
            try {
                val `in` = java.net.URL(urldisplay).openStream()
                mIcon11 = android.graphics.BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return mIcon11
        }

        override fun onPostExecute(result: android.graphics.Bitmap?) {
            if (result != null) {
                bmImage.setImageBitmap(result)
            }
        }
    }
}
