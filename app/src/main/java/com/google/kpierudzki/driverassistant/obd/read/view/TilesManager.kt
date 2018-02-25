package com.google.kpierudzki.driverassistant.obd.read.view

import android.support.v4.app.FragmentManager
import android.support.v7.widget.GridLayout
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import com.google.kpierudzki.driverassistant.R
import com.google.kpierudzki.driverassistant.common.view_components.IChartAble
import com.google.kpierudzki.driverassistant.common.view_components.TileFragmentNew
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType
import com.google.kpierudzki.driverassistant.obd.service.provider.scheduler.ScheduleItem
import com.google.kpierudzki.driverassistant.util.CommandsProvider
import com.google.kpierudzki.driverassistant.util.ScreenUtils
import java8.util.stream.Collectors
import java8.util.stream.StreamSupport

/**
 * Created by Kamil on 22.12.2017.
 */
class TilesManager : IChartAble {

    private var params: MutableMap<ObdParamType, TileFragmentNew>
    private var fragmentManager: FragmentManager

    private var scrollView: View
    private var gridLayout: GridLayout

    constructor(scrollView: View, fragmentManager: FragmentManager) {
        this.scrollView = scrollView;
        this.gridLayout = scrollView.findViewById(R.id.obd_read_grid);

        this.params = HashMap<ObdParamType, TileFragmentNew>();
        this.fragmentManager = fragmentManager;

        val availableParameters = StreamSupport.stream<ScheduleItem>(
                CommandsProvider.getSupportedCommands())
                .map<ObdParamType> { scheduleItem -> scheduleItem.command.paramType }
                .collect(Collectors.toList())

        scrollView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val columnCount: Int
                val rowCount: Int
                val orientation: Int
                val sizeOfTileInPx: Int

                if (ScreenUtils.isPortrait()) {
                    columnCount = 2
                    rowCount = availableParameters.size / 2
                    orientation = GridLayout.VERTICAL
                    sizeOfTileInPx = scrollView.width / 2
                } else {
                    rowCount = 2
                    columnCount = availableParameters.size / 2
                    orientation = GridLayout.HORIZONTAL
                    sizeOfTileInPx = scrollView.height / 2
                }

                gridLayout.columnCount = columnCount
                gridLayout.rowCount = rowCount
                gridLayout.orientation = orientation

                var counter = 0
                for (i in 0 until gridLayout.rowCount) {
                    for (j in 0 until gridLayout.columnCount) {
                        val frameLayout = FrameLayout(gridLayout.context)
                        frameLayout.layoutParams = ViewGroup.LayoutParams(
                                sizeOfTileInPx, sizeOfTileInPx)
                        frameLayout.id = View.generateViewId()

                        val param = availableParameters.get(counter++)
                        val fragment = TileFragmentNew.newInstance(param)

                        fragmentManager.beginTransaction()
                                .replace(frameLayout.id, fragment)
                                .commit()

                        params.put(param, fragment)
                        gridLayout.addView(frameLayout)
                    }
                }

                scrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    override fun updateValue(newValue: Float, paramType: ObdParamType) {
        val tileFragment = params.get(paramType)
        tileFragment?.let { tileFragment.updateValue(newValue, paramType) }
    }

    override fun updateChart(newValue: Float, paramType: ObdParamType) {
        val tileFragment = params.get(paramType)
        tileFragment?.let { tileFragment.updateChart(newValue, paramType) }
    }

    override fun restoreValuesAndUpdateChart(values: MutableList<Float>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getValues(): Array<Float> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getChartEntriesLimit(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}