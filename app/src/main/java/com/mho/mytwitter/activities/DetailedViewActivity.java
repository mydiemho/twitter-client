package com.mho.mytwitter.activities;

import com.mho.mytwitter.R;
import com.mho.mytwitter.models.Tweet;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailedViewActivity extends Activity {

    private ImageView ivProfileImage;
    private TextView tvName;
    private TextView tvScreenName;
    private TextView tvBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);

        setUpViews();
    }

    private void setUpViews() {
        Log.d("DEBUG", "geronimo");
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvName = (TextView) findViewById(R.id.tvName);
        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        tvBody = (TextView) findViewById(R.id.tvBody);

        Tweet tweet = getIntent().getParcelableExtra("tweet");
        Picasso.with(this)
                .load(tweet.getUser().getProfileImageUrl())
                .into(ivProfileImage);

        tvName.setText(tweet.getUser().getName());
        tvScreenName.setText(tweet.getUser().getScreenName());
        tvBody.setText(tweet.getBody());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detailed_view, menu);
        return true;
    }
}
