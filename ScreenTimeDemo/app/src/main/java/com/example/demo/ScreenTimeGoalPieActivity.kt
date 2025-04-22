package com.example.demo

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.demo.databinding.ActivityScreenTimeGoalPieBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class ScreenTimeGoalPieActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScreenTimeGoalPieBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScreenTimeGoalPieBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.ivBack.setOnClickListener {
//            finish()
//        }
        // 下拉框适配器
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.week_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter

        // 监听选择事件
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> showSinglePieChart(generateThisWeekFakeData()) // This Week
                    1 -> showSinglePieChart(generateLastWeekFakeData()) // Last Week
                    2 -> showComparisonChart() // Comparison
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        showSinglePieChart(generateThisWeekFakeData())
        
        binding.llTitle.visibility = View.GONE
        binding.llLegend.visibility = View.GONE
    }

    /** 生成假数据 */
    private fun generateThisWeekFakeData(): List<PieEntry> {
        return listOf(
            PieEntry(50f, "Entertainment"),
            PieEntry(40f, "Work"),
            PieEntry(10f, "Social")
        )
    }

    private fun generateLastWeekFakeData(): List<PieEntry> {
        return listOf(
            PieEntry(20f, "Entertainment"),
            PieEntry(50f, "Work"),
            PieEntry(30f, "Social")
        )
    }

    /** 显示单个饼图 */
    private fun showSinglePieChart(data: List<PieEntry>) {
        binding.llTitle.visibility = View.GONE
        binding.llLegend.visibility = View.GONE
        
        binding.pieChartContainer.removeAllViews()
        val pieChart = PieChart(this)
        pieChart.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 380)
        binding.pieChartContainer.addView(pieChart)

        val dataSet = PieDataSet(data, "").apply {
            setColors(Color.parseColor("#8879FF"), Color.parseColor("#FF928A"), Color.parseColor("#3BC3DF"))
            sliceSpace = 2f
            setDrawValues(false) // 不显示百分比
        }

        val pieData = PieData(dataSet)
        pieChart.apply {
            animateXY(1000, 1000)
            this.data = pieData
            description.isEnabled = false
            isDrawHoleEnabled = true // 圆环效果
            holeRadius = 50f // 环形大小
            transparentCircleRadius = 55f // 设置透明圆环半径，增强美观度
            setDrawEntryLabels(false) // 不绘制分类标签

            centerText = "51.7" // 设置中心文字
            setCenterTextSize(18f) // 设置文字大小
            setCenterTextColor(Color.BLACK) // 设置文字颜色
            val typeface = Typeface.DEFAULT_BOLD // 默认加粗字体
            setCenterTextTypeface(typeface)

            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.CENTER
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                orientation = Legend.LegendOrientation.VERTICAL
                textSize = 12f
            }

            invalidate() // 刷新
        }

    }

    /** 显示对比模式 */
    private fun showComparisonChart() {
        binding.llTitle.visibility = View.VISIBLE
        binding.llLegend.visibility = View.VISIBLE
        binding.pieChartContainer.removeAllViews()

        // 创建左右两侧的饼图
        val pieChartLastWeek = PieChart(this)
        pieChartLastWeek.layoutParams = LinearLayout.LayoutParams(0, 380,1f)
        val pieChartThisWeek = PieChart(this)
        pieChartThisWeek.layoutParams = LinearLayout.LayoutParams(0,  380,1f)
        binding.pieChartContainer.apply {
            addView(pieChartLastWeek, 0)
            addView(pieChartThisWeek, 1)
        }

        // 设置数据
        val lastWeekData = generateLastWeekFakeData()
        val thisWeekData = generateThisWeekFakeData()

        setupLastWeekPieChart(pieChartLastWeek, lastWeekData)
        setupThisWeekPieChart(pieChartThisWeek, thisWeekData)
    }

    /** 配置饼图 */
    private fun setupLastWeekPieChart(pieChart: PieChart, data: List<PieEntry>) {
        val dataSet = PieDataSet(data, "").apply {
            setColors(Color.parseColor("#8879FF"), Color.parseColor("#FF928A"), Color.parseColor("#3BC3DF"))
            sliceSpace = 2f
            setDrawValues(false) // 不显示百分比
        }

        pieChart.apply {
            animateXY(1000, 1000)
            this.data = PieData(dataSet)
            description.isEnabled = false
            isDrawHoleEnabled = true // 圆环效果
            holeRadius = 50f // 环形大小
            transparentCircleRadius = 55f // 设置透明圆环半径，增强美观度
            setDrawEntryLabels(false) // 不绘制分类标签

            centerText = "51.7" // 设置中心文字
            setCenterTextSize(18f) // 设置文字大小
            setCenterTextColor(Color.BLACK) // 设置文字颜色
            val typeface = Typeface.DEFAULT_BOLD // 默认加粗字体
            setCenterTextTypeface(typeface)

            legend.isEnabled = false

            invalidate() // 刷新
        }
    }

    private fun setupThisWeekPieChart(pieChart: PieChart, data: List<PieEntry>) {
        val dataSet = PieDataSet(data, "").apply {
            setColors(Color.parseColor("#8879FF"), Color.parseColor("#FF928A"), Color.parseColor("#3BC3DF"))
            sliceSpace = 2f
            setDrawValues(false) // 不显示百分比
        }

        pieChart.apply {
            animateXY(1000, 1000)
            this.data = PieData(dataSet)
            description.isEnabled = false
            isDrawHoleEnabled = true // 圆环效果
            holeRadius = 50f // 环形大小
            transparentCircleRadius = 55f // 设置透明圆环半径，增强美观度
            setDrawEntryLabels(false) // 不绘制分类标签

            centerText = "51.7" // 设置中心文字
            setCenterTextSize(18f) // 设置文字大小
            setCenterTextColor(Color.BLACK) // 设置文字颜色
            val typeface = Typeface.DEFAULT_BOLD // 默认加粗字体
            setCenterTextTypeface(typeface)

            legend.isEnabled = false

            invalidate() // 刷新
        }
    }


}