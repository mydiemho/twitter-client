package com.mho.mytwitter.activities;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mho.mytwitter.R;
import com.mho.mytwitter.adapters.TweetArrayAdapter;
import com.mho.mytwitter.helpers.EndlessScrollListener;
import com.mho.mytwitter.helpers.TwitterApplication;
import com.mho.mytwitter.helpers.TwitterClient;
import com.mho.mytwitter.helpers.Utils;
import com.mho.mytwitter.models.Tweet;

import org.json.JSONArray;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;


public class TimelineActivity extends SherlockFragmentActivity {

    private static final int MAX_RESULT_COUNT = 25;

    private TwitterClient twitterClient;
    private List<Tweet> tweets;
    private ArrayAdapter<Tweet> tweetsAdapter;
    private ListView lvTweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Request the action bar progress bar feature before setting content view
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_timeline);

        twitterClient = TwitterApplication.getTwitterClient();

        lvTweets = (ListView) findViewById(R.id.lvTweets);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView

                // get lowest id received from previous request,
                // subtract 1 to page through a timeline without receiving redundant Tweets
                long maxId = tweetsAdapter.getItem(totalItemsCount - 1).getTweetId() - 1;

                // don't care about new tweets, so use -1 for sinceId
                populateTimeline(MAX_RESULT_COUNT, -1, maxId);
            }
        });

        tweets = new ArrayList<Tweet>();
        tweetsAdapter = new TweetArrayAdapter(this, tweets);
        lvTweets.setAdapter(tweetsAdapter);

        // first request to a timeline endpoint should only specify a count
        populateTimeline(MAX_RESULT_COUNT, 0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML; this adds items to the action bar if it is present.
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.timeline, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private void populateTimeline(int count, long sinceId, long maxId) {

        if (!Utils.isNetworkAvailable(this)) {
            Crouton.makeText(this, getString(R.string.msg_network_unavailble), Utils.STYLE).show();
            return;
        }

        fetchTweets(count, sinceId, maxId);
    }

    private void fetchTweets(int count, long sinceId, long maxId) {
        setProgressBarIndeterminateVisibility(true);

        twitterClient.getHomeTimeline(
                count,
                sinceId,
                maxId,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONArray jsonArray) {
//                        Log.d("DEBUG", jsonArray.toString());
                        setProgressBarIndeterminateVisibility(false);

                        List<Tweet> newTweets = Tweet.fromJsonArray(jsonArray);
                        tweetsAdapter.addAll(newTweets);
                    }

                    @Override
                    public void onFailure(Throwable throwable, String s) {
                        Log.d("DEBUG", throwable.toString());
                        Log.d("DEBUG", s);
                    }
                }
        );
    }

    public void displayComposePanel(MenuItem item) {
        Intent i = new Intent(this, ComposeActivity.class);
        startActivityForResult(i, Utils.COMPOSE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("DEBUG", String.valueOf(requestCode));
//        if((requestCode == Utils.COMPOSE_REQUEST_CODE)) {
//            Tweet latestTweet = data.getParcelableExtra("tweet");
//            Log.d("DEBUG", latestTweet.toString());
//
//            // add tweet to top of container
//            tweets.add(0, latestTweet);
//            // show list at beginning
//            lvTweets.setSelection(0);
//
//            tweetsAdapter.notifyDataSetChanged();
//        }
    }
}
