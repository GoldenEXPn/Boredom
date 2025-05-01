package com.example.demo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.databinding.ItemSelectAppBinding

class SelectAppAdapter(
    private var apps: List<AppInfo>,
    private val onAppSelectionChanged: (AppInfo, Boolean) -> Unit
) : RecyclerView.Adapter<SelectAppAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemSelectAppBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSelectAppBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]

        with(holder.binding) {
            ivAppIcon.setImageDrawable(app.icon)
            tvAppName.text = app.name
            checkbox.isChecked = app.isBlocked

            // Handle clicks on the item
            root.setOnClickListener {
                checkbox.isChecked = !checkbox.isChecked
                app.isBlocked = checkbox.isChecked
                onAppSelectionChanged(app, checkbox.isChecked)
            }

            // Handle clicks on the checkbox
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (app.isBlocked != isChecked) {
                    app.isBlocked = isChecked
                    onAppSelectionChanged(app, isChecked)
                }
            }
        }
    }

    override fun getItemCount(): Int = apps.size

    fun updateList(newList: List<AppInfo>) {
        apps = newList
        notifyDataSetChanged()
    }
}