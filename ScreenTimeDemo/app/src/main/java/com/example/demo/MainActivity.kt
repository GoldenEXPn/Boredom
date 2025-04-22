package com.example.demo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.demo.databinding.ActivityMainBinding
import com.example.demo.databinding.ActivityScreenTimeGoalBinding
import com.example.demo.databinding.ActivityScreenTimeGoalPieBinding

class MainActivity : AppCompatActivity() {

    private lateinit var btns: List<RelativeLayout>
    private var isExpanded = false

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btns = listOf(
            findViewById(R.id.btn_TBD),
            findViewById(R.id.btn_Data),
            findViewById(R.id.btn_Settings),
            findViewById(R.id.btn_app_monitor)
        )

        binding.btnBoredom.setOnClickListener {
            if (isExpanded) {
                collapseButtons()
            } else {
                expandButtons()
            }
            isExpanded = !isExpanded
        }

        binding.btnSettings.setOnClickListener{
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        binding.btnData.setOnClickListener{
            startActivity(Intent(this, ScreenTimeGoalActivity::class.java))
        }
        binding.btnAppMonitor.setOnClickListener{
            startActivity(Intent(this, ScreenTimeGoalPieActivity::class.java))
        }
    }

    private fun expandButtons() {
        // 自定义偏移量（单位：px）
        val offsets = listOf(
            Pair(-300f, -470f),
            Pair(300f, -300f),
            Pair(-220f, 380f),
            Pair(300f, 240f)
        )

        for ((index, btn) in btns.withIndex()) {
            btn.visibility = View.VISIBLE
            btn.alpha = 0f
            btn.post {
                val (offsetX, offsetY) = offsets[index]

                btn.animate()
                    .translationX(offsetX)
                    .translationY(offsetY)
                    .alpha(1f)
                    .setDuration(500)
                    .setInterpolator(OvershootInterpolator())
                    .start()
            }
        }
    }

    private fun collapseButtons() {
        for (btn in btns) {
            btn.animate()
                .translationX(0f)
                .translationY(0f)
                .alpha(0f)
                .setDuration(500)
                .setInterpolator(OvershootInterpolator())
                .withEndAction { btn.visibility = View.GONE }
                .start()
        }
    }
}