package com.simats.Pysiohire

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class SetAvailabilityActivity : AppCompatActivity() {

    private lateinit var etDate: TextInputEditText
    private lateinit var etStart: TextInputEditText
    private lateinit var etEnd: TextInputEditText
    private lateinit var etSlots: TextInputEditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_availability)

        etDate = findViewById(R.id.etDate)
        etStart = findViewById(R.id.etStart)
        etEnd = findViewById(R.id.etEnd)
        etSlots = findViewById(R.id.etSlots)
        btnSave = findViewById(R.id.btnSave)

        val session = SessionManager(this)
        val api = ApiClient.retrofit.create(ApiService::class.java)

        // Date picker
        etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                etDate.setText(String.format("%04d-%02d-%02d", year, month + 1, day))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Start time picker
        etStart.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(this, { _, hour, minute ->
                etStart.setText(String.format("%02d:%02d:00", hour, minute))
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
        }

        // End time picker
        etEnd.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(this, { _, hour, minute ->
                etEnd.setText(String.format("%02d:%02d:00", hour, minute))
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
        }

        btnSave.setOnClickListener {
            val date = etDate.text.toString().trim()
            val startTime = etStart.text.toString().trim()
            val endTime = etEnd.text.toString().trim()
            val maxSlots = etSlots.text.toString().trim()

            if (date.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || maxSlots.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val slotsInt = try {
                maxSlots.toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Please enter a valid number for slots", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (slotsInt <= 0) {
                Toast.makeText(this, "Slots must be greater than 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val body: Map<String, Any> = mapOf(
                "date" to date,
                "start_time" to startTime,
                "end_time" to endTime,
                "max_slots" to slotsInt
            )

            api.setAvailability(session.getToken(), body).enqueue(object : Callback<Map<String, @JvmSuppressWildcards Any>> {
                override fun onResponse(call: Call<Map<String, @JvmSuppressWildcards Any>>, response: Response<Map<String, @JvmSuppressWildcards Any>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@SetAvailabilityActivity, "Availability saved successfully!", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMsg = try {
                            val json = org.json.JSONObject(errorBody ?: "{}")
                            json.optString("message", "Failed to save availability")
                        } catch (e: Exception) {
                            "Failed to save availability: ${response.code()}"
                        }
                        Toast.makeText(this@SetAvailabilityActivity, errorMsg, Toast.LENGTH_LONG).show()
                        android.util.Log.e("SetAvailability", "Error: $errorBody")
                    }
                }

                override fun onFailure(call: Call<Map<String, @JvmSuppressWildcards Any>>, t: Throwable) {
                    val errorMsg = "Network error: ${t.localizedMessage}"
                    Toast.makeText(this@SetAvailabilityActivity, errorMsg, Toast.LENGTH_LONG).show()
                    android.util.Log.e("SetAvailability", "Failure: ${t.message}", t)
                }
            })
        }
    }
}

