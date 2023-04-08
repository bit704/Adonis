package com.example.adonis.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adonis.R
import com.example.adonis.utils.AddAdapter

class AddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_results)
        val layout = LinearLayoutManager(recyclerView.context)
        layout.orientation = LinearLayoutManager.VERTICAL
        val adapter = AddAdapter()
        adapter.setAddButtonClickListener(object : AddAdapter.OnAddButtonClickListener{
            override fun onAddButtonClick() {
                val intent = Intent(this@AddActivity, ConfirmActivity::class.java)
                startActivity(intent)
            }
        })

        recyclerView.layoutManager = layout
        recyclerView.adapter = adapter
    }
}