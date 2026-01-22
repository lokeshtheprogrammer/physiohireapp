package com.simats.Pysiohire

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddPhysioProfileActivity : AppCompatActivity() {

    private lateinit var etSpecialty: EditText
    private lateinit var etBio: EditText
    private lateinit var etQual: EditText
    private lateinit var etYears: EditText
    private lateinit var etRate: EditText
    private lateinit var etLocation: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_physio)

        etSpecialty = findViewById(R.id.etSpecialty)
        etBio = findViewById(R.id.etBio)
        etQual = findViewById(R.id.etQual)
        etYears = findViewById(R.id.etYears)
        etRate = findViewById(R.id.etRate)
        etLocation = findViewById(R.id.etLocation)
        btnSave = findViewById(R.id.btnSaveProfile)

        val session = SessionManager(this)
        val api = ApiClient.retrofit.create(ApiService::class.java)

        btnSave.setOnClickListener {
            val specialty = etSpecialty.text.toString().trim()
            val bio = etBio.text.toString().trim()
            val qualifications = etQual.text.toString().trim()
            val yearsExperience = etYears.text.toString().trim()
            val rate = etRate.text.toString().trim()
            val location = etLocation.text.toString().trim()

            if (specialty.isEmpty() || bio.isEmpty() || qualifications.isEmpty() || 
                yearsExperience.isEmpty() || rate.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val body = mapOf(
                "specialty" to specialty,
                "bio" to bio,
                "qualifications" to qualifications,
                "years_experience" to yearsExperience.toInt(),
                "rate_per_session" to rate.toInt(),
                "location" to location
            )

            api.addPhysio(session.getToken(), body).enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddPhysioProfileActivity, "Profile saved successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddPhysioProfileActivity, "Failed to save profile", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    Toast.makeText(this@AddPhysioProfileActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
