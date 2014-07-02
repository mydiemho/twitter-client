package com.mho.mytwitter.activities;

import com.mho.mytwitter.R;
import com.mho.mytwitter.fragments.UserTimelineFragment;
import com.mho.mytwitter.models.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProfileActivity extends ActionBarActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName() + "_DEBUG";

    private ImageView mIvProfileImage;
    private TextView mTvName;
    private TextView mTvScreenName;
    private TextView mTvFollowersCount;
    private TextView mTvFriendsCount;
    private LinearLayout mLlProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // MUST request the feature before setting content view
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_profile);

        setUpViews();
        loadProfileInfo();
        addUserTimelineFragment();
    }

    // Should be called manually when an async task has started
    public void showProgressBar() {
        setProgressBarIndeterminateVisibility(true);
    }

    // Should be called when an async task has finished
    public void hideProgressBar() {
        setProgressBarIndeterminateVisibility(false);
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
        mLlProfile = (LinearLayout) findViewById(R.id.llProfile);
    }

    private void loadProfileInfo() {
        showProgressBar();
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

        String profileBannerUrl = user.getProfileBannerUrl();

        Log.d(TAG, "show banner");
        if(!profileBannerUrl.isEmpty()) {

            // tranfer url to get mobile image
            profileBannerUrl = profileBannerUrl + "/mobile";

            Log.d(TAG, profileBannerUrl.toString());
            Picasso.with(this)
                    .load(user.getProfileBannerUrl())
                    .placeholder(new ColorDrawable(R.color.profile_banner_background))
                    .error(new ColorDrawable(R.color.profile_banner_background))
                    .centerCrop()
                    .resize(320, 160)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            mLlProfile.setBackgroundDrawable(new BitmapDrawable(bitmap));
                            hideProgressBar();
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            mLlProfile.setBackgroundDrawable(errorDrawable);
                            hideProgressBar();
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            Log.d(TAG, "loading banner");
                            mLlProfile.setBackgroundDrawable(placeHolderDrawable);
                        }
                    });
        } else {
            Log.d(TAG, "banner is null");
            mLlProfile.setBackgroundDrawable(new ColorDrawable(R.color.profile_banner_background));
        }
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
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
