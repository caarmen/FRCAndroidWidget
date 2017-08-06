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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.GregorianCalendar;
import java.util.Locale;

import ca.rmen.android.frccommon.Constants;
import ca.rmen.android.frccommon.prefs.FRCPreferences;
import ca.rmen.lfrc.FrenchRevolutionaryCalendar;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

@TargetApi(Constants.MIN_API_LEVEL_F2G_CONVERTER)
class F2gConverterModel {

    private final F2gConverterView mView;
    private FrenchRevolutionaryCalendar mFrcRomme;
    private FrenchRevolutionaryCalendar mFrcEquinox;
    private FrenchRevolutionaryCalendar mFrcVonMadler;

    F2gConverterModel(F2gConverterView view) {
        mView = view;
        mView.setListener(new ConverterListener());

        Context context = mView.getContext();

        Locale locale = FRCPreferences.getInstance(context).getLocale();
        FrenchRevolutionaryCalendar.CalculationMethod method = FRCPreferences.getInstance(context).getCalculationMethod();
        mFrcRomme = new FrenchRevolutionaryCalendar(locale, FrenchRevolutionaryCalendar.CalculationMethod.ROMME);
        mFrcEquinox = new FrenchRevolutionaryCalendar(locale, FrenchRevolutionaryCalendar.CalculationMethod.EQUINOX);
        mFrcVonMadler = new FrenchRevolutionaryCalendar(locale, FrenchRevolutionaryCalendar.CalculationMethod.VON_MADLER);

        mView.setLocale(locale);
        mView.setMethod(method);
        mView.setUseRomanNumerals(FRCPreferences.getInstance(context).isRomanNumeralEnabled());

        PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(mPrefsListener);
    }

    void destroy() {
        PreferenceManager.getDefaultSharedPreferences(mView.getContext()).unregisterOnSharedPreferenceChangeListener(mPrefsListener);
    }

    private FrenchRevolutionaryCalendar getCalendar(FrenchRevolutionaryCalendar.CalculationMethod method) {
        if (method == FrenchRevolutionaryCalendar.CalculationMethod.ROMME) {
            return mFrcRomme;
        } else if (method == FrenchRevolutionaryCalendar.CalculationMethod.EQUINOX) {
            return mFrcEquinox;
        } else {
            return mFrcVonMadler;
        }
    }

    private class ConverterListener implements F2gConverterView.F2gConverterListener {
        @Override
        public void onFrcDateSelected(FrenchRevolutionaryCalendar.CalculationMethod method, FrenchRevolutionaryCalendarDate frcDate) {
            final GregorianCalendar gregDate;
            if (frcDate == null) {
                gregDate = null;
            } else {
                gregDate = getCalendar(method).getDate(frcDate);
            }
            mView.setGregorianDate(gregDate);
        }
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener mPrefsListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (FRCPreferences.PREF_LANGUAGE.equals(key)) {
                Locale locale = FRCPreferences.getInstance(mView.getContext().getApplicationContext()).getLocale();
                mFrcRomme = new FrenchRevolutionaryCalendar(locale, FrenchRevolutionaryCalendar.CalculationMethod.ROMME);
                mFrcEquinox = new FrenchRevolutionaryCalendar(locale, FrenchRevolutionaryCalendar.CalculationMethod.EQUINOX);
                mFrcVonMadler = new FrenchRevolutionaryCalendar(locale, FrenchRevolutionaryCalendar.CalculationMethod.VON_MADLER);
                mView.setLocale(locale);
            } else if (FRCPreferences.PREF_ROMAN_NUMERAL.equals(key)) {
                mView.setUseRomanNumerals(FRCPreferences.getInstance(mView.getContext().getApplicationContext()).isRomanNumeralEnabled());
            }
        }
    };
}
