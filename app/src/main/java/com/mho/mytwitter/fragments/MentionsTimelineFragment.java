package com.mho.mytwitter.fragments;

import com.mho.mytwitter.apps.TwitterApplication;
import com.mho.mytwitter.helpers.Utils;

import android.os.Bundle;

/**
 * Created by myho on 7/1/14.
 */
public class MentionsTimelineFragment extends TweetsListFragment {

    // Store instance variables
    private String mTitle;
    private int mPage;

    public static MentionsTimelineFragment newInstance(String title, int page) {
        MentionsTimelineFragment fragment = new MentionsTimelineFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("page", page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mPage = getArguments().getInt("page");
        mTitle = getArguments().getString("title");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void populateTimeline() {
        populateTimeline(-1, -1);
    }

    @Override
    protected void fetchTweets(long sinceId, long maxId) {
        showProgressBar();
        TwitterApplication.getTwitterClient().getMentionsTimeline(
                Utils.MAX_RESULT_COUNT,
                sinceId,
                maxId,
                getHandler(sinceId)
        );
    }
}
