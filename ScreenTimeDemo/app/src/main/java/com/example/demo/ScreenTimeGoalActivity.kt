package com.example.demo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.example.demo.databinding.ActivityScreenTimeGoalBinding

class ScreenTimeGoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScreenTimeGoalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScreenTimeGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.ivBack.setOnClickListener {
//            finish()
//        }
        binding.btnGenerate.setOnClickListener {
        }

        setupBarChart(binding.barChart)
    }

    private fun setupBarChart(barChart: BarChart) {
        val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

        // 模拟数据：Entertainment、Work、Social
        val entertainmentValues = listOf(5f, 7f, 8f, 6f, 5f, 4f, 6f)
        val workValues = listOf(8f, 9f, 7f, 10f, 9f, 6f, 5f)
        val socialValues = listOf(3f, 4f, 5f, 4f, 3f, 5f, 6f)

        val groupCount = 7
        val groupSpace = 0.3f  // 组与组之间的间距
        val barSpace = 0.05f   // 单个柱子之间的间距
        val barWidth = 0.2f   // 每个柱子的宽度

        val entriesEntertainment = mutableListOf<BarEntry>()
        val entriesWork = mutableListOf<BarEntry>()
        val entriesSocial = mutableListOf<BarEntry>()

        for (i in days.indices) {
            entriesEntertainment.add(BarEntry(i.toFloat(), entertainmentValues[i]))
            entriesWork.add(BarEntry(i.toFloat() , workValues[i]))
            entriesSocial.add(BarEntry(i.toFloat(), socialValues[i]))
        }

        val setEntertainment = BarDataSet(entriesEntertainment, "Entertainment").apply {
            color = Color.BLACK
            setGradientColor(Color.BLACK, Color.CYAN)
        }

        val setWork = BarDataSet(entriesWork, "Work").apply {
            color = Color.BLACK
            setGradientColor(Color.BLACK, Color.MAGENTA)
        }

        val setSocial = BarDataSet(entriesSocial, "Social").apply {
            color = Color.BLACK
            setGradientColor(Color.BLACK, Color.LTGRAY)
        }

        val barData = BarData(setEntertainment, setWork, setSocial)
        barData.barWidth = barWidth // 设置柱子宽度

        barChart.apply {
            data = barData
            description.isEnabled = false // 隐藏描述
            setFitBars(true)
            animateY(1000)
            barData.setDrawValues(false) // 隐藏数值
            extraBottomOffset = 20f // x轴与底部的间距
            groupBars(0f, groupSpace, barSpace)

            // 设置 X 轴
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(days)
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setCenterAxisLabels(true)
                granularity = 1f
                axisMinimum = 0f
                axisMaximum = barData.getGroupWidth(groupSpace, barSpace) * groupCount
                labelRotationAngle = -45f
            }

            // 设置 Y 轴
            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 12f
                enableGridDashedLine(10f, 10f, 0f)
            }
            axisRight.isEnabled = false

            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                setDrawInside(false)
                xEntrySpace = 20f
            }

            invalidate()
        }
    }


}