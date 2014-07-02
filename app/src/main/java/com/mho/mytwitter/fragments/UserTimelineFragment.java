package com.mho.mytwitter.fragments;

import com.mho.mytwitter.apps.TwitterApplication;

import android.os.Bundle;
import android.util.Log;

/**
 * Created by myho on 7/1/14.
 */
public class UserTimelineFragment extends TweetsListFragment {

    private static final String TAG = UserTimelineFragment.class.getSimpleName() + "_DEBUG";
    private String mScreenName;

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment fragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mScreenName = getArguments().getString("screen_name");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void populateTimeline() {
        populateTimeline(-1, -1);
    }

    @Override
    protected void fetchTweets(long sinceId, long maxId) {
        Log.d(TAG, "mScreenName: " +  mScreenName);
        TwitterApplication.getTwitterClient().getUserTimeline(mScreenName, getHandler(sinceId));
    }
}
