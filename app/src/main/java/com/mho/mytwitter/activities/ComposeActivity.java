package com.mho.mytwitter.activities;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mho.mytwitter.R;
import com.mho.mytwitter.apps.TwitterApplication;
import com.mho.mytwitter.helpers.TwitterClient;
import com.mho.mytwitter.helpers.Utils;
import com.mho.mytwitter.models.Tweet;
import com.mho.mytwitter.models.User;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public class ComposeActivity extends ActionBarActivity {

    private static final int MAX_CHAR_COUNT = 140;
    private TwitterClient twitterClient;

    // custom action bar layout views
    private ImageView ivProfileImage;
    private TextView tvName;
    private TextView tvScreenName;

    // action view items
    private TextView tvCharsLeft;
    private Button btnTweet;

    // view
    private EditText etTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        twitterClient = TwitterApplication.getTwitterClient();

        tweakActionBar();
        setUpViews();
    }

    private void tweakActionBar() {
        /* To use custom actionBar Layout */
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.actionbar_compose);

        User user = TwitterApplication.getUser();

        // set up custom layout view
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvName = (TextView) findViewById(R.id.tvName);
        tvScreenName = (TextView) findViewById(R.id.tvScreenName);

        Picasso
                .with(this)
                .load(user.getProfileImageUrl())
                .fit()
                .centerCrop()
                .into(ivProfileImage);

        tvName.setText(user.getName());
        tvScreenName.setText(user.getScreenName());
    }

    private void setUpViews() {
        etTweet = (EditText) findViewById(R.id.etTweet);
        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // update chars count label
                int charsLeft = MAX_CHAR_COUNT - etTweet.getText().length();

                if (charsLeft < 0) {
                    tvCharsLeft.setTextColor(
                            getResources().getColor(R.color.twitter_over_char_limit));
                } else {
                    tvCharsLeft.setTextColor(getResources().getColor(R.color.twitter_normal_text));
                }

                btnTweet.setEnabled(charsLeft >= 0);
                btnTweet.setAlpha(charsLeft >= 0 ? 1 : (float) 0.4);
                tvCharsLeft.setText(String.valueOf(charsLeft));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.compose, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem actionViewItem = menu.findItem(R.id.action_tweet);
        View v = MenuItemCompat.getActionView(actionViewItem);
        btnTweet = (Button) v.findViewById(R.id.btnTweet);

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // disable button
                btnTweet.setEnabled(false);
                postTweet();
            }
        });

        actionViewItem = menu.findItem(R.id.action_chars_label);
        v = MenuItemCompat.getActionView(actionViewItem);
        tvCharsLeft = (TextView) v.findViewById(R.id.tvCharsLeft);

        return super.onPrepareOptionsMenu(menu);
    }

    public void postTweet() {
        Log.d("DEBUG", "calling send tweet");

        twitterClient.postTweet(etTweet.getText().toString(), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(JSONObject response) {
                notifyUser(getString(R.string.msg_send_success));

                Log.d("DEBUG", "successfully send tweet");
                Log.d("DEBUG", "response: " + response);

                Tweet tweet = Tweet.fromJsonObject(response);
                Log.d("DEBUG", "tweet: " + tweet);

                Intent data = new Intent();
                data.putExtra("tweet", tweet);
                setResult(RESULT_OK, data);

                // closes the activity, go back and pass data to Timeline activity
                finish();
            }

            @Override
            public void onFailure(Throwable error, String content) {
                if (Utils.isNetworkAvailable(getApplicationContext())) {
                    notifyUser(getString(R.string.msg_send_fail));
                } else {
                    notifyUser(getString(R.string.msg_network_unavailable));
                }
                Log.d("DEBUG", "error: " + error.toString());
                Log.d("DEBUG", "content: " + content);
            }
        });
    }

    private void notifyUser(String msg) {
        Crouton.showText(this, msg, Utils.STYLE);
    }
}
