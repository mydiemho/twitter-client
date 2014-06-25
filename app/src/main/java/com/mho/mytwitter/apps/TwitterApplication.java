package com.mho.mytwitter.apps;

import com.activeandroid.ActiveAndroid;
import com.mho.mytwitter.helpers.TwitterClient;
import com.mho.mytwitter.models.User;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.Context;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 * 
 *     RestClient client = RestClientApp.getTwitterClient();
 *     // use client to send requests to API
 *     
 */
public class TwitterApplication extends com.activeandroid.app.Application {

    private static Context context;
    private static User user;

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterApplication.context = this;

        // Create global configuration and initialize ImageLoader with this configuration
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
                cacheInMemory().cacheOnDisc().build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);

        ActiveAndroid.initialize(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        ActiveAndroid.dispose();
    }

    public static TwitterClient getTwitterClient() {
        return (TwitterClient) TwitterClient
                .getInstance(TwitterClient.class, TwitterApplication.context);
    }

    public static void setUser(User user)
    {
        TwitterApplication.user = user;
    }

    public static User getUser() {
        return TwitterApplication.user;
    }
}