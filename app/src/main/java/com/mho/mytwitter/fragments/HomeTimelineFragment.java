package com.mho.mytwitter.fragments;

import com.mho.mytwitter.apps.TwitterApplication;
import com.mho.mytwitter.helpers.Utils;
import com.mho.mytwitter.models.Tweet;

import java.util.List;

/**
 * Created by myho on 7/1/14.
 */
public class HomeTimelineFragment extends TweetsListFragment {

    @Override
    protected void populateTimeline() {
        List<Tweet> tweets = Tweet.getAll();

        // nothing in local db
        if (tweets.isEmpty()) {
            populateTimeline(-1, -1);
            return;
        }

        mAdapterTweets.addAll(tweets);

        // see if there's any new tweets since last access
        long sinceId = tweets.get(0).getTweetId() + 1;
        populateTimeline(sinceId, -1);
    }

    @Override
    protected void fetchTweets(long sinceId, long maxId) {
        showProgressBar();
        TwitterApplication.getTwitterClient().getHomeTimeline(
                Utils.MAX_RESULT_COUNT,
                sinceId,
                maxId,
                getHandler(sinceId)
        );
    }
}
