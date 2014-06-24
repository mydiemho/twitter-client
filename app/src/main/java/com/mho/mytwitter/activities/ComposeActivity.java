package com.mho.mytwitter.activities;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mho.mytwitter.R;
import com.mho.mytwitter.helpers.TwitterApplication;
import com.mho.mytwitter.helpers.TwitterClient;
import com.mho.mytwitter.helpers.Utils;
import com.mho.mytwitter.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public class ComposeActivity extends SherlockFragmentActivity {

    private static final int MAX_CHAR_COUNT = 140;
    private TwitterClient twitterClient;
    private EditText etTweet;
    private TextView tvCharsLeft;
    private Button btnTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        twitterClient = TwitterApplication.getTwitterClient();

        tweakActionBar();
        setUpViews();
    }

    private void tweakActionBar() {
        /* Custom ActionBar Layout */
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.actionbar_compose);

        ImageLoader imageLoader = ImageLoader.getInstance();
        // change action bar icon to be user's profile image
        User user = TwitterApplication.getUser();

//        try {
//            Drawable profileImage = TwitterApplication.drawableFromUrl(user.getProfileImageUrl());
//            getSupportActionBar().setIcon(profileImage);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // change action bar title to
        getSupportActionBar().setTitle(user.getName());
        getSupportActionBar().setSubtitle(user.getScreenName());
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xffffff));
    }

    private void setUpViews() {
        etTweet = (EditText) findViewById(R.id.etTweet);
//        tvCharsLeft = (TextView) findViewById(R.id.tvCharsLeft);
//        btnTweet = (Button) findViewById(R.id.btnTweet);
        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // update chars count label
                int charsLeft = MAX_CHAR_COUNT - etTweet.getText().length();
//                Log.d("DEBUG", String.valueOf(charsLeft));

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
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.compose, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem actionViewItem = menu.findItem(R.id.action_tweet);
        View v = actionViewItem.getActionView();
        btnTweet = (Button) v.findViewById(R.id.btnTweet);

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeTweet();
            }
        });

        actionViewItem = menu.findItem(R.id.action_chars_label);
        v = actionViewItem.getActionView();
        tvCharsLeft = (TextView) v.findViewById(R.id.tvCharsLeft);

        return super.onPrepareOptionsMenu(menu);
    }

    public void composeTweet() {
        Log.d("DEBUG", "calling send tweet");
        // jump back to TimelineActivity when done

        twitterClient.sendTweet(etTweet.getText().toString(), new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String content) {
                Log.d("DEBUG", "successfully send tweet");
                Log.d("DEBUG", "Content: " + content);

//                Tweet tweet = Tweet.fromJsonObject(response);
                notifyUser(getBaseContext(), getString(R.string.msg_send_success));
//                Intent intent = new Intent(getBaseContext(), TimelineActivity.class);
//                intent.putExtra("tweet", tweet);
//                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(Throwable error, String content) {
                if (Utils.isNetworkAvailable(getApplicationContext())) {
                    notifyUser(getBaseContext(), getString(R.string.msg_send_fail));
                } else {
                    notifyUser(getBaseContext(), getString(R.string.msg_network_unavailble));
                }
                Log.d("DEBUG", "error: " + error.toString());
                Log.d("DEBUG", "content: " + content);
            }
        });
    }

//    public void composeTweet() {
//        Log.d("DEBUG", "calling send tweet");
//        // jump back to TimelineActivity when done
//
//        twitterClient.sendTweet(etTweet.getText().toString(), new JsonHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(JSONObject response) {
//                Log.d("DEBUG", "successfully send tweet");
//                Log.d("DEBUG", response.toString());
//
//                Tweet tweet = Tweet.fromJsonObject(response);
//                notifyUser(getBaseContext(), getString(R.string.msg_send_success));
//                Intent intent = new Intent(getBaseContext(), TimelineActivity.class);
//                intent.putExtra("tweet", tweet);
//                setResult(RESULT_OK, intent);
//                finish();
//            }
//
//            @Override
//            public void onFailure(Throwable error, String content) {
//                if (Utils.isNetworkAvailable(getApplicationContext())) {
//                    notifyUser(getBaseContext(), getString(R.string.msg_send_fail));
//                } else {
//                    notifyUser(getBaseContext(), getString(R.string.msg_network_unavailble));
//                }
//                Log.d("DEBUG", error.toString());
//                Log.d("DEBUG", content);
//            }
//        });
//    }

    private void notifyUser(Context baseContext, String msg) {
        Crouton.showText(this, msg, Utils.STYLE);
    }
}
