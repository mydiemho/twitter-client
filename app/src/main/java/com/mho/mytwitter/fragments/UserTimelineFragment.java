package com.mho.mytwitter.fragments;

import com.mho.mytwitter.apps.TwitterApplication;

/**
 * Created by myho on 7/1/14.
 */
public class UserTimelineFragment extends TweetsListFragment {

    @Override
    protected void populateTimeline() {
        populateTimeline(-1, -1);
    }

    @Override
    protected void fetchTweets(long sinceId, long maxId) {
        TwitterApplication.getTwitterClient().getUserTimeline(getHandler(sinceId));
    }
}
