package com.mho.mytwitter.fragments;

import com.activeandroid.ActiveAndroid;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mho.mytwitter.R;
import com.mho.mytwitter.activities.DetailedViewActivity;
import com.mho.mytwitter.adapters.TweetArrayAdapter;
import com.mho.mytwitter.helpers.EndlessScrollListener;
import com.mho.mytwitter.helpers.Utils;
import com.mho.mytwitter.models.Tweet;

import org.json.JSONArray;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public abstract class TweetsListFragment extends Fragment {

    private static final String TAG = TweetsListFragment.class.getSimpleName() + "_DEBUG";

    private List<Tweet> mTweets;
    protected ArrayAdapter<Tweet> mAdapterTweets;
    private ListView mListViewTweets;
    private PullToRefreshLayout mPullToRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // non-view initialization
        mTweets = new ArrayList<Tweet>();
        mAdapterTweets = new TweetArrayAdapter(getActivity(), mTweets);

        Log.d(TAG, "onCreate");
        populateTimeline();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // false = don't attach to container yet
        View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);

        // assign view references
        setUpViews(view);

        return view;
    }

    public void postNewTweetToTop(Tweet tweet) {

        // add tweet to top of container
        mTweets.add(0, tweet);
        // show list at beginning
        mListViewTweets.setSelection(0);

        mAdapterTweets.notifyDataSetChanged();
    }

    private void setUpViews(View view) {
        mListViewTweets = (ListView) view.findViewById(R.id.lvTweets);
        mListViewTweets.setAdapter(mAdapterTweets);

        mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_timeline_layout);

        setUpInfiniteScroll();
        setUpDisplayDetailedView();
        setUpPullToRefresh();
    }

    private void setUpPullToRefresh() {

        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(getActivity())
                .allChildrenArePullable()
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {

                        long sinceId = -1;

                        // not a first request
                        if (!mTweets.isEmpty()) {
                            // needs to add 1, since_id returns inclusive results
                            sinceId = mTweets.get(0).getTweetId() + 1;
                        }

                        // for refresh, maxId doesn't matter
                        populateTimeline(sinceId, -1);
                    }
                })
                .setup(mPullToRefreshLayout);// Finally commit the setup to our PullToRefreshLayout
    }

    private void setUpDisplayDetailedView() {
        mListViewTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("DEBUG", "item click");
                Intent intent = new Intent(getActivity(), DetailedViewActivity.class);
                Tweet tweet = mTweets.get(position);
                intent.putExtra("tweet", tweet);

                Log.d("DEBUG", tweet.toString());
                // fire intent
                startActivity(intent);
            }
        });
    }

    private void setUpInfiniteScroll() {
        mListViewTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView

                // only fetch new data if we have reached the end of local database
                Log.d(TAG, "infinite scroll");
                if (totalItemsCount >= mTweets.size()) {
                    // get lowest id received from previous request,
                    // subtract 1 to page through a timeline without receiving redundant Tweets
                    long maxId = mAdapterTweets.getItem(totalItemsCount - 1).getTweetId() - 1;

                    // don't care about new tweets, so use -1 for sinceId
                    populateTimeline(-1, maxId);
                }
            }
        });
    }

    protected void populateTimeline(long sinceId, long maxId) {

        if (!Utils.isNetworkAvailable(getActivity())) {
            notifyUser(getString(R.string.msg_network_unavailable));
            return;
        }

        fetchTweets(sinceId, maxId);
    }

    protected abstract void populateTimeline();

    protected abstract void fetchTweets(final long sinceId, long maxId);

    protected JsonHttpResponseHandler getHandler(final long sinceId) {
        return new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(JSONArray jsonArray) {

                List<Tweet> newTweets = Tweet.fromJsonArray(jsonArray);
                Log.d(TAG, "fetched item count: " + newTweets.size());
                Log.d(TAG, newTweets.toString());

                // save tweets to db
                saveTweets(newTweets);

                Log.d(TAG, "db size: " + Tweet.getAll().size());

                // infinite scroll and not adding new tweets to top
                if (sinceId < 0) {
                    Log.d(TAG, "fetch: infinite scroll");
                    mAdapterTweets.addAll(newTweets);
                } else {  // refresh
                    Log.d(TAG, "fetch: refreshing");

                    // save all new tweets to top of list
                    mTweets.addAll(0, newTweets);
                    mAdapterTweets.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String s) {
                Log.d(TAG, throwable.toString());
                Log.d(TAG, s);
            }

            @Override
            protected void handleFailureMessage(Throwable e, String responseBody) {
                // do something when
                Log.d(TAG, e.toString());
                Log.d(TAG, responseBody);

                notifyUser(getString(R.string.exceed_rate_limit));
            }

            @Override
            public void onFinish() {

                mPullToRefreshLayout.setRefreshComplete();
                super.onFinish();
            }
        };
    }

    private void saveTweets(List<Tweet> newTweets) {
        ActiveAndroid.beginTransaction();
        try {
            for (Tweet newTweet : newTweets) {
                newTweet.getUser().save();
                newTweet.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    private void notifyUser(String msg) {
        Crouton.makeText(getActivity(), msg, Utils.STYLE).show();
    }
}
