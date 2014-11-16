/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2014 Benoit 'BoD' Lubek (BoD@JRAF.org)
 * Copyright (C) 2011 - 2014 Carmen Alvarez
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package ca.rmen.android.frcwidget.wear;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import ca.rmen.android.frenchcalendar.common.WearCommConstants;

/**
 * Helper singleton class to communicate with wearables.<br/>
 * Note: {@link #connect(android.content.Context)} must be called prior to calling all the other methods.<br/>
 */
public class FRCWearCommHelper {
    private static final String TAG = FRCWearCommHelper.class.getSimpleName();

    private static final FRCWearCommHelper INSTANCE = new FRCWearCommHelper();

    private GoogleApiClient mGoogleApiClient;

    private FRCWearCommHelper() {}

    public static FRCWearCommHelper get() {
        return INSTANCE;
    }

    public void connect(Context context) {
        Log.d(TAG, "connect");
        if (mGoogleApiClient != null) {
            Log.d(TAG, "connect Already connected");
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(context).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle connectionHint) {
                Log.d(TAG, "onConnected connectionHint=" + connectionHint);
            }

            @Override
            public void onConnectionSuspended(int cause) {
                Log.w(TAG, "onConnectionSuspended cause=" + cause);
                // TODO reconnect
            }
        }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult result) {
                Log.w(TAG, "onConnectionFailed result=" + result);
                // TODO handle failures
            }
        }).addApi(Wearable.API).build();
        mGoogleApiClient.connect();
    }

    public void disconnect() {
        Log.d(TAG, "disconnect");
        if (mGoogleApiClient != null) mGoogleApiClient.disconnect();
        mGoogleApiClient = null;
    }


    /*
     * Today.
     */

    /**
     * This must not be called from the UI thread.
     */
    public void updateToday(String date, String dayOfYear, int month) {
        Log.d(TAG, "updateToday date=" + date + " dayOfYear=" + dayOfYear);
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(WearCommConstants.PATH_TODAY);

        DataMap dataMap = putDataMapRequest.getDataMap();
        dataMap.putString(WearCommConstants.EXTRA_DATE, date);
        dataMap.putString(WearCommConstants.EXTRA_DAY_OF_YEAR, dayOfYear);
        dataMap.putInt(WearCommConstants.EXTRA_MONTH, month);

        PutDataRequest request = putDataMapRequest.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, request).await();
    }

    /**
     * This must not be called from the UI thread.
     */
    public void removeToday() {
        Log.d(TAG, "removeToday");
        Wearable.DataApi.deleteDataItems(mGoogleApiClient, createUri(WearCommConstants.PATH_TODAY)).await();
    }


    /*
     * Misc.
     */

    private static Uri createUri(String path) {
        return new Uri.Builder().scheme("wear").path(path).build();
    }
}