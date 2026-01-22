package com.simats.Pysiohire

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var tvSignup: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvSignup = findViewById(R.id.tvSignup)

        val api = ApiClient.retrofit.create(ApiService::class.java)
        val session = SessionManager(this)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val body = mapOf(
                "email" to email,
                "password" to password
            )

            api.login(body).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, res: Response<LoginResponse>) {
                    if (res.isSuccessful && res.body() != null) {
                        val data = res.body()!!
                        session.saveToken(data.token, data.user.role)
                        session.saveUserInfo(data.user.name, data.user.phone)

                        if (data.user.role == "physio") {
                            startActivity(Intent(this@LoginActivity, PhysioHomeActivity::class.java))
                        } else {
                            startActivity(Intent(this@LoginActivity, PatientHomeActivity::class.java))
                        }
                        finish()
                    } else {
                        val errorBody = res.errorBody()?.string() ?: "Unknown error"
                        Toast.makeText(this@LoginActivity, "Login failed: $errorBody", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    t.printStackTrace()
                }
            })
        }

        tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}
