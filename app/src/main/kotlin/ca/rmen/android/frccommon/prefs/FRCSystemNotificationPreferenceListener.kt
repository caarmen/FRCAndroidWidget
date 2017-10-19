/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2016 - 2017 Carmen Alvarez
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

package ca.rmen.android.frccommon.prefs

import android.content.Context
import android.content.SharedPreferences
import ca.rmen.android.frccommon.FRCNotificationScheduler
import ca.rmen.android.frccommon.FRCSystemNotification

class FRCSystemNotificationPreferenceListener constructor(context: Context) : SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        private lateinit var mContext: Context
    }

    init {
        mContext = context
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (FRCPreferences.PREF_SYSTEM_NOTIFICATION == key) {
            val prefs = FRCPreferences.getInstance(mContext)
            if (prefs.systemNotificationEnabled) {
                FRCSystemNotification.showNotification(mContext)
                FRCNotificationScheduler.scheduleRepeatingAlarm(mContext)
            } else {
                FRCNotificationScheduler.unscheduleRepeatingAlarm(mContext)
            }
        } else if (FRCPreferences.PREF_SYSTEM_NOTIFICATION_PRIORITY == key) {
            FRCSystemNotification.hideNotification(mContext)
            FRCSystemNotification.showNotification(mContext)
        }
    }
}
