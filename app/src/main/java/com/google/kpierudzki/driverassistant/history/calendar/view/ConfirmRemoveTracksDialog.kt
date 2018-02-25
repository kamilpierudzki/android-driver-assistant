package com.google.kpierudzki.driverassistant.history.calendar.view

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.google.kpierudzki.driverassistant.R
import com.google.kpierudzki.driverassistant.history.calendar.HistoryCalendarContract

/**
 * Created by Kamil on 18.02.2018.
 */

class ConfirmRemoveTracksDialog : DialogFragment() {

    companion object {
        val TAG: String = "ConfirmRemoveTracksDialog_TAG"
        val KEY_ITEMS: String = "KEY_ITEMS"
    }

    private var _callbacks: ConfirmRemoveCallback? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val items = ArrayList<HistoryCalendarContract.CalendarTranslateInfoModel>()

        arguments?.let {
            items.addAll(it.getParcelableArrayList(HistoryCalendarFragment.ITEMS_TO_REMOVE_KEY))
        }

        val builder = AlertDialog.Builder(activity)
        builder.setMessage(R.string.History_Calendar_RemoveDialog_Title)
                .setPositiveButton(R.string.History_Calendar_RemoveDialog_Positive) { dialog, _ ->
                    _callbacks?.onTracksRemove(items)
                    dialog?.cancel()
                }
                .setNegativeButton(R.string.History_Calendar_RemoveDialog_Negative, { dialog, _ ->
                    _callbacks?.onCancel()
                    dialog?.cancel()
                })
                .setCancelable(false)
        return builder.create()
    }

    fun newInstance(items: ArrayList<HistoryCalendarContract.CalendarTranslateInfoModel>):
            ConfirmRemoveTracksDialog {
        val dialog = ConfirmRemoveTracksDialog()
        val args = Bundle()
        args.putSerializable(KEY_ITEMS, items)
        dialog.arguments = args
        return dialog
    }

    interface ConfirmRemoveCallback {
        fun onTracksRemove(tracksToRemove: ArrayList<HistoryCalendarContract.CalendarTranslateInfoModel>)
        fun onCancel();
    }

    override fun onAttachFragment(childFragment: Fragment?) {
        super.onAttachFragment(childFragment)
        _callbacks = null
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)

        val calendarFragment = (activity as? AppCompatActivity)
                ?.supportFragmentManager
                ?.findFragmentByTag(HistoryCalendarFragment.TAG)
        calendarFragment?.let {
            _callbacks = it as? ConfirmRemoveCallback
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        val calendarFragment = (context as? AppCompatActivity)
                ?.supportFragmentManager
                ?.findFragmentByTag(HistoryCalendarFragment.TAG)
        calendarFragment?.let {
            _callbacks = it as? ConfirmRemoveCallback
        }
    }
}
