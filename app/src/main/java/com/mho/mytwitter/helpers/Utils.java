package com.mho.mytwitter.helpers;

import com.google.common.base.Splitter;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateUtils;
import android.view.Gravity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;

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

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();

            relativeDate = DateUtils.getRelativeTimeSpanString(
                    dateMillis,
                    System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS).toString();

        } catch (ParseException e) {
            e.printStackTrace();
        }

//        Log.d("DEBUG", "relativeDate: " + relativeDate);

        if(relativeDate.equals("yesterday")) {
            return "1d";
        }
        // hack to get twitter-like relative time format
        Iterable<String> tokens = Splitter.on(' ').split(relativeDate);
//        Log.d("DEBUG", tokens.toString());

        Iterator<String> iterator = tokens.iterator();
        String value = iterator.next();
        char unit = iterator.next().charAt(0);

        return value + unit;
    }
}
