/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2011 - 2014 Carmen Alvarez
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
package ca.rmen.android.frcwidget;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ca.rmen.android.frccommon.Constants;
import ca.rmen.android.frccommon.FRCDateUtils;
import ca.rmen.android.frccommon.prefs.FRCPreferenceActivity;
import ca.rmen.android.frcwidget.render.Font;
import ca.rmen.android.frenchcalendar.R;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

/**
 * Display a list of actions for the user. These are displayed in a context-menu style:
 * This activity has a transparent background and displays an alert dialog.
 * 
 */
public class FRCPopupActivity extends Activity { // NO_UCD (use default)
    private static final String TAG = Constants.TAG + FRCPopupActivity.class.getSimpleName();
    private static final int ACTION_SHARE = 1;
    private static final int ACTION_SETTINGS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        // Build our adapter with the list of actions
        ActionsAdapter adapter = new ActionsAdapter(this);
        adapter.add(new Action(ACTION_SHARE, R.string.popup_action_share, R.drawable.ic_action_share));
        adapter.add(new Action(ACTION_SETTINGS, R.string.popup_action_settings, R.drawable.ic_action_settings));

        // Build the alert dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setAdapter(adapter, listener);
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(dismissListener);
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }

    private static class Action {
        final int id;
        final int titleId;
        final int iconId;

        public Action(int id, int titleId, int iconId) {
            this.id = id;
            this.titleId = titleId;
            this.iconId = iconId;
        }
    }

    private static class ActionsAdapter extends ArrayAdapter<Action> {

        public ActionsAdapter(Context context) {
            super(context, R.layout.popup_item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) super.getView(position, convertView, parent);
            Action action = getItem(position);
            textView.setText(action.titleId);
            textView.setCompoundDrawablesWithIntrinsicBounds(action.iconId, 0, 0, 0);
            Font.applyFont(getContext(), textView);
            return textView;
        }
    }

    private final OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            ActionsAdapter adapter = (ActionsAdapter) ((AlertDialog) dialog).getListView().getAdapter();
            Action action = adapter.getItem(which);
            switch (action.id) {
                case ACTION_SHARE:
                    // Prepare the text to share, based on the current date.
                    FrenchRevolutionaryCalendarDate frenchDate = FRCDateUtils.getToday(getApplication());
                    String subject = getString(R.string.share_subject, frenchDate.getWeekdayName(), frenchDate.dayOfMonth, frenchDate.getMonthName(),
                            frenchDate.year);
                    String time = String.format(Locale.US, "%02d:%02d:%02d", frenchDate.hour, frenchDate.minute, frenchDate.second);
                    String objectType = getResources().getStringArray(R.array.daily_object_types)[frenchDate.getObjectType().ordinal()];
                    String body = getString(R.string.share_body, frenchDate.getWeekdayName(), frenchDate.dayOfMonth, frenchDate.getMonthName(),
                            frenchDate.year, time, objectType, frenchDate.getDayOfYear(), FRCDateUtils.getDaysSinceDay1());

                    // Open an intent chooser to share our text.
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, body);
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.share_chooser_title)));
                    Log.v(TAG, "started share chooser");
                    break;
                case ACTION_SETTINGS:
                    Intent settingsIntent = new Intent(getApplication(), FRCPreferenceActivity.class);
                    startActivity(settingsIntent);
                    Log.v(TAG, "started settings activity");
                    break;
                default:
                    break;
            }
            finish();

        }
    };
    private final OnDismissListener dismissListener = new OnDismissListener() {

        @Override
        public void onDismiss(DialogInterface dialog) {
            finish();
        }
    };
}
