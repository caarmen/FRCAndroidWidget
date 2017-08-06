/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2011 - 2016 Carmen Alvarez
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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ca.rmen.android.frccommon.Action;
import ca.rmen.android.frccommon.Constants;
import ca.rmen.android.frenchcalendar.R;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

/**
 * Display a list of actions for the user. These are displayed in a context-menu style:
 * This activity has a transparent background and displays an alert dialog.
 * 
 */
public class FRCPopupActivity extends Activity { // NO_UCD (use default)
    private static final String TAG = Constants.TAG + FRCPopupActivity.class.getSimpleName();

    public static final String EXTRA_DATE = "extra_date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        // Build our adapter with the list of actions
        ActionsAdapter adapter = new ActionsAdapter(this);
        FrenchRevolutionaryCalendarDate frenchDate = (FrenchRevolutionaryCalendarDate) getIntent().getSerializableExtra(EXTRA_DATE);
        adapter.add(Action.getDarkShareAction(this, frenchDate));
        adapter.add(Action.getSettingsAction(this));
        if (ApiHelper.getAPILevel() >= Constants.MIN_API_LEVEL_TWO_WAY_CONVERTER) {
            adapter.add(Action.getConverterAction(this));
        } else {
            adapter.add(Action.getLegacyConverterAction(this));
        }
        adapter.add(Action.getDarkSearchAction(this, frenchDate));

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

    private static class ActionsAdapter extends ArrayAdapter<Action> {

        ActionsAdapter(Context context) {
            super(context, R.layout.popup_item);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            TextView textView = (TextView) super.getView(position, convertView, parent);
            Action action = getItem(position);
            if (action != null) {
                textView.setText(action.title);
                textView.setCompoundDrawablesWithIntrinsicBounds(action.iconId, 0, 0, 0);
            }
            return textView;
        }
    }

    private final OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            ActionsAdapter adapter = (ActionsAdapter) ((AlertDialog) dialog).getListView().getAdapter();
            Action action = adapter.getItem(which);
            if (action != null) {
                Log.v(TAG, "clicked on action " + action.title);
                startActivity(action.intent);
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
