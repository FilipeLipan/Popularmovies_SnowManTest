package com.github.filipelipan.popularmovies.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by lispa on 12/03/2017.
 */

public class NetworkConnectReceiver extends BroadcastReceiver {

    public static final String NOTIFY_NETWORK_CHANGE = "NOTIFY_NETWORK_CHANGE";
    public static final String EXTRA_IS_CONNECTED = "EXTRA_IS_CONNECTED";

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        Intent localIntent = new Intent(NOTIFY_NETWORK_CHANGE);
        localIntent.putExtra(EXTRA_IS_CONNECTED, isOnline());
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
