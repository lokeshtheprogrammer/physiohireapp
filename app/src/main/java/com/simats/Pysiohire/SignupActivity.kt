package com.simats.Pysiohire

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var rbPhysio: RadioButton
    private lateinit var rbPatient: RadioButton
    private lateinit var cvPatient: androidx.cardview.widget.CardView
    private lateinit var cvPhysio: androidx.cardview.widget.CardView
    private lateinit var btnSignup: Button
    private lateinit var tvLogin: TextView
    private var selectedRole = "patient"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        rbPhysio = findViewById(R.id.rbPhysio)
        rbPatient = findViewById(R.id.rbPatient)
        cvPatient = findViewById(R.id.cvPatient)
        cvPhysio = findViewById(R.id.cvPhysio)
        btnSignup = findViewById(R.id.btnSignup)
        tvLogin = findViewById(R.id.tvLogin)

        // Setup role selection cards
        cvPatient.setOnClickListener {
            selectedRole = "patient"
            rbPatient.isChecked = true
            rbPhysio.isChecked = false
            updateRoleSelection()
        }

        cvPhysio.setOnClickListener {
            selectedRole = "physio"
            rbPhysio.isChecked = true
            rbPatient.isChecked = false
            updateRoleSelection()
        }

        updateRoleSelection()

        btnSignup.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()

            val role = selectedRole

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val body = mapOf(
                "name" to name,
                "email" to email,
                "phone" to phone,
                "password" to password,
                "role" to role
            )

            val api = ApiClient.retrofit.create(ApiService::class.java)
            api.signup(body).enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(
                    call: Call<Map<String, Any>>,
                    response: Response<Map<String, Any>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()!!
                        val message = result["message"]?.toString() ?: "Account created successfully"
                        Toast.makeText(
                            this@SignupActivity,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        Toast.makeText(
                            this@SignupActivity,
                            "Signup failed: $errorBody",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    Toast.makeText(
                        this@SignupActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    t.printStackTrace()
                }
            })
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun updateRoleSelection() {
        val selectedColor = getColor(R.color.blue_100)      // light blue
        val unselectedColor = getColor(R.color.white)

        if (selectedRole == "patient") {
            cvPatient.setCardBackgroundColor(selectedColor)
            cvPhysio.setCardBackgroundColor(unselectedColor)
        } else {
            cvPhysio.setCardBackgroundColor(selectedColor)
            cvPatient.setCardBackgroundColor(unselectedColor)
        }
    } }
