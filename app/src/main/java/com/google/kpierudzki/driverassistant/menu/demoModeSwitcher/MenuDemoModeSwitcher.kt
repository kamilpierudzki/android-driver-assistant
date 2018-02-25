package com.google.kpierudzki.driverassistant.menu.demoModeSwitcher

import android.view.View
import android.widget.Switch
import com.google.kpierudzki.driverassistant.App
import com.google.kpierudzki.driverassistant.GlobalConfig
import com.google.kpierudzki.driverassistant.R
import com.google.kpierudzki.driverassistant.common.IDestroyable

/**
 * Created by Kamil on 11.02.2018.
 */

class MenuDemoModeSwitcher(root: View) : IDestroyable {

    init {
        val switch = root.findViewById<Switch>(R.id.demo_mode_switcher_switcher)
        switch.isChecked = GlobalConfig.DEMO_MODE
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            App.restartApp(isChecked)
        }
    }

    override fun onDestroy() {
        //Ignore
    }
}
