package com.mho.mytwitter.fragments;

import com.mho.mytwitter.apps.TwitterApplication;
import com.mho.mytwitter.helpers.Utils;

/**
 * Created by myho on 7/1/14.
 */
public class MentionsTimelineFragment extends TweetsListFragment {

    @Override
    protected void populateTimeline() {
        populateTimeline(-1, -1);
    }

    @Override
    protected void fetchTweets(long sinceId, long maxId) {
        TwitterApplication.getTwitterClient().getMentionsTimeline(
                Utils.MAX_RESULT_COUNT,
                sinceId,
                maxId,
                getHandler(sinceId)
        );
    }
}
