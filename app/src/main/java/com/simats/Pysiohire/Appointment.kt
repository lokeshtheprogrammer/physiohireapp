package com.simats.Pysiohire

data class Appointment(
    val id: Int,
    val date: String,
    val start_time: String,
    val end_time: String,
    val status: String,
    val appointment_type: String? = null,
    val physio_name: String? = null,
    val patient_name: String? = null
)
