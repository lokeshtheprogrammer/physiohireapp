package com.simats.Pysiohire

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("login.php")
    fun login(@Body body: Map<String, String>): Call<LoginResponse>

    @POST("signup.php")
    fun signup(@Body body: Map<String, String>): Call<Map<String, @JvmSuppressWildcards Any>>

    @POST("add_physio.php")
    fun addPhysio(
        @Header("Authorization") token: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Call<Map<String, @JvmSuppressWildcards Any>>

    @POST("set_availability.php")
    fun setAvailability(
        @Header("Authorization") token: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Call<Map<String, @JvmSuppressWildcards Any>>

    @GET("list_physios.php")
    fun listPhysios(): Call<List<Physio>>

    @POST("book_appointment.php")
    fun bookAppointment(
        @Header("Authorization") token: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Call<Map<String, @JvmSuppressWildcards Any>>

    @POST("update_profile.php")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Call<Map<String, @JvmSuppressWildcards Any>>

    @GET("list_appointments.php")
    fun listAppointments(
        @Header("Authorization") token: String
    ): Call<List<Appointment>>

    @GET("get_profile.php")
    fun getProfile(
        @Header("Authorization") token: String
    ): Call<Map<String, @JvmSuppressWildcards Any>>

    @retrofit2.http.Multipart
    @POST("update_profile.php")
    fun uploadProfileImage(
        @Header("Authorization") token: String,
        @retrofit2.http.Part image: okhttp3.MultipartBody.Part
    ): Call<Map<String, @JvmSuppressWildcards Any>>
}
