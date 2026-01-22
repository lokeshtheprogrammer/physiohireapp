package com.simats.Pysiohire

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PhysioListActivity : AppCompatActivity() {

    private lateinit var rvPhysios: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_physio_list)

        rvPhysios = findViewById(R.id.rvPhysios)
        val progressBar = findViewById<android.widget.ProgressBar>(R.id.progressBar)

        val api = ApiClient.retrofit.create(ApiService::class.java)
        rvPhysios.layoutManager = LinearLayoutManager(this)

        val etSearch = findViewById<android.widget.EditText>(R.id.etSearch)
        val initialQuery = intent.getStringExtra("SEARCH_QUERY") ?: ""
        etSearch.setText(initialQuery)

        var allPhysios: List<Physio> = emptyList()

        progressBar.visibility = android.view.View.VISIBLE
        api.listPhysios().enqueue(object : Callback<List<Physio>> {
            override fun onResponse(call: Call<List<Physio>>, res: Response<List<Physio>>) {
                progressBar.visibility = android.view.View.GONE
                if (res.isSuccessful && res.body() != null) {
                    allPhysios = res.body()!!
                    
                    filterAndDisplay(allPhysios, etSearch.text.toString(), rvPhysios)

                    etSearch.addTextChangedListener(object : android.text.TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            filterAndDisplay(allPhysios, s.toString(), rvPhysios)
                        }
                        override fun afterTextChanged(s: android.text.Editable?) {}
                    })
                    
                } else {
                    Toast.makeText(this@PhysioListActivity, "Failed to load physios", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Physio>>, t: Throwable) {
                progressBar.visibility = android.view.View.GONE
                Toast.makeText(this@PhysioListActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterAndDisplay(list: List<Physio>, query: String, recyclerView: RecyclerView) {
        val filtered = if (query.isEmpty()) {
            list
        } else {
            list.filter { 
                it.name?.contains(query, ignoreCase = true) == true || 
                it.specialty?.contains(query, ignoreCase = true) == true 
            }
        }
        
        recyclerView.adapter = PhysioAdapter(filtered) {
            val i = Intent(this@PhysioListActivity, BookAppointmentActivity::class.java)
            i.putExtra("PHYSIO_ID", it.user_id)
            startActivity(i)
        }

        if (filtered.isEmpty() && list.isNotEmpty()) {
            Toast.makeText(this, "No matches found", Toast.LENGTH_SHORT).show()
        }
    }
}
