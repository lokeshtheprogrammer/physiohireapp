package com.simats.Pysiohire

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PhysioAdapter(
    private val list: List<Physio>,
    private val onClick: (Physio) -> Unit
) : RecyclerView.Adapter<PhysioAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.tvName)
        val specialty: TextView = v.findViewById(R.id.tvSpecialty)
        val location: TextView = v.findViewById(R.id.tvLocation)
        val rate: TextView = v.findViewById(R.id.tvRate)
        val btnBook: android.widget.Button = v.findViewById(R.id.btnBook)

        init {
            v.setOnClickListener { onClick(list[adapterPosition]) }
            btnBook.setOnClickListener { onClick(list[adapterPosition]) }
        }
    }

    override fun onCreateViewHolder(p: ViewGroup, v: Int) =
        VH(LayoutInflater.from(p.context).inflate(R.layout.item_physio, p, false))

    override fun onBindViewHolder(h: VH, i: Int) {
        val p = list[i]
        h.name.text = p.name
        h.specialty.text = p.specialty ?: "General Physiotherapist"
        h.location.text = p.location ?: "Unknown Location"
        h.rate.text = if (p.rate_per_session != null) "$${p.rate_per_session}/session" else "Contact for price"
    }

    override fun getItemCount() = list.size
}
