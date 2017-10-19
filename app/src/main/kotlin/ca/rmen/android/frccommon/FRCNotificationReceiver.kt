/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2016-2017 Carmen Alvarez
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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ca.rmen.android.frccommon.prefs.FRCPreferences

class FRCNotificationReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION = "ca.rmen.android.frenchcalendar.NOTIFICATION"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = FRCPreferences.getInstance(context)
        if (prefs.systemNotificationEnabled) {
            FRCSystemNotification.showNotification(context)
        }
    }
}
