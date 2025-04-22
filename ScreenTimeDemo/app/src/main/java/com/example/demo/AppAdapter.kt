package com.example.demo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.databinding.ItemAppBinding

class AppAdapter(
    private val apps: List<AppInfo>,
    private val onAppSelectionChanged: (AppInfo, Boolean) -> Unit
) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    class AppViewHolder(private val binding: ItemAppBinding) : RecyclerView.ViewHolder(binding.root) {
        val appIcon: ImageView = binding.ivAppIcon
        val appName: TextView = binding.tvAppName
        val checkbox: CheckBox = binding.checkboxApp
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]
        holder.appIcon.setImageDrawable(app.icon)
        holder.appName.text = app.name
        holder.checkbox.isChecked = app.isBlocked

        // Set click listener for the entire item
        holder.itemView.setOnClickListener {
            holder.checkbox.isChecked = !holder.checkbox.isChecked
            app.isBlocked = holder.checkbox.isChecked
            onAppSelectionChanged(app, app.isBlocked)
        }

        // Set checkbox listener
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            app.isBlocked = isChecked
            onAppSelectionChanged(app, isChecked)
        }
    }

    override fun getItemCount(): Int = apps.size
}