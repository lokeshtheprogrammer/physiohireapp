package com.simats.Pysiohire

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppointmentsActivity : AppCompatActivity() {

    private lateinit var rvAppointments: RecyclerView
    private lateinit var emptyStateLayout: android.widget.LinearLayout
    private lateinit var progressBar: android.widget.ProgressBar
    private lateinit var fabAddAppointment: com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments)

        rvAppointments = findViewById(R.id.rvAppointments)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        progressBar = findViewById(R.id.progressBar)
        fabAddAppointment = findViewById(R.id.fabAddAppointment)

        val session = SessionManager(this)
        val api = ApiClient.retrofit.create(ApiService::class.java)

        rvAppointments.layoutManager = LinearLayoutManager(this)

        fabAddAppointment.setOnClickListener {
            startActivity(android.content.Intent(this, PhysioListActivity::class.java))
        }

        progressBar.visibility = android.view.View.VISIBLE
        api.listAppointments(session.getToken()).enqueue(object : Callback<List<Appointment>> {
            override fun onResponse(call: Call<List<Appointment>>, res: Response<List<Appointment>>) {
                progressBar.visibility = android.view.View.GONE
                if (res.isSuccessful && res.body() != null) {
                    val list = res.body()!!
                    if (list.isNotEmpty()) {
                        rvAppointments.visibility = android.view.View.VISIBLE
                        emptyStateLayout.visibility = android.view.View.GONE
                        rvAppointments.adapter = AppointmentAdapter(list)
                    } else {
                        rvAppointments.visibility = android.view.View.GONE
                        emptyStateLayout.visibility = android.view.View.VISIBLE
                    }
                } else {
                    Toast.makeText(this@AppointmentsActivity, "Failed to load appointments", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Appointment>>, t: Throwable) {
                progressBar.visibility = android.view.View.GONE
                Toast.makeText(this@AppointmentsActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
