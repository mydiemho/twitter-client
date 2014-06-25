package com.mho.mytwitter.activities;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.activeandroid.ActiveAndroid;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mho.mytwitter.R;
import com.mho.mytwitter.adapters.TweetArrayAdapter;
import com.mho.mytwitter.apps.TwitterApplication;
import com.mho.mytwitter.fragments.ComposeDiaglog;
import com.mho.mytwitter.helpers.CustomisedHeaderTransformer;
import com.mho.mytwitter.helpers.EndlessScrollListener;
import com.mho.mytwitter.helpers.TwitterClient;
import com.mho.mytwitter.helpers.Utils;
import com.mho.mytwitter.models.Tweet;

import org.json.JSONArray;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;


public class TimelineActivity extends SherlockFragmentActivity
        implements ComposeDiaglog.ComposeDialogListener {

    private static final int MAX_RESULT_COUNT = 25;
    private static final String TAG = TimelineActivity.class.getSimpleName() + "DEBUG";

    private TwitterClient twitterClient;
    private List<Tweet> tweets;
    private ArrayAdapter<Tweet> tweetsAdapter;
    private ListView lvTweets;

    private PullToRefreshLayout mPullToRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Request the action bar progress bar feature before setting content view
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_timeline);

        twitterClient = TwitterApplication.getTwitterClient();

        setUpPullToRefresh();
        setUpViews();
    }

    private void populateTimeline(int maxResultCount) {
        populateTimeline(MAX_RESULT_COUNT, -1, -1);
    }

    private void setUpPullToRefresh() {
        // Now find the PullToRefreshLayout to setup
        mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.timeline_layout);

        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(this)
                .options(Options.create()
                        // Here we make the refresh scroll distance to 75% of the GridView height
                        .scrollDistance(.75f)
                                // Here we define a custom header layout which will be inflated and used
                        .headerLayout(R.layout.customised_header)
                                // Here we define a custom header transformer which will alter the header
                                // based on the current pull-to-refresh state
                        .headerTransformer(new CustomisedHeaderTransformer())
                        .build())
                        // Mark All Children as pullable
                .allChildrenArePullable()
                        // Set a OnRefreshListener
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {

                        Log.d("DEBUG", "refresh started");
                        long sinceId = -1;

                        // not a first request
                        if (!tweets.isEmpty()) {
                            sinceId = tweets.get(0).getTweetId();
                        }

                        // for refresh, maxId doesn't matter
                        populateTimeline(MAX_RESULT_COUNT, sinceId, -1);
                    }
                })
                        // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);
    }

    private void setUpViews() {
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<Tweet>();
        tweetsAdapter = new TweetArrayAdapter(this, tweets);
        lvTweets.setAdapter(tweetsAdapter);

        setUpInfiniteScroll();
        setUpDisplayDetailedView();
        loadTweets();
    }

    private void loadTweets() {
        List<Tweet> tweets = Tweet.getAll();

        // only fetch new tweets if we have exhausted local db
        if(tweets.isEmpty()) {
            Log.d(TAG, "nothing in db");
            // first request to a timeline endpoint should only specify a count
            populateTimeline(MAX_RESULT_COUNT);
            return;
        }

        Log.d(TAG, "loading tweets from db");
        for(Tweet tweet : tweets){
            Log.d(TAG, "loaded tweet: " + tweet.getTweetId());
        }
        Log.d(TAG, "db size: " + tweets.size());
        tweetsAdapter.addAll(tweets);


    }

    private void setUpDisplayDetailedView() {
        lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("DEBUG", "item click");
                Intent intent = new Intent(getApplicationContext(), DetailedViewActivity.class);
                Tweet tweet = tweets.get(position);
                intent.putExtra("tweet", tweet);

                Log.d("DEBUG", tweet.toString());
                // fire intent
                startActivity(intent);
            }
        });
    }

    private void setUpInfiniteScroll() {
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView

                // only fetch new data if we have reached the end of local database
                Log.d(TAG, "infinite scroll");
                Log.d(TAG, "totalItemsCount: " + totalItemsCount);
                Log.d(TAG, "array size: " + tweets.size());
                if (totalItemsCount >= tweets.size()) {
                    // get lowest id received from previous request,
                    // subtract 1 to page through a timeline without receiving redundant Tweets
                    long maxId = tweetsAdapter.getItem(totalItemsCount - 1).getTweetId() - 1;

                    // don't care about new tweets, so use -1 for sinceId
                    populateTimeline(MAX_RESULT_COUNT, -1, maxId);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML; this adds items to the action bar if it is present.
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.timeline, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void populateTimeline(int count, long sinceId, long maxId) {

        if (!Utils.isNetworkAvailable(this)) {
            notifyUser(getString(R.string.msg_network_unavailble));
            return;
        }

        fetchTweets(count, sinceId, maxId);
    }

    private void fetchTweets(int count, final long sinceId, long maxId) {
        setProgressBarIndeterminateVisibility(true);

        twitterClient.getHomeTimeline(
                count,
                sinceId,
                maxId,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONArray jsonArray) {
                        setProgressBarIndeterminateVisibility(false);

                        List<Tweet> newTweets = Tweet.fromJsonArray(jsonArray);
                        Log.d(TAG, "fetched item count: " + newTweets.size());

                        // save tweets to db
                        ActiveAndroid.beginTransaction();
                        try {
                            for (Tweet newTweet : newTweets) {
                                newTweet.getUser().save();
                                newTweet.save();

                                Log.d(TAG, "saving tweet: " + newTweet.getTweetId());
                            }
                            ActiveAndroid.setTransactionSuccessful();
                        } finally {
                            ActiveAndroid.endTransaction();
                            Log.d(TAG, "saved to db");
                        }

                        List<Tweet> tweets = Tweet.getAll();
                        Log.d(TAG, "db size: " + tweets.size());

                        // infinite scroll and not adding new tweets to top
                        if (sinceId < 0) {
                            Log.d(TAG, "infinite scroll");
                            tweetsAdapter.addAll(newTweets);
                        } else {  // refresh
                            Log.d(TAG, "refreshing");

                            // save all new tweets to top of list
                            tweets.addAll(0, newTweets);
                            tweetsAdapter.notifyDataSetChanged();
                        }

                        // Notify PullToRefreshLayout that the refresh has finished
                        mPullToRefreshLayout.setRefreshComplete();
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
                }
        );
    }

    private void notifyUser(String msg) {
        Crouton.makeText(this, msg, Utils.STYLE).show();
    }

    public void displayComposePanel(MenuItem item) {
        Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
        startActivityForResult(i, Utils.COMPOSE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("DEBUG", "Back to timeline: " + String.valueOf(requestCode));
        if ((requestCode == Utils.COMPOSE_REQUEST_CODE) && (resultCode == RESULT_OK)) {
            Tweet latestTweet = data.getParcelableExtra("tweet");

            // persist data
            latestTweet.getUser().save();
            latestTweet.save();

            Log.d("DEBUG", "latestTweet: " + latestTweet.toString());

            // add tweet to top of container
            tweets.add(0, latestTweet);
            // show list at beginning
            lvTweets.setSelection(0);

            tweetsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFinishTweet(Tweet newTweet) {
        // post new tweet to top of timeline
        Log.d("DEBUG", newTweet.toString());

        // add tweet to top of container
        tweets.add(0, newTweet);
        tweetsAdapter.notifyDataSetChanged();

        // show list at beginning
        lvTweets.setSelection(0);
    }
}
