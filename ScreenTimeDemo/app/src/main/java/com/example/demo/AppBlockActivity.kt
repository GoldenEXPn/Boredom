package com.example.demo

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demo.databinding.ActivityAppBlockBinding

// Testing purposes
import androidx.core.content.ContextCompat


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppBlockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up RecyclerViews
        setupRecyclerViews()

        // Load apps
        loadInstalledApps()

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

    private fun loadInstalledApps() {
        val packageManager = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val installedApps = packageManager.queryIntentActivities(intent, 0)
        // Log the total number of apps found
        Log.d("AppBlock", "Found ${installedApps.size} installed apps.")

        // Clear existing lists
        entertainmentApps.clear()
        socialApps.clear()

        // Entertainment apps keywords
        val entertainmentKeywords = listOf(
            "game", "play", "music", "video", "tv", "movie", "entertainment",
            "stream", "netflix", "hulu", "disney", "youtube", "spotify", "tiktok"
        )

        // Social apps keywords
        val socialKeywords = listOf(
            "social", "chat", "message", "facebook", "instagram", "twitter",
            "snapchat", "whatsapp", "telegram", "wechat", "line", "discord",
            "reddit", "linkedin", "pinterest", "dating"
        )

        // Process each installed app
        for (app in installedApps) {
            val packageName = app.activityInfo.packageName
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val appName = packageManager.getApplicationLabel(appInfo).toString()
            val appIcon = packageManager.getApplicationIcon(appInfo)

            // Skip system apps
            if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                continue
            }

            // Determine category
            val lowerCaseName = appName.lowercase()
            val lowerCasePackage = packageName.lowercase()

            when {
                entertainmentKeywords.any { keyword ->
                    lowerCaseName.contains(keyword) || lowerCasePackage.contains(keyword)
                } -> {
                    entertainmentApps.add(AppInfo(packageName, appName, appIcon, "ENTERTAINMENT"))
                }
                socialKeywords.any { keyword ->
                    lowerCaseName.contains(keyword) || lowerCasePackage.contains(keyword)
                } -> {
                    socialApps.add(AppInfo(packageName, appName, appIcon, "SOCIAL"))
                }
            }
        }

        // Load previously saved blocking status
        loadBlockingStates()

        // Log final result
        Log.d("AppBlock", "Categorized ${entertainmentApps.size} entertainment apps")
        Log.d("AppBlock", "Categorized ${socialApps.size} social apps")

        // Add test data if no apps were categorized
        if (entertainmentApps.isEmpty()) {
            // Get default icons - using standard Android system icons
            val defaultIcon = ContextCompat.getDrawable(this, android.R.drawable.ic_media_play)

            // Add sample entertainment apps
            entertainmentApps.add(AppInfo("com.example.youtube", "YouTube", defaultIcon!!, "ENTERTAINMENT"))
            entertainmentApps.add(AppInfo("com.example.netflix", "Netflix", defaultIcon, "ENTERTAINMENT"))
            entertainmentApps.add(AppInfo("com.example.spotify", "Spotify", defaultIcon, "ENTERTAINMENT"))
            entertainmentApps.add(AppInfo("com.example.youtube", "YouTube", defaultIcon!!, "ENTERTAINMENT"))
            entertainmentApps.add(AppInfo("com.example.netflix", "Netflix", defaultIcon, "ENTERTAINMENT"))
            entertainmentApps.add(AppInfo("com.example.spotify", "Spotify", defaultIcon, "ENTERTAINMENT"))

            Log.d("AppBlock", "Added sample entertainment apps")
        }

        if (socialApps.isEmpty()) {
            // Get default icons - using standard Android system icons
            val defaultIcon = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_share)

            // Add sample social apps
            socialApps.add(AppInfo("com.example.facebook", "Facebook", defaultIcon!!, "SOCIAL"))
            socialApps.add(AppInfo("com.example.instagram", "Instagram", defaultIcon, "SOCIAL"))
            socialApps.add(AppInfo("com.example.twitter", "Twitter", defaultIcon, "SOCIAL"))
            socialApps.add(AppInfo("com.example.facebook", "Facebook", defaultIcon!!, "SOCIAL"))
            socialApps.add(AppInfo("com.example.instagram", "Instagram", defaultIcon, "SOCIAL"))
            socialApps.add(AppInfo("com.example.twitter", "Twitter", defaultIcon, "SOCIAL"))

            Log.d("AppBlock", "Added sample social apps")
        }

        Log.d("AppBlock", "Final count - Entertainment: ${entertainmentApps.size}, Social: ${socialApps.size}")

        // Update adapters
        entertainmentAdapter.notifyDataSetChanged()
        socialAdapter.notifyDataSetChanged()
    }

    private fun toggleEntertainmentSection() {
        isEntertainmentExpanded = !isEntertainmentExpanded

        // Rotate the arrow icon
        rotateArrow(binding.ivEntertainmentExpand, isEntertainmentExpanded)

        // Debug log
        Log.d("AppBlock", "Entertainment section expanded: $isEntertainmentExpanded")
        Log.d("AppBlock", "Entertainment app count: ${entertainmentApps.size}")

        if (isEntertainmentExpanded) {
            binding.llEntertainmentContent.visibility = View.VISIBLE
            // Debug visibility
            Log.d("AppBlock", "Entertainment content should now be visible")
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

        // Debug log
        Log.d("AppBlock", "Social section expanded: $isSocialExpanded")
        Log.d("AppBlock", "Social app count: ${socialApps.size}")

        if (isSocialExpanded) {
            binding.llSocialContent.visibility = View.VISIBLE
            // Debug visibility
            Log.d("AppBlock", "Social content should now be visible")
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
        // Save to SharedPreferences
        val prefs = getSharedPreferences("app_blocking", MODE_PRIVATE)
        prefs.edit().putBoolean(app.packageName, isBlocked).apply()
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
}












































