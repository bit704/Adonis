package com.example.adonis.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.adonis.R
import java.util.zip.Inflater

class AddAdapter: Adapter<AddAdapter.AddViewHolder>() {
    var listener: OnAddButtonClickListener? = null

    inner class AddViewHolder(itemView: View) : ViewHolder(itemView) {
        val avatar = itemView.findViewById<ImageView>(R.id.add_avatar)
        val nickname = itemView.findViewById<TextView>(R.id.add_nickname)
        val user = itemView.findViewById<TextView>(R.id.add_id)
        val addButton = itemView.findViewById<Button>(R.id.button_add)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_add, null)
        return AddViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: AddViewHolder, position: Int) {
        holder.addButton.setOnClickListener {
            if (listener != null) {
                listener?.onAddButtonClick()
            }
        }
    }

    interface OnAddButtonClickListener {
        fun onAddButtonClick()
    }

    fun setAddButtonClickListener(listener: OnAddButtonClickListener) {
        this.listener = listener
    }
}