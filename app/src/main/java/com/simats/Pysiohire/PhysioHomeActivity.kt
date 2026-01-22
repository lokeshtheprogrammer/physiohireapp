package com.simats.Pysiohire

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class PhysioHomeActivity : AppCompatActivity() {

    private lateinit var btnProfile: Button
    private lateinit var btnAvailability: Button
    private lateinit var btnAppointments: Button
    private lateinit var btnLogout: Button

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_physio_home)

        sessionManager = SessionManager(this)

        btnProfile = findViewById(R.id.btnProfile)
        btnAvailability = findViewById(R.id.btnAvailability)
        btnAppointments = findViewById(R.id.btnAppointments)
        btnLogout = findViewById(R.id.btnLogout)

        btnProfile.setOnClickListener {
            startActivity(Intent(this, AddPhysioProfileActivity::class.java))
        }

        btnAvailability.setOnClickListener {
            startActivity(Intent(this, SetAvailabilityActivity::class.java))
        }

        btnAppointments.setOnClickListener {
            startActivity(Intent(this, AppointmentsActivity::class.java))
        }

        btnLogout.setOnClickListener {
            sessionManager.logout()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
