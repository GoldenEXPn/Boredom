package com.example.demo

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demo.databinding.ActivityAppBlockBinding

class AppBlockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppBlockBinding

    // Lists to hold app information
    private val entertainmentApps = mutableListOf<AppInfo>()
    private val socialApps = mutableListOf<AppInfo>()

    // Adapters for RecyclerViews
    private lateinit var entertainmentAdapter: AppAdapter
    private lateinit var socialAdapter: AppAdapter

    // Tracks expansion state
    private var isEntertainmentExpanded = false
    private var isSocialExpanded = false

    // Activity result launchers for app selection
    private val selectEntertainmentAppsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedPackages = result.data?.getStringArrayExtra("selected_packages")
            if (selectedPackages != null && selectedPackages.isNotEmpty()) {
                // Add the selected packages to entertainment apps
                addSelectedApps(selectedPackages, AppCategory.ENTERTAINMENT)
            }
        }
    }

    private val selectSocialAppsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedPackages = result.data?.getStringArrayExtra("selected_packages")
            if (selectedPackages != null && selectedPackages.isNotEmpty()) {
                // Add the selected packages to social apps
                addSelectedApps(selectedPackages, AppCategory.SOCIAL)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppBlockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up back button
        binding.ivBack.setOnClickListener {
            finish()
        }

        // Set up RecyclerViews
        setupRecyclerViews()

        // Set up category headers for expansion
        setupCategoryHeaders()

        // Load installed apps
        loadInstalledApps()
    }

    private fun setupRecyclerViews() {
        // Entertainment RecyclerView
        binding.rvEntertainmentApps.layoutManager = LinearLayoutManager(this)
        entertainmentAdapter = AppAdapter(entertainmentApps) { app, isBlocked ->
            // Handle app selection change
            saveAppBlockingState(app, isBlocked)
        }
        binding.rvEntertainmentApps.adapter = entertainmentAdapter

        // Social RecyclerView
        binding.rvSocialApps.layoutManager = LinearLayoutManager(this)
        socialAdapter = AppAdapter(socialApps) { app, isBlocked ->
            // Handle app selection change
            saveAppBlockingState(app, isBlocked)
        }
        binding.rvSocialApps.adapter = socialAdapter
    }

    private fun setupCategoryHeaders() {
        // Set up entertainment expansion
        binding.llEntertainmentHeader.setOnClickListener {
            toggleEntertainmentSection()
        }
        binding.ivEntertainmentExpand.setOnClickListener {
            toggleEntertainmentSection()
        }

        // Set up social expansion
        binding.llSocialHeader.setOnClickListener {
            toggleSocialSection()
        }
        binding.ivSocialExpand.setOnClickListener {
            toggleSocialSection()
        }

        // Set up "Add More Apps" buttons
        binding.btnAddEntertainmentApps.setOnClickListener {
            openAppSelector(AppCategory.ENTERTAINMENT)
        }

        binding.btnAddSocialApps.setOnClickListener {
            openAppSelector(AppCategory.SOCIAL)
        }
    }

    private fun loadInstalledApps() {
        // Clear existing lists
        entertainmentApps.clear()
        socialApps.clear()

        // Get all apps with launcher intent
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfoList = packageManager.queryIntentActivities(intent, 0)

        Log.d("AppBlock", "Found ${resolveInfoList.size} installed apps")

        // Process each app and categorize using our database
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

                // Check the app's category using our database
                when {
                    AppCategoryDatabase.isInCategory(packageName, AppCategory.ENTERTAINMENT) -> {
                        entertainmentApps.add(AppInfo(packageName, appName, appIcon, "ENTERTAINMENT"))
                        Log.d("AppBlock", "Added $appName to entertainment apps")
                    }
                    AppCategoryDatabase.isInCategory(packageName, AppCategory.SOCIAL) -> {
                        socialApps.add(AppInfo(packageName, appName, appIcon, "SOCIAL"))
                        Log.d("AppBlock", "Added $appName to social apps")
                    }
                    AppCategoryDatabase.isInCategory(packageName, AppCategory.GAME) -> {
                        // Categorize games as entertainment for blocking purposes
                        entertainmentApps.add(AppInfo(packageName, appName, appIcon, "ENTERTAINMENT"))
                        Log.d("AppBlock", "Added game $appName to entertainment apps")
                    }
                }
            } catch (e: Exception) {
                Log.e("AppBlock", "Error processing app $packageName: ${e.message}")
                continue
            }
        }

        // Also load user-categorized apps from preferences
        loadUserCategorizedApps()

        // Load blocking states from preferences
        loadBlockingStates()

        // Add test data if lists are empty (useful for emulators with few apps)
        addTestDataIfEmpty()

        // Sort apps alphabetically
        entertainmentApps.sortBy { it.name }
        socialApps.sortBy { it.name }

        // Update adapters
        entertainmentAdapter.notifyDataSetChanged()
        socialAdapter.notifyDataSetChanged()

        Log.d("AppBlock", "Categorized ${entertainmentApps.size} entertainment apps")
        Log.d("AppBlock", "Categorized ${socialApps.size} social apps")
    }

    private fun loadUserCategorizedApps() {
        val prefs = getSharedPreferences("user_categorized_apps", MODE_PRIVATE)

        // Load user-categorized entertainment apps
        val userEntertainmentApps = prefs.getStringSet("entertainment_apps", setOf()) ?: setOf()
        for (packageName in userEntertainmentApps) {
            // Skip if already in list
            if (entertainmentApps.any { it.packageName == packageName }) {
                continue
            }

            try {
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                val appName = packageManager.getApplicationLabel(appInfo).toString()
                val appIcon = packageManager.getApplicationIcon(appInfo)
                entertainmentApps.add(AppInfo(packageName, appName, appIcon, "ENTERTAINMENT"))
                Log.d("AppBlock", "Added user-categorized entertainment app: $appName")
            } catch (e: Exception) {
                // App might have been uninstalled
                Log.e("AppBlock", "Error loading user entertainment app $packageName: ${e.message}")
            }
        }

        // Load user-categorized social apps
        val userSocialApps = prefs.getStringSet("social_apps", setOf()) ?: setOf()
        for (packageName in userSocialApps) {
            // Skip if already in list
            if (socialApps.any { it.packageName == packageName }) {
                continue
            }

            try {
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                val appName = packageManager.getApplicationLabel(appInfo).toString()
                val appIcon = packageManager.getApplicationIcon(appInfo)
                socialApps.add(AppInfo(packageName, appName, appIcon, "SOCIAL"))
                Log.d("AppBlock", "Added user-categorized social app: $appName")
            } catch (e: Exception) {
                // App might have been uninstalled
                Log.e("AppBlock", "Error loading user social app $packageName: ${e.message}")
            }
        }
    }

    private fun loadBlockingStates() {
        val prefs = getSharedPreferences("app_blocking", MODE_PRIVATE)

        // Load states for entertainment apps
        for (app in entertainmentApps) {
            app.isBlocked = prefs.getBoolean(app.packageName, false)
        }

        // Load states for social apps
        for (app in socialApps) {
            app.isBlocked = prefs.getBoolean(app.packageName, false)
        }
    }

    private fun addTestDataIfEmpty() {
        if (entertainmentApps.isEmpty()) {
            // Add test entertainment apps
            val defaultIcon = ContextCompat.getDrawable(this, android.R.drawable.ic_media_play)
            entertainmentApps.add(AppInfo("com.example.youtube", "YouTube", defaultIcon!!, "ENTERTAINMENT"))
            entertainmentApps.add(AppInfo("com.example.netflix", "Netflix", defaultIcon, "ENTERTAINMENT"))
            entertainmentApps.add(AppInfo("com.example.spotify", "Spotify", defaultIcon, "ENTERTAINMENT"))
            Log.d("AppBlock", "Added test entertainment apps")
        }

        if (socialApps.isEmpty()) {
            // Add test social apps
            val defaultIcon = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_share)
            socialApps.add(AppInfo("com.example.facebook", "Facebook", defaultIcon!!, "SOCIAL"))
            socialApps.add(AppInfo("com.example.instagram", "Instagram", defaultIcon, "SOCIAL"))
            socialApps.add(AppInfo("com.example.twitter", "Twitter", defaultIcon, "SOCIAL"))
            Log.d("AppBlock", "Added test social apps")
        }
    }

    private fun toggleEntertainmentSection() {
        isEntertainmentExpanded = !isEntertainmentExpanded

        // Rotate the arrow icon
        rotateArrow(binding.ivEntertainmentExpand, isEntertainmentExpanded)

        Log.d("AppBlock", "Entertainment expanded: $isEntertainmentExpanded")

        if (isEntertainmentExpanded) {
            binding.llEntertainmentContent.visibility = View.VISIBLE
            val animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
            binding.llEntertainmentContent.startAnimation(animation)
        } else {
            val animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)
            animation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(p0: android.view.animation.Animation?) {}
                override fun onAnimationRepeat(p0: android.view.animation.Animation?) {}
                override fun onAnimationEnd(p0: android.view.animation.Animation?) {
                    binding.llEntertainmentContent.visibility = View.GONE
                }
            })
            binding.llEntertainmentContent.startAnimation(animation)
        }
    }

    private fun toggleSocialSection() {
        isSocialExpanded = !isSocialExpanded

        // Rotate the arrow icon
        rotateArrow(binding.ivSocialExpand, isSocialExpanded)

        Log.d("AppBlock", "Social expanded: $isSocialExpanded")

        if (isSocialExpanded) {
            binding.llSocialContent.visibility = View.VISIBLE
            val animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
            binding.llSocialContent.startAnimation(animation)
        } else {
            val animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)
            animation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(p0: android.view.animation.Animation?) {}
                override fun onAnimationRepeat(p0: android.view.animation.Animation?) {}
                override fun onAnimationEnd(p0: android.view.animation.Animation?) {
                    binding.llSocialContent.visibility = View.GONE
                }
            })
            binding.llSocialContent.startAnimation(animation)
        }
    }

    private fun rotateArrow(arrow: ImageView, isExpanded: Boolean) {
        val rotation = if (isExpanded) 180f else 0f
        arrow.animate().rotation(rotation).setDuration(300).start()
    }

    private fun saveAppBlockingState(app: AppInfo, isBlocked: Boolean) {
        val prefs = getSharedPreferences("app_blocking", MODE_PRIVATE)
        prefs.edit().putBoolean(app.packageName, isBlocked).apply()
        Log.d("AppBlock", "Saved blocking state for ${app.name}: $isBlocked")
    }

    private fun openAppSelector(category: AppCategory) {
        val intent = Intent(this, SelectAppsActivity::class.java).apply {
            putExtra("category", category.name)

            // Pass the list of already selected packages
            val selectedPackages = when (category) {
                AppCategory.ENTERTAINMENT ->
                    entertainmentApps.map { it.packageName }.toTypedArray()
                AppCategory.SOCIAL ->
                    socialApps.map { it.packageName }.toTypedArray()
                else -> arrayOf()
            }
            putExtra("already_selected", selectedPackages)
        }

        when (category) {
            AppCategory.ENTERTAINMENT -> selectEntertainmentAppsLauncher.launch(intent)
            AppCategory.SOCIAL -> selectSocialAppsLauncher.launch(intent)
            else -> { /* Do nothing */ }
        }
    }

    private fun addSelectedApps(selectedPackages: Array<String>, category: AppCategory) {
        val prefs = getSharedPreferences("user_categorized_apps", MODE_PRIVATE)
        val editor = prefs.edit()

        when (category) {
            AppCategory.ENTERTAINMENT -> {
                // Save to preferences
                val existingSet = prefs.getStringSet("entertainment_apps", setOf()) ?: setOf()
                val newSet = existingSet.toMutableSet()
                newSet.addAll(selectedPackages)
                editor.putStringSet("entertainment_apps", newSet)

                // Add to current list if not already present
                for (packageName in selectedPackages) {
                    if (entertainmentApps.none { it.packageName == packageName }) {
                        try {
                            val appInfo = packageManager.getApplicationInfo(packageName, 0)
                            val appName = packageManager.getApplicationLabel(appInfo).toString()
                            val appIcon = packageManager.getApplicationIcon(appInfo)
                            entertainmentApps.add(AppInfo(packageName, appName, appIcon, "ENTERTAINMENT"))
                        } catch (e: Exception) {
                            Log.e("AppBlock", "Error adding entertainment app: ${e.message}")
                        }
                    }
                }

                // Resort and update
                entertainmentApps.sortBy { it.name }
                entertainmentAdapter.notifyDataSetChanged()
            }
            AppCategory.SOCIAL -> {
                // Save to preferences
                val existingSet = prefs.getStringSet("social_apps", setOf()) ?: setOf()
                val newSet = existingSet.toMutableSet()
                newSet.addAll(selectedPackages)
                editor.putStringSet("social_apps", newSet)

                // Add to current list if not already present
                for (packageName in selectedPackages) {
                    if (socialApps.none { it.packageName == packageName }) {
                        try {
                            val appInfo = packageManager.getApplicationInfo(packageName, 0)
                            val appName = packageManager.getApplicationLabel(appInfo).toString()
                            val appIcon = packageManager.getApplicationIcon(appInfo)
                            socialApps.add(AppInfo(packageName, appName, appIcon, "SOCIAL"))
                        } catch (e: Exception) {
                            Log.e("AppBlock", "Error adding social app: ${e.message}")
                        }
                    }
                }

                // Resort and update
                socialApps.sortBy { it.name }
                socialAdapter.notifyDataSetChanged()
            }
            else -> { /* Do nothing */ }
        }

        editor.apply()
    }
}