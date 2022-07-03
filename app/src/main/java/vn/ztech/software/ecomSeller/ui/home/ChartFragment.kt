package vn.ztech.software.ecomSeller.ui.home

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import kotlinx.android.synthetic.main.fragment_chart.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.databinding.FragmentChartBinding
import vn.ztech.software.ecomSeller.model.OrderWithTime
import vn.ztech.software.ecomSeller.ui.BaseFragment2
import java.time.LocalDate


class ChartFragment : BaseFragment2<FragmentChartBinding>() {
    private val viewModel: ChartViewModel by viewModel()
    private val saleReportViewModel: SaleReportViewModel by sharedViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.takeIf { it.containsKey("indicator") }?.let {
            viewModel.indicator.value = it.getString("indicator")
            viewModel.orders.value = saleReportViewModel.orders.value
        }
    }

    override fun setUpViews() {
        super.setUpViews()
        toastCenter("inside ChartFragment")
        binding.chart.setBackgroundColor(Color.WHITE)
        binding.chart.description.isEnabled = false
        binding.chart.setTouchEnabled(true)

////        // set listeners
////        chart.setOnChartValueSelectedListener(this)
////        chart.setDrawGridBackground(false)
////
////
////        // create marker to display box when values are selected
////        val mv = MyMarkerView(this, R.layout.custom_marker_view)
////
////        // Set the marker to the chart
////
////        // Set the marker to the chart
////        mv.setChartView(chart)
////        chart.marker = mv
//
//
        // enable scaling and dragging
        binding.chart.isDragEnabled = true
        binding.chart.setScaleEnabled(true)

        // force pinch zoom along both axis
        binding.chart.setPinchZoom(true)

        /**axis*/
        binding.chart.axisRight.isEnabled = false
    }

    override fun observeView() {
        super.observeView()
        viewModel.orders.observe(viewLifecycleOwner){
            it?.let {
                generateEntries(it)
            }
        }
        viewModel.entries.observe(viewLifecycleOwner){
            it?.let {
                setUpChart(it)
            }
        }
    }

    private fun setUpChart(it: List<Pair<Float, Float>>) {
        binding.chart.axisLeft.axisMaximum = 200f/*viewModel.maxY * 120 / 100*/ /**set y axis with max value of 120% compare to max value of entries*/
        binding.chart.axisLeft.axisMinimum = 10f

        val entries = java.util.ArrayList<Entry>()

        for (i in 0 until 45) {
            val `val`: Float = (Math.random() * 200).toFloat() - 30
            entries.add(Entry(i.toFloat(), `val`, resources.getDrawable(R.drawable.star)))
        }
        Log.d("ENTRIES", entries.toString())

//        val entries = ArrayList<Entry>()
//        it.forEach {
//            entries.add(Entry(it.first, it.second))
//        }
        val set1: LineDataSet
        if (binding.chart.data != null && binding.chart.data.dataSetCount > 0){
            set1 = chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = entries
            set1.notifyDataSetChanged()
            binding.chart.data.notifyDataChanged()
            binding.chart.notifyDataSetChanged()
        }else{
            set1 = LineDataSet(entries,"DataSet 1")
            set1.setDrawIcons(false)

            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f)

            // black lines and points
            set1.color = Color.BLACK
            set1.setCircleColor(Color.BLACK)

            // line thickness and point size
            set1.lineWidth = 1f
            set1.circleRadius = 3f


            // draw points as solid circles
            set1.setDrawCircleHole(false)


            // customize legend entry
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f

            // text size of values
            set1.valueTextSize = 9f

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f)

            set1.setDrawFilled(true)
            set1.fillFormatter =
                IFillFormatter { dataSet, dataProvider -> chart.axisLeft.axisMinimum }

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.fade_red)
                set1.fillDrawable = drawable
            } else {
                set1.fillColor = Color.BLACK
            }

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1)
            val data = LineData(dataSets)

            binding.chart.data = data
        }
    }

    private fun generateEntries(it: Map<LocalDate, List<OrderWithTime>>) {
        viewModel.generateEntries(it)
    }

    override fun setViewBinding(): FragmentChartBinding {
        return FragmentChartBinding.inflate(layoutInflater)
    }



}