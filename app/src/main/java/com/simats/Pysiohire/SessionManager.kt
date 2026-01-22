package com.simats.Pysiohire

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("pysiohire", Context.MODE_PRIVATE)

    fun saveToken(token: String, role: String) {
        prefs.edit()
            .putString("TOKEN", token)
            .putString("ROLE", role)
            .apply()
    }

    fun saveUserInfo(name: String, phone: String?) {
        prefs.edit()
            .putString("USER_NAME", name)
            .putString("USER_PHONE", phone ?: "")
            .apply()
    }

    fun getToken(): String =
        "Bearer ${prefs.getString("TOKEN", "")}"

    fun getRole(): String? = prefs.getString("ROLE", null)

    fun logout() = prefs.edit().clear().apply()
}
