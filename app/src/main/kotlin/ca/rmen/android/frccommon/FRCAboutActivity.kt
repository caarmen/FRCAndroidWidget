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

package ca.rmen.android.frccommon

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import ca.rmen.android.frccommon.compat.Api11Helper
import ca.rmen.android.frccommon.compat.ApiHelper
import ca.rmen.android.frcwidget.render.Font
import ca.rmen.android.frenchcalendar.R

class FRCAboutActivity : Activity() {
    companion object {
        private val TAG = Constants.TAG + FRCAboutActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about)
        if (ApiHelper.getAPILevel() >= Build.VERSION_CODES.HONEYCOMB) {
            Api11Helper.setDisplayHomeAsUpEnabled(this)
        }
        val view = findViewById<View>(R.id.aboutview)
        Font.applyFont(this, view)
        val tvAppVersion = view.findViewById<TextView>(R.id.tv_app_version)
        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            tvAppVersion.text = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, e.message, e)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
