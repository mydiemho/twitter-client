package com.mho.mytwitter.activities;

import com.mho.mytwitter.R;
import com.mho.mytwitter.fragments.UserTimelineFragment;
import com.mho.mytwitter.models.User;
import com.squareup.picasso.Picasso;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends ActionBarActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName() + "_DEBUG";

    private ImageView mIvProfileImage;
    private TextView mTvName;
    private TextView mTvScreenName;
    private TextView mTvFollowersCount;
    private TextView mTvFriendsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setUpViews();
        loadProfileInfo();
        addUserTimelineFragment();
    }

    private void addUserTimelineFragment() {
        User user = getIntent().getParcelableExtra("user");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        UserTimelineFragment fragment = UserTimelineFragment.newInstance(user.getScreenName());
        ft.replace(R.id.flContainerUserTimeline, fragment);
        ft.commit();
    }

    private void setUpViews() {
        mIvProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        mTvName = (TextView) findViewById(R.id.tvName);
        mTvScreenName = (TextView) findViewById(R.id.tvScreenName);
        mTvFollowersCount = (TextView) findViewById(R.id.tvFollowersCount);
        mTvFriendsCount = (TextView) findViewById(R.id.tvFriendsCount);
    }

    private void loadProfileInfo() {
        User user = getIntent().getParcelableExtra("user");
        Log.d(TAG, "load profile info");
        Log.d(TAG, user.toString());

        Picasso.with(this)
                .load(user.getProfileImageUrl())
                .into(mIvProfileImage);

        mTvName.setText(user.getName());
        mTvScreenName.setText(user.getScreenName());
        mTvFollowersCount.setText(user.getFollowersCount() + " Followers");
        mTvFriendsCount.setText(user.getFriendsCount() + " Following");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
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
