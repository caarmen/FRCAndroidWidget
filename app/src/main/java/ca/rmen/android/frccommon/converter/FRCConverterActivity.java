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
package ca.rmen.android.frccommon.converter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;

import ca.rmen.android.frccommon.Constants;
import ca.rmen.android.frccommon.compat.ApiHelper;
import ca.rmen.android.frccommon.compat.ContextCompat;
import ca.rmen.android.frccommon.prefs.FRCPreferenceActivity;
import ca.rmen.android.frenchcalendar.R;

public class FRCConverterActivity extends Activity {

    private static final String TAB_G2F = "G2F";
    private static final String TAB_F2G = "F2G";
    private static final String EXTRA_SELECTED_TAB = "selected_tab";

    private F2gConverterModel mF2gModel;
    private G2fConverterModel mG2fModel;
    private TabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.converter);
        findViewById(R.id.btn_help).setOnClickListener(mOnHelpClickedListener);

        G2fConverterView g2fView = findViewById(R.id.converter_g2f);
        mG2fModel = new G2fConverterModel(g2fView);

        if (ApiHelper.getAPILevel() >= Constants.MIN_API_LEVEL_F2G_CONVERTER) {
            F2gConverterView f2gView = findViewById(R.id.converter_f2g);
            mF2gModel = new F2gConverterModel(f2gView);

            mTabHost = findViewById(android.R.id.tabhost);
            mTabHost.setup();
            addTab(TAB_G2F, R.id.converter_g2f, R.drawable.ic_tab_g2f);
            addTab(TAB_F2G, R.id.converter_f2g, R.drawable.ic_tab_f2g);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (mTabHost != null) {
            mTabHost.setCurrentTab(savedInstanceState.getInt(EXTRA_SELECTED_TAB, mTabHost.getCurrentTab()));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, FRCPreferenceActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mTabHost != null) outState.putInt(EXTRA_SELECTED_TAB, mTabHost.getCurrentTab());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        mG2fModel.destroy();
        if (mF2gModel != null) mF2gModel.destroy();
        super.onDestroy();
    }

    private void addTab(String tag, @IdRes int viewIdRes, @DrawableRes int iconRes) {
        TabHost.TabSpec spec = mTabHost.newTabSpec(tag);
        spec.setContent(viewIdRes);
        spec.setIndicator(null, ContextCompat.getDrawable(this, iconRes));
        mTabHost.addTab(spec);
    }

    private final View.OnClickListener mOnHelpClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CharSequence message = TextUtils.concat(getText(R.string.romme_description),
                    "\n",
                    getText(R.string.equinox_description),
                    "\n",
                    getText(R.string.von_madler_description));
            new AlertDialog.Builder(FRCConverterActivity.this)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    };

}
