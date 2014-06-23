package com.mho.mytwitter.activities;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mho.mytwitter.R;
import com.mho.mytwitter.helpers.TwitterApplication;
import com.mho.mytwitter.helpers.TwitterClient;
import com.mho.mytwitter.models.Tweet;

import org.json.JSONArray;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class TimelineActivity extends Activity {

    private TwitterClient twitterClient;
    private List<Tweet> tweets;
    private ArrayAdapter<Tweet> adapterTweets;
    private ListView lvTweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        twitterClient = TwitterApplication.getTwitterClient();

        lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<Tweet>();
        adapterTweets = new TweetArrayAdapter(this, tweets);
        lvTweets.setAdapter(adapterTweets);

        populatTimeline();
    }

    private void populatTimeline() {

        twitterClient.getHomeTimeline(
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONArray jsonArray) {

                        adapterTweets.addAll(Tweet.fromJsonArray(jsonArray));

                        Log.d("DEBUG", jsonArray.toString());
                    }

                    @Override
                    public void onFailure(Throwable throwable, String s) {
                        Log.d("DEBUG", throwable.toString());
                        Log.d("DEBUG", s);
                    }
                }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
