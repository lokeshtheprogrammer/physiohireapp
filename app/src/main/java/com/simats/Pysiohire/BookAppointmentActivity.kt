package com.simats.Pysiohire

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class BookAppointmentActivity : AppCompatActivity() {

    private lateinit var etDate: TextInputEditText
    private lateinit var etStart: TextInputEditText
    private lateinit var etEnd: TextInputEditText
    private lateinit var etNotes: TextInputEditText
    private lateinit var rbClinic: RadioButton
    private lateinit var rbHomeVisit: RadioButton
    private lateinit var rbVirtual: RadioButton
    private lateinit var btnBook: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_appointment)

        etDate = findViewById(R.id.etDate)
        etStart = findViewById(R.id.etStart)
        etEnd = findViewById(R.id.etEnd)
        etNotes = findViewById(R.id.etNotes)
        rbClinic = findViewById(R.id.rbClinic)
        rbHomeVisit = findViewById(R.id.rbHomeVisit)
        rbVirtual = findViewById(R.id.rbVirtual)
        btnBook = findViewById(R.id.btnBook)

        val physioId = intent.getIntExtra("PHYSIO_ID", 0)
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

        btnBook.setOnClickListener {
            val date = etDate.text.toString().trim()
            val startTime = etStart.text.toString().trim()
            val endTime = etEnd.text.toString().trim()
            val notes = etNotes.text.toString().trim()

            // Get appointment type
            val appointmentType = when {
                rbClinic.isChecked -> "clinic"
                rbHomeVisit.isChecked -> "home_visit"
                rbVirtual.isChecked -> "teleconsult"
                else -> "clinic"
            }

            if (date.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val body = mapOf(
                "physio_id" to physioId,
                "date" to date,
                "start_time" to startTime,
                "end_time" to endTime,
                "appointment_type" to appointmentType,
                "notes" to notes
            )

            api.bookAppointment(session.getToken(), body).enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@BookAppointmentActivity, "Appointment booked successfully!", Toast.LENGTH_LONG).show()
                        // Set result to indicate success
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        // Try to get error message from response
                        val errorBody = response.errorBody()?.string()
                        val errorMsg = try {
                            // Try to parse JSON error
                            val json = org.json.JSONObject(errorBody ?: "{}")
                            json.optString("message", "Failed to book appointment")
                        } catch (e: Exception) {
                            "Failed to book appointment: ${response.code()}"
                        }
                        Toast.makeText(this@BookAppointmentActivity, errorMsg, Toast.LENGTH_LONG).show()
                        android.util.Log.e("BookAppointment", "Error: $errorBody")
                    }
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    val errorMsg = "Network error: ${t.localizedMessage}"
                    Toast.makeText(this@BookAppointmentActivity, errorMsg, Toast.LENGTH_LONG).show()
                    android.util.Log.e("BookAppointment", "Failure: ${t.message}", t)
                }
            })
        }
    }
}
