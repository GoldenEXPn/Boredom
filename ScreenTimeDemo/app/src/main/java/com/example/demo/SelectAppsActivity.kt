package com.example.demo

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demo.databinding.ActivitySelectAppsBinding
import java.util.*

class SelectAppsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectAppsBinding
    private val allApps = mutableListOf<AppInfo>()
    private val filteredApps = mutableListOf<AppInfo>()
    private val selectedApps = mutableListOf<AppInfo>()
    private lateinit var adapter: SelectAppAdapter
    private var categoryName = ""
    private lateinit var category: AppCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectAppsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the category from intent
        categoryName = intent.getStringExtra("category") ?: "ENTERTAINMENT"
        category = try {
            AppCategory.valueOf(categoryName)
        } catch (e: Exception) {
            AppCategory.ENTERTAINMENT
        }

        // Get already selected apps
        val alreadySelectedPackages = intent.getStringArrayExtra("already_selected") ?: arrayOf()
        Log.d("SelectApps", "Already selected packages: ${alreadySelectedPackages.joinToString()}")

        // Set the title
        binding.tvTitle.text = "Select ${categoryName.lowercase().capitalize(Locale.ROOT)} Apps"

        // Setup done button
        binding.btnDone.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("category", categoryName)
            resultIntent.putExtra("selected_packages", selectedApps.map { it.packageName }.toTypedArray())
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        // Setup RecyclerView
        setupRecyclerView()

        // Load apps
        loadInstalledApps(alreadySelectedPackages)

        // Setup search
        setupSearch()
    }

    private fun setupRecyclerView() {
        binding.rvApps.layoutManager = LinearLayoutManager(this)
        adapter = SelectAppAdapter(filteredApps) { app, isSelected ->
            if (isSelected) {
                if (!selectedApps.contains(app)) {
                    selectedApps.add(app)
                }
            } else {
                selectedApps.removeAll { it.packageName == app.packageName }
            }

            // Update the button state
            updateButtonState()
        }
        binding.rvApps.adapter = adapter
    }

    private fun loadInstalledApps(alreadySelectedPackages: Array<String>) {
        binding.progressBar.visibility = View.VISIBLE

        allApps.clear()
        selectedApps.clear()

        // Get all launcher apps
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfoList = packageManager.queryIntentActivities(intent, 0)

        for (app in resolveInfoList) {
            val packageName = app.activityInfo.packageName

            try {
                val appInfo = packageManager.getApplicationInfo(packageName, 0)

                // Skip system apps
                if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                    continue
                }

                val appName = packageManager.getApplicationLabel(appInfo).toString()
                val appIcon = packageManager.getApplicationIcon(appInfo)

                // Create app info object
                val appInfoObj = AppInfo(packageName, appName, appIcon, categoryName)

                // Check if this app is already selected
                if (alreadySelectedPackages.contains(packageName)) {
                    appInfoObj.isBlocked = true
                    selectedApps.add(appInfoObj)
                }

                // Add to all apps list
                allApps.add(appInfoObj)
            } catch (e: Exception) {
                Log.e("SelectApps", "Error loading app $packageName: ${e.message}")
                continue
            }
        }

        // Sort alphabetically
        allApps.sortBy { it.name }

        // Update filtered list
        updateFilteredList()

        // Update button state
        updateButtonState()

        binding.progressBar.visibility = View.GONE
    }

    private fun updateFilteredList() {
        filteredApps.clear()
        filteredApps.addAll(allApps)
        adapter.notifyDataSetChanged()
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterApps(newText)
                return true
            }
        })
    }

    private fun filterApps(query: String?) {
        filteredApps.clear()

        if (query.isNullOrEmpty()) {
            filteredApps.addAll(allApps)
        } else {
            val lowerQuery = query.lowercase(Locale.getDefault())
            filteredApps.addAll(allApps.filter {
                it.name.lowercase(Locale.getDefault()).contains(lowerQuery)
            })
        }

        adapter.notifyDataSetChanged()
    }

    private fun updateButtonState() {
        binding.btnDone.isEnabled = selectedApps.isNotEmpty()
    }
}