package com.github.filipelipan.popularmovies.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by lispa on 26/02/2017.
 */

public class NetworkUtil {
    /**
     * verify the connection and return true if there is a connection available
     *
     * @param context the context will be use to get the connection status
     * @return true if there is a connection available and false otherwise
     */
    public static boolean isConnectionAvailable(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }
}
