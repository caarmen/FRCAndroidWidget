/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2017 Carmen Alvarez
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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import ca.rmen.android.frccommon.prefs.FRCPreferences;
import ca.rmen.lfrc.FrenchRevolutionaryCalendar;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

class G2fConverterModel {

    private final G2fConverterView mView;
    private FrenchRevolutionaryCalendar mFrcRomme;
    private FrenchRevolutionaryCalendar mFrcEquinox;
    private FrenchRevolutionaryCalendar mFrcVonMadler;

    G2fConverterModel(G2fConverterView view) {
        mView = view;
        mView.setListener(new ConverterListener());

        Context context = mView.getContext();

        Locale locale = FRCPreferences.getInstance(context).getLocale();
        mFrcRomme = new FrenchRevolutionaryCalendar(locale, FrenchRevolutionaryCalendar.CalculationMethod.ROMME);
        mFrcEquinox = new FrenchRevolutionaryCalendar(locale, FrenchRevolutionaryCalendar.CalculationMethod.EQUINOX);
        mFrcVonMadler = new FrenchRevolutionaryCalendar(locale, FrenchRevolutionaryCalendar.CalculationMethod.VON_MADLER);

        updateFrcDateTexts((GregorianCalendar) Calendar.getInstance());

        PreferenceManager.getDefaultSharedPreferences(mView.getContext()).registerOnSharedPreferenceChangeListener(mPrefsListener);
    }

    void destroy() {
        PreferenceManager.getDefaultSharedPreferences(mView.getContext()).unregisterOnSharedPreferenceChangeListener(mPrefsListener);
    }

    private class ConverterListener implements G2fConverterView.G2fConverterListener {
        @Override
        public void onGregorianDateSelected(GregorianCalendar date) {
            updateFrcDateTexts(date);
        }
    }

    private void updateFrcDateTexts(GregorianCalendar gregorianCalendar) {
        FrenchRevolutionaryCalendarDate frenchDateRomme = mFrcRomme.getDate(gregorianCalendar);
        FrenchRevolutionaryCalendarDate frenchDateEquinox = mFrcEquinox.getDate(gregorianCalendar);
        FrenchRevolutionaryCalendarDate frenchDateVonMadler = mFrcVonMadler.getDate(gregorianCalendar);
        mView.updateFrcDateTexts(frenchDateRomme, frenchDateEquinox, frenchDateVonMadler);
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener mPrefsListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (FRCPreferences.PREF_LANGUAGE.equals(key)) {
                Locale locale = FRCPreferences.getInstance(mView.getContext()).getLocale();
                mFrcRomme = new FrenchRevolutionaryCalendar(locale, FrenchRevolutionaryCalendar.CalculationMethod.ROMME);
                mFrcEquinox = new FrenchRevolutionaryCalendar(locale, FrenchRevolutionaryCalendar.CalculationMethod.EQUINOX);
                mFrcVonMadler = new FrenchRevolutionaryCalendar(locale, FrenchRevolutionaryCalendar.CalculationMethod.VON_MADLER);
                updateFrcDateTexts(mView.getDate());
            } else if (FRCPreferences.PREF_ROMAN_NUMERAL.equals(key)) {
                updateFrcDateTexts(mView.getDate());
            }
        }
    };
}
