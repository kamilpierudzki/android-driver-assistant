package com.google.kpierudzki.driverassistant.menu.about

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v4.widget.DrawerLayout
import android.view.View
import com.google.kpierudzki.driverassistant.R
import com.google.kpierudzki.driverassistant.common.IDestroyable
import kotlinx.android.synthetic.main.nav_layout.view.*

/**
 * Created by Kamil on 27.02.2018.
 */
class MenuDrawerAbout(root: View, fragmentManager: FragmentManager, drawer: DrawerLayout) : IDestroyable {

    init {
        root.nav_about.setOnClickListener({
            drawer.closeDrawers()
            AboutDialog().show(fragmentManager, AboutDialog.TAG)
        })
    }

    override fun onDestroy() {
        //Ignore
    }
}

class AboutDialog : DialogFragment() {

    companion object {
        val TAG = "AboutDialog_TAG"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setMessage(R.string.Drawer_Menu_About_Dialog_Msg)
                .setPositiveButton(R.string.Drawer_Menu_About_Dialog_Button) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()
    }
}