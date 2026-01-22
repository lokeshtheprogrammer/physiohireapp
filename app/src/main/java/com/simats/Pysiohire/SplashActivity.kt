package com.simats.Pysiohire

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val session = SessionManager(this)

        Handler(Looper.getMainLooper()).postDelayed({
            if (session.getRole() == null) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                if (session.getRole() == "physio")
                    startActivity(Intent(this, PhysioHomeActivity::class.java))
                else
                    startActivity(Intent(this, PatientHomeActivity::class.java))
            }
            finish()
        }, 2000)
    }
}
