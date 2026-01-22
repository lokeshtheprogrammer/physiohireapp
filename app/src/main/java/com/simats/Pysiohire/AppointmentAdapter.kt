package com.simats.Pysiohire

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppointmentAdapter(
    private val list: List<Appointment>
) : RecyclerView.Adapter<AppointmentAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val doctorName: TextView = v.findViewById(R.id.tvDoctorName)
        val dateTime: TextView = v.findViewById(R.id.tvDateTime)
        val status: com.google.android.material.chip.Chip = v.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(p: ViewGroup, v: Int): VH {
        return VH(
            LayoutInflater.from(p.context)
            .inflate(R.layout.item_appointment, p, false))
    }

    override fun onBindViewHolder(h: VH, i: Int) {
        val a = list[i]
        // Show Physio Name if available, else show ID
        h.doctorName.text = if (!a.physio_name.isNullOrEmpty()) "Dr. ${a.physio_name}" else "Appointment #${a.id}"
        
        h.dateTime.text = "${a.date} • ${a.start_time}"
        h.status.text = a.status.capitalize()
        
        // Color coding for status
        if (a.status == "confirmed") {
             h.status.setChipBackgroundColorResource(android.R.color.holo_green_light)
        } else if (a.status == "pending") {
             h.status.setChipBackgroundColorResource(android.R.color.holo_orange_light)
        }
    }

    override fun getItemCount() = list.size
}
