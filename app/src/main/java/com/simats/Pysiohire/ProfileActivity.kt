package com.simats.Pysiohire

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class ProfileActivity : AppCompatActivity() {

    private lateinit var etName: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etAge: TextInputEditText
    private lateinit var etGender: TextInputEditText
    private lateinit var etAddress: TextInputEditText
    private lateinit var btnSave: Button

    private val pickImage = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri: android.net.Uri? ->
        uri?.let {
            uploadImage(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etAge = findViewById(R.id.etAge)
        etGender = findViewById(R.id.etGender)
        etAddress = findViewById(R.id.etAddress)
        btnSave = findViewById(R.id.btnSave)

        val cvProfileImage = findViewById<androidx.cardview.widget.CardView>(R.id.cvProfileImage)
        val tvEditPhoto = findViewById<android.widget.TextView>(R.id.tvEditPhoto)

        val imagePickerAction = { 
            pickImage.launch("image/*")
        }

        cvProfileImage.setOnClickListener { imagePickerAction() }
        tvEditPhoto.setOnClickListener { imagePickerAction() }

        // Setup gender dropdown
        etGender.setOnClickListener {
            val genders = arrayOf("Male", "Female", "Other")
            AlertDialog.Builder(this)
                .setTitle("Select Gender")
                .setItems(genders) { _, which ->
                    etGender.setText(genders[which])
                }
                .show()
        }

        val session = SessionManager(this)
        val api = ApiClient.retrofit.create(ApiService::class.java)

        // Load current profile data
        loadProfileData()

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val ageStr = etAge.text.toString().trim()
            val genderStr = etGender.text.toString().trim()
            val address = etAddress.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Calculate DOB from age (approximate)
            val dob = if (ageStr.isNotEmpty()) {
                val age = ageStr.toIntOrNull() ?: 0

                val calendar = Calendar.getInstance()
                calendar.add(Calendar.YEAR, -age)

                String.format(
                    "%04d-%02d-%02d",
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            } else
                null


            // Map gender to database format
            val gender = when(genderStr.lowercase()) {
                "male" -> "male"
                "female" -> "female"
                "other" -> "other"
                else -> null
            }

            val body = mutableMapOf<String, String>(
                "name" to name
            )
            if (phone.isNotEmpty()) body["phone"] = phone
            if (address.isNotEmpty()) body["address"] = address
            if (dob != null) body["dob"] = dob
            if (gender != null) body["gender"] = gender

            api.updateProfile(session.getToken(), body)
                .enqueue(object : Callback<Map<String, Any>> {
                    override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@ProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            val errorBody = response.errorBody()?.string() ?: "Unknown error"
                            Toast.makeText(this@ProfileActivity, "Failed to update profile: $errorBody", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                        Toast.makeText(this@ProfileActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                        t.printStackTrace()
                    }
                })
        }
    }

    private fun uploadImage(uri: android.net.Uri) {
        val progressBar = android.app.ProgressDialog(this).apply {
            setMessage("Uploading image...")
            setCancelable(false)
            show()
        }

        try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = java.io.File(cacheDir, "profile_upload.jpg")
            val outputStream = java.io.FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            
            val requestFile = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/*"), file)
            val body = okhttp3.MultipartBody.Part.createFormData("image", file.name, requestFile)
            
            val session = SessionManager(this)
            val api = ApiClient.retrofit.create(ApiService::class.java)

            api.uploadProfileImage(session.getToken(), body).enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                    progressBar.dismiss()
                    if (response.isSuccessful) {
                        val ivProfile = findViewById<android.widget.ImageView>(R.id.ivProfile)
                        ivProfile.setImageURI(uri)
                        Toast.makeText(this@ProfileActivity, "Photo updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ProfileActivity, "Upload failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    progressBar.dismiss()
                    Toast.makeText(this@ProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
            
            inputStream?.close()
            outputStream.close()
        } catch (e: Exception) {
            progressBar.dismiss()
            Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun loadProfileData() {
        val session = SessionManager(this)
        val api = ApiClient.retrofit.create(ApiService::class.java)

        api.getProfile(session.getToken()).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    val data = result["data"] as? Map<*, *> ?: return
                    
                    data["name"]?.let { etName.setText(it.toString()) }
                    data["phone"]?.let { etPhone.setText(it.toString()) }
                    data["address"]?.let { etAddress.setText(it.toString()) }
                    
                    // Handle profile image if available
                    // Note: We need an image handling library like Glide or Coil usually, but for now we might skip or set if accessible
                    // For a real app, use: Glide.with(this).load(imageUrl).into(ivProfile)
                    
                    // Calculate age from DOB
                    data["dob"]?.let { dobStr ->
                        try {
                            val parts = dobStr.toString().split("-")
                            if (parts.size == 3) {
                                val dob = Calendar.getInstance().apply {
                                    set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
                                }
                                val age = Calendar.getInstance().get(Calendar.YEAR) - dob.get(Calendar.YEAR)
                                etAge.setText(age.toString())
                            }
                        } catch (e: Exception) {
                            // Ignore date parse errors
                        }
                    }
                    
                    data["gender"]?.let { gender ->
                        val genderStr = gender.toString()
                        etGender.setText(genderStr.replaceFirstChar { it.uppercaseChar() })
                    }

                    // Load profile image
                    data["profile_image"]?.let { imageUrl ->
                         val url = imageUrl.toString()
                         if (url.isNotEmpty()) {
                             val ivProfile = findViewById<android.widget.ImageView>(R.id.ivProfile)
                             DownloadImageTask(ivProfile).execute(url)
                         }
                    }

                } else {
                    // Fallback to saved preferences
                    val prefs = getSharedPreferences("pysiohire", MODE_PRIVATE)
                    val savedName = prefs.getString("USER_NAME", "")
                    val savedPhone = prefs.getString("USER_PHONE", "")
                    
                    if (!savedName.isNullOrEmpty()) {
                        etName.setText(savedName)
                    }
                    if (!savedPhone.isNullOrEmpty()) {
                        etPhone.setText(savedPhone)
                    }
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                // Fallback to saved preferences on error
                val prefs = getSharedPreferences("pysiohire", MODE_PRIVATE)
                val savedName = prefs.getString("USER_NAME", "")
                val savedPhone = prefs.getString("USER_PHONE", "")
                
                if (!savedName.isNullOrEmpty()) {
                    etName.setText(savedName)
                }
                if (!savedPhone.isNullOrEmpty()) {
                    etPhone.setText(savedPhone)
                }
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
