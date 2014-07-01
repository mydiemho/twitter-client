package com.mho.mytwitter.helpers;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Style;

public class Utils {

    public static final int COMPOSE_REQUEST_CODE = 20;
    public static final int MAX_RESULT_COUNT = 25;

    private static final Configuration CROUTON_CONFIGURATION = new Configuration.Builder().setDuration(4000).build();

    public static final Style STYLE = new Style.Builder()
            .setBackgroundColorValue(Color.parseColor("#daffc0"))
            .setGravity(Gravity.CENTER_HORIZONTAL)
            .setConfiguration(CROUTON_CONFIGURATION)
            .setHeight(150)
            .setTextColorValue(Color.parseColor("#323a2c")).build();

    public static Boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
