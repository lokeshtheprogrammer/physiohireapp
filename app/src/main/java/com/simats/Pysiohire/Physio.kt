package com.simats.Pysiohire

data class Physio(
    val user_id: Int,
    val name: String,
    val specialty: String?,
    val location: String?,
    val rate_per_session: Int?
)

