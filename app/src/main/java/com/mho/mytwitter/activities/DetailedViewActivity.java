package com.mho.mytwitter.activities;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mho.mytwitter.R;
import com.mho.mytwitter.apps.TwitterApplication;
import com.mho.mytwitter.helpers.TwitterClient;
import com.mho.mytwitter.helpers.Utils;
import com.mho.mytwitter.models.Tweet;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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

public class DetailedViewActivity extends ActionBarActivity {

    private static final int MAX_CHAR_COUNT = 140;
    private static final String TAG = DetailedViewActivity.class.getSimpleName() + "_DEBUG";

    private ImageView ivProfileImage;
    private TextView tvName;
    private TextView tvScreenName;
    private TextView tvBody;
    private ImageView ivMedia;

    private TwitterClient twitterClient;
    private EditText etReply;
    private TextView tvCharsLeft;
    private Button btnTweet;

    private Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);

        twitterClient = TwitterApplication.getTwitterClient();
        tweet = getIntent().getParcelableExtra("tweet");

        setUpActionBar();
        setUpViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detailed_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpViews() {
        Log.d("DEBUG", "geronimo");

        setUpDetailedView();
        setUpReplyView();
    }

    private void setUpReplyView() {
        etReply = (EditText) findViewById(R.id.etReply);
        btnTweet = (Button) findViewById(R.id.btnTweet);
        tvCharsLeft = (TextView) findViewById(R.id.tvCharsLeft);

        etReply.setHint(getString(R.string.reply_hint) + tweet.getUser().getName());

        etReply.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String screenName = tweet.getUser().getScreenName();
                etReply.setText(screenName + " ");
                etReply.setSelection(screenName.length() + 1);
            }
        });

        etReply.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // update chars count label
                int charsLeft = MAX_CHAR_COUNT - etReply.getText().length();

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

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // disable button
                btnTweet.setEnabled(false);
                postTweet();
            }
        });
    }

    private void setUpDetailedView() {
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvName = (TextView) findViewById(R.id.tvName);
        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        tvBody = (TextView) findViewById(R.id.tvBody);
        ivMedia = (ImageView) findViewById(R.id.ivMedia);

        Log.d(TAG, tweet.toString());

        Picasso.with(this)
                .load(tweet.getUser().getProfileImageUrl())
                .into(ivProfileImage);

        tvName.setText(tweet.getUser().getName());
        tvScreenName.setText(tweet.getUser().getScreenName());
        tvBody.setText(tweet.getBody());

        if(!tweet.getMediaUrl().isEmpty()) {
            Log.d(TAG, "show media");
            Log.d(TAG, tweet.getMediaUrl().toString());
            ivMedia.setVisibility(View.VISIBLE);
            Picasso.with(this)
                    .load(tweet.getMediaUrl())
                    .into(ivMedia);
        }
    }


    public void postTweet() {
        Log.d("DEBUG", "calling send tweet");

        twitterClient.postTweet(etReply.getText().toString(), new JsonHttpResponseHandler() {

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
                    notifyUser(getString(R.string.msg_network_unavailble));
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
