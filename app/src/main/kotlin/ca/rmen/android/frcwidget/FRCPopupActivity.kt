/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2011 - 2017 Carmen Alvarez
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses/>.
 */

package ca.rmen.android.frcwidget

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ca.rmen.android.frccommon.Action
import ca.rmen.android.frccommon.Constants
import ca.rmen.android.frccommon.compat.ApiHelper
import ca.rmen.android.frenchcalendar.R
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate

/**
 * Display a list of actions for the user. These are displayed in a context-menu style:
 * This activity has a transparent background and displays an alert dialog.
 *
 */
class FRCPopupActivity : Activity() {
    companion object {
        private val TAG = Constants.TAG + FRCPopupActivity::class.java.simpleName
        const val EXTRA_DATE = "extra_date"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.v(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        // Build our adapter with the list of actions
        val adapter = ActionsAdapter(this)
        val frenchDate = intent.getSerializableExtra(EXTRA_DATE) as FrenchRevolutionaryCalendarDate
        adapter.add(Action.getDarkShareAction(this, frenchDate))
        adapter.add(Action.getSettingsAction(this))
        if (ApiHelper.apiLevel >= Constants.MIN_API_LEVEL_TWO_WAY_CONVERTER) {
            adapter.add(Action.getConverterAction(this))
        } else {
            adapter.add(Action.getLegacyConverterAction(this))
        }
        adapter.add(Action.getDarkSearchAction(this, frenchDate))

        // Build the alert dialog.
        val builder = AlertDialog.Builder(this)
        builder.setAdapter(adapter, mListener)
        val dialog = builder.create()
        dialog.setOnDismissListener(mDismissListener)
        dialog.show()
    }

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.v(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.v(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy")
    }

    private class ActionsAdapter constructor(context: Context) : ArrayAdapter<Action>(context, R.layout.popup_item) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val textView = super.getView(position, convertView, parent) as TextView
            val action = getItem(position)
            if (action != null) {
                textView.text = action.title
                textView.setCompoundDrawablesWithIntrinsicBounds(action.iconId, 0, 0, 0)
            }
            return textView
        }
    }

    private val mListener = DialogInterface.OnClickListener { dialog, which ->
        val adapter = (dialog as AlertDialog).listView.adapter as ActionsAdapter
        val action = adapter.getItem(which)
        if (action != null) {
            Log.v(TAG, "clicked on action " + action.title)
            startActivity(action.intent)
        }
        finish()
    }
    private val mDismissListener = DialogInterface.OnDismissListener { _ -> finish() }

}
